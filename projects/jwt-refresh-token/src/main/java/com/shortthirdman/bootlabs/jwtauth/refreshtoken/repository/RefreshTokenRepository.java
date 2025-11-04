package com.shortthirdman.bootlabs.jwtauth.refreshtoken.repository;

import com.shortthirdman.bootlabs.jwtauth.refreshtoken.model.RefreshToken;
import com.shortthirdman.bootlabs.jwtauth.refreshtoken.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    @Modifying
    int deleteByUser(User user);
}
