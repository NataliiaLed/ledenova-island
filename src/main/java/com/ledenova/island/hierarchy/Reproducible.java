package com.ledenova.island.hierarchy;

public interface Reproducible {
    void reproduce();
    boolean canReproduce();
    void setReproduced(boolean value);
    boolean reproduced();
}
