package com.ledenova.island.util;

import com.ledenova.island.config.OrganismClass;

import java.util.concurrent.atomic.AtomicInteger;

public class OrganismNameGenerator {
    private final AtomicInteger counter = new AtomicInteger();

    public String generateName(OrganismClass organismClass) {
        return String.format("%s_%d", organismClass.name(), counter.getAndIncrement());
    }
}
