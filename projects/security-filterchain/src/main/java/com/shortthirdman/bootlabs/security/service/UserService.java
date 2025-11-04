package com.shortthirdman.bootlabs.security.service;

import com.shortthirdman.bootlabs.security.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    List<User> getUsers();

    User save(User user);
}
