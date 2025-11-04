package com.shortthirdman.bootlabs.jwtauth.refreshtoken.repository;

import com.shortthirdman.bootlabs.jwtauth.refreshtoken.model.Role;
import com.shortthirdman.bootlabs.jwtauth.refreshtoken.model.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleType name);
}
