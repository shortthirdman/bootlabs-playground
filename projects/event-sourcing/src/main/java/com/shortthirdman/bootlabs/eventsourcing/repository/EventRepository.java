package com.shortthirdman.bootlabs.eventsourcing.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class EventRepository {

    private final DSLContext dsl;

    public void saveEvent(UUID id, UUID aggregateId, String eventType, byte[] eventData, Instant timestamp) {
        dsl.insertInto(EVENT_STORE)
                .set(EVENT_STORE.ID, id)
                .set(EVENT_STORE.AGGREGATE_ID, aggregateId)
                .set(EVENT_STORE.EVENT_TYPE, eventType)
                .set(EVENT_STORE.EVENT_DATA, eventData)
                .set(EVENT_STORE.TIMESTAMP, timestamp)
                .execute();
    }

    public List<EventStoreRecord> findByAggregateId(UUID aggregateId) {
        return dsl.selectFrom(EVENT_STORE)
                .where(EVENT_STORE.AGGREGATE_ID.eq(aggregateId))
                .orderBy(EVENT_STORE.SEQUENCE_NUMBER.asc())
                .fetch();
    }

    public List<EventStoreRecord> findByEventType(String eventType) {
        return dsl.selectFrom(EVENT_STORE)
                .where(EVENT_STORE.EVENT_TYPE.eq(eventType))
                .orderBy(EVENT_STORE.TIMESTAMP.asc())
                .fetch();
    }

    public List<EventStoreRecord> findByAggregateIdAndEventType(UUID aggregateId, String eventType) {
        return dsl.selectFrom(EVENT_STORE)
                .where(EVENT_STORE.AGGREGATE_ID.eq(aggregateId))
                .and(EVENT_STORE.EVENT_TYPE.eq(eventType))
                .orderBy(EVENT_STORE.SEQUENCE_NUMBER.asc())
                .fetch();
    }

    public List<EventStoreRecord> findAllOrderByTimestamp() {
        return dsl.selectFrom(EVENT_STORE)
                .orderBy(EVENT_STORE.TIMESTAMP.asc())
                .fetch();
    }
}
