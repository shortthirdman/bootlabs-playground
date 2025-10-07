package com.shortthirdman.bootlabs.springai.streaming.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class ChatRequest {

    @NotNull
    private String prompt;
}
