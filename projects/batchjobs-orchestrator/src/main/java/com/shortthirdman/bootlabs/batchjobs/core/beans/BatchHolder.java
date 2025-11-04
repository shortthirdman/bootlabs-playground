package com.shortthirdman.bootlabs.batchjobs.core.beans;

import com.shortthirdman.bootlabs.batchjobs.core.annotations.BatchDefinition;

import java.lang.reflect.Method;

public record BatchHolder(Object bean,
                          BatchDefinition definition,
                          Method entryPoint) {
}
