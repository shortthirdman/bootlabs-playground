package com.shortthirdman.bootlabs.eventsourcing.service;

import com.shortthirdman.bootlabs.eventsourcing.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public void saveAndPublishEvent(SpecificRecord event) {
        try {
            // Extract base event information
            BaseEvent baseEvent = extractBaseEvent(event);
            UUID eventId = UUID.fromString(baseEvent.getEventId());
            UUID aggregateId = UUID.fromString(baseEvent.getAggregateId());
            String eventType = event.getClass().getSimpleName();
            Instant timestamp = Instant.ofEpochMilli(baseEvent.getTimestamp().toEpochMilli());

            // Serialize event for storage
            byte[] serializedEvent = serializeEvent(event);

            // Save to event store
            eventRepository.saveEvent(eventId, aggregateId, eventType, serializedEvent, timestamp);

            // Publish to Kafka
            kafkaTemplate.send("order-events", aggregateId.toString(), event);

            log.info("Event {} saved and published for aggregate {}", eventType, aggregateId);
        } catch (Exception e) {
            log.error("Error saving and publishing event", e);
            throw new RuntimeException("Error processing event", e);
        }
    }

    @Transactional(readOnly = true)
    public List<SpecificRecord> getEvents(UUID aggregateId) {
        return eventRepository.findByAggregateId(aggregateId)
                .stream()
                .map(this::deserializeEvent)
                .collect(Collectors.toList());
    }

    private BaseEvent extractBaseEvent(SpecificRecord event) {
        try {
            // This assumes each event has a 'base' field that is a BaseEvent
            return (BaseEvent) event.get("base");
        } catch (Exception e) {
            throw new RuntimeException("Error extracting base event", e);
        }
    }

    private byte[] serializeEvent(SpecificRecord event) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);
            DatumWriter<SpecificRecord> writer = new SpecificDatumWriter<>(event.getSchema());
            writer.write(event, encoder);
            encoder.flush();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error serializing event", e);
        }
    }

    private SpecificRecord deserializeEvent(EventStoreRecord record) {
        try {
            // Determine the event class based on the stored event type
            Class<? extends SpecificRecord> eventClass = getEventClass(record.getEventType());

            // Create a reader for this specific event type
            SpecificDatumReader<? extends SpecificRecord> reader = new SpecificDatumReader<>(eventClass);

            // Deserialize the event data
            return reader.read(null, DecoderFactory.get().binaryDecoder(record.getEventData(), null));
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing event", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Class<? extends SpecificRecord> getEventClass(String eventType) {
        try {
            return (Class<? extends SpecificRecord>) Class.forName("com.shortthirdman.springboot.eventsourcing.event." + eventType);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Event type not found: " + eventType, e);
        }
    }
}
