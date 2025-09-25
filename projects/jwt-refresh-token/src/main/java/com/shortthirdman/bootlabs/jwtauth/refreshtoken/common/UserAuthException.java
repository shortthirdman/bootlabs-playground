package com.shortthirdman.bootlabs.jwtauth.refreshtoken.common;

public class UserAuthException extends RuntimeException {
    public UserAuthException(String message) {
        super(message);
    }
}
