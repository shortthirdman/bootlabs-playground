package com.shortthirdman.bootlabs.security.repository;

import com.shortthirdman.springboot.security.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM app_user u WHERE u.username = :username AND u.enabled = true")
    Optional<User> findActiveByUsername(@Param("username") String username);
}
