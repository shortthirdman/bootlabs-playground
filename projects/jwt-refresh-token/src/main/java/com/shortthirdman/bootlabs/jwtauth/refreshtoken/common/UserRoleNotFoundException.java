package com.shortthirdman.bootlabs.jwtauth.refreshtoken.common;

public class UserRoleNotFoundException extends RuntimeException {
    public UserRoleNotFoundException(String message) {
        super(message);
    }
}
