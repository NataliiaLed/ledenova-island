package com.ledenova.island.config;

public class Characteristics {
    private final OrganismClass organismClass;
    private final double weight;
    private final int maxPopulation;
    private final int speed;
    private final double maxSaturation;
    private final String icon;

    public Characteristics(OrganismClass organismClass, double weight, int maxPopulation, int speed, double maxSaturation, String icon) {
        this.organismClass = organismClass;
        this.weight = weight;
        this.maxPopulation = maxPopulation;
        this.speed = speed;
        this.maxSaturation = maxSaturation;
        this.icon = icon;
    }

    public OrganismClass getOrganismClass() {
        return organismClass;
    }

    public double getWeight() {
        return weight;
    }

    public int getMaxPopulation() {
        return maxPopulation;
    }

    public int getSpeed() {
        return speed;
    }

    public double getMaxSaturation() {
        return maxSaturation;
    }

    public String getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        return "Characteristics{" +
                "organismClass=" + organismClass +
                ", weight=" + weight +
                ", maxPopulation=" + maxPopulation +
                ", speed=" + speed +
                ", maxSaturation=" + maxSaturation +
                ", icon=" + icon +
                '}';
    }
}
