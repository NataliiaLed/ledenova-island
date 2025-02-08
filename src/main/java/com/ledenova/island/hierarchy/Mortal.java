package com.ledenova.island.hierarchy;

public interface Mortal {
    void die(CauseOfDeath causeOfDeath);
    boolean isAlive();
}
