package com.shortthirdman.bootlabs.springai.streaming;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@EnableWebFlux
@SpringBootApplication
public class ChatClientStreamsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatClientStreamsApplication.class, args);
	}

}
