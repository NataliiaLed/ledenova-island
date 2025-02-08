package com.ledenova.island.config;

import java.util.Map;

public class Configuration {
    private final int boardHeight;
    private final int boardWidth;
    private final int maxTurns;
    private final int schedulePeriodSec;
    private final int awaitTerminationMin;
    private final Map<OrganismClass, Characteristics> characteristicsMap;
    private final Map<OrganismClass, Integer> initialPopulation;
    private final Map<OrganismClass, Map<OrganismClass, Integer>> probabilitiesToEat;
    public Configuration(int boardHeight, int boardWidth, int maxTurns, int schedulePeriodSec, int awaitTerminationMin, Map<OrganismClass, Characteristics> characteristicsMap, Map<OrganismClass, Integer> initialPopulation, Map<OrganismClass, Map<OrganismClass, Integer>> probabilitiesToEat) {
        this.boardHeight = boardHeight;
        this.boardWidth = boardWidth;
        this.maxTurns = maxTurns;
        this.schedulePeriodSec = schedulePeriodSec;
        this.awaitTerminationMin = awaitTerminationMin;
        this.characteristicsMap = characteristicsMap;
        this.initialPopulation = initialPopulation;
        this.probabilitiesToEat = probabilitiesToEat;
    }

    public int getBoardHeight() {
        return boardHeight;
    }

    public int getBoardWidth() {
        return boardWidth;
    }

    public Map<OrganismClass, Characteristics> getCharacteristicsMap() {
        return characteristicsMap;
    }

    public Map<OrganismClass, Integer> getInitialPopulation() {
        return initialPopulation;
    }

    public Map<OrganismClass, Map<OrganismClass, Integer>> getProbabilitiesToEat() {
        return probabilitiesToEat;
    }

    public int getMaxTurns() {
        return maxTurns;
    }

    public int getSchedulePeriodSec() {
        return schedulePeriodSec;
    }

    public int getAwaitTerminationMin() {
        return awaitTerminationMin;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "boardHeight=" + boardHeight +
                ", boardWidth=" + boardWidth +
                ", characteristicsMap=" + characteristicsMap +
                ", initialPopulation=" + initialPopulation +
                ", probabilitiesToEat=" + probabilitiesToEat +
                '}';
    }
}
