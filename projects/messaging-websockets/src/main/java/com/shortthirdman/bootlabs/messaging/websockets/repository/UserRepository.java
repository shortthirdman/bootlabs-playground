package com.shortthirdman.bootlabs.messaging.websockets.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository {
    Object findByUsername(String username);
}
