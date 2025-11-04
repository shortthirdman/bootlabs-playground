package com.shortthirdman.bootlabs.security.repository;

import com.shortthirdman.bootlabs.security.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
