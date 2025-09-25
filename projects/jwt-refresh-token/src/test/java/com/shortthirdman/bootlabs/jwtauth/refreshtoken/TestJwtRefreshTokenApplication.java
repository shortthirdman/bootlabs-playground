package com.shortthirdman.bootlabs.jwtauth.refreshtoken;

import org.springframework.boot.SpringApplication;

public class TestJwtRefreshTokenApplication {

	public static void main(String[] args) {
		SpringApplication.from(JwtRefreshTokenApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
