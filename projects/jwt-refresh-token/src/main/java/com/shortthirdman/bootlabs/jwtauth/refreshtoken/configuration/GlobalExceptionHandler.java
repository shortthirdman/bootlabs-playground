package com.shortthirdman.bootlabs.jwtauth.refreshtoken.configuration;

import com.shortthirdman.bootlabs.jwtauth.refreshtoken.common.TokenRefreshException;
import com.shortthirdman.bootlabs.jwtauth.refreshtoken.common.UserAuthException;
import com.shortthirdman.bootlabs.jwtauth.refreshtoken.common.UserRoleNotFoundException;
import com.shortthirdman.bootlabs.jwtauth.refreshtoken.dto.response.MessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = TokenRefreshException.class)
    public ResponseEntity<MessageResponse> handleTokenRefreshError(TokenRefreshException tre) {
        return ResponseEntity.badRequest().body(new MessageResponse(tre.getMessage()));
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<MessageResponse> handleException(Exception e) {
        return ResponseEntity.internalServerError().body(new MessageResponse(e.getMessage()));
    }

    @ExceptionHandler(value = UserAuthException.class)
    public ResponseEntity<MessageResponse> handleUserAuthException(UserAuthException uae) {
        return ResponseEntity.badRequest().body(new MessageResponse(uae.getMessage()));
    }

    @ExceptionHandler(value = UserRoleNotFoundException.class)
    public ResponseEntity<MessageResponse> handleUserRoleNotFoundException(UserRoleNotFoundException urnf) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(urnf.getMessage()));
    }
}
