package com.shortthirdman.bootlabs.security.model;

import org.springframework.lang.NonNull;

public record Login(@NonNull String username, @NonNull String password) {
}
