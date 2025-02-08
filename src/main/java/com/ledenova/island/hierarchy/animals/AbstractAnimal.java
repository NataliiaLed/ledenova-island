package com.ledenova.island.hierarchy.animals;

import com.ledenova.island.game.Board;
import com.ledenova.island.game.Location;
import com.ledenova.island.config.Configuration;
import com.ledenova.island.config.OrganismClass;
import com.ledenova.island.hierarchy.*;

import java.util.Map;

import static com.ledenova.island.hierarchy.CauseOfDeath.*;

public class AbstractAnimal extends AbstractOrganism implements Traveler, Eater {
    private static final int SATURATION_PERCENTAGE_TO_BE_ALIVE = 50;
    private static final int SATURATION_PERCENTAGE_TO_REPRODUCE = 100;

    private final int speed;
    private final double maxSaturation;

    private double currentSaturation = 0;

    private final Map<OrganismClass, Integer> probabilityToEat;

    public AbstractAnimal(Configuration configuration, String name, OrganismClass organismClass, Board board, OrganismFactory organismFactory) {
        super(name,
                organismClass,
                configuration.getCharacteristicsMap().get(organismClass).getWeight(),
                configuration.getCharacteristicsMap().get(organismClass).getIcon(),
                board,
                organismFactory
        );
        this.speed = configuration.getCharacteristicsMap().get(organismClass).getSpeed();
        this.maxSaturation = configuration.getCharacteristicsMap().get(organismClass).getMaxSaturation();
        this.probabilityToEat = configuration.getProbabilitiesToEat().get(organismClass);
        recordEvent("I am born");
    }

    @Override
    public void makeTurn() {
        if (!isAlive()) {
            recordEvent("I am dead. Can't make my turn");
            return;
        }
        recordEvent("Making its turn");
        reset();
        move();
        eat();
        reproduce();
        dieIfStarving();
    }

    private void dieIfStarving() {
        if (isStarving()) {
            die(STARVATION);
        }
    }

    private void reset() {
        currentSaturation = 0;
        setReproduced(false);
    }

    @Override
    public void reproduce() {
        if (!isAlive()) {
            recordEvent("I am dead. Can't reproduce");
            return;
        }
        recordEvent("Trying to reproduce");
        if (!canReproduce()) {
            recordEvent("Can not reproduce");
            return;
        }
        getBoard().forEachOrganismOfClassWhile(getLocation(), getOrganismClass(), this::tryToReproduce, this::canReproduce);
    }

    private void tryToReproduce(Organism other) {
        if (other.canReproduce() && this.canReproduce() && other != this) {
            recordEvent(String.format("Breeding with %s", other));
            setReproduced(true);
            other.setReproduced(true);
            Organism child = organismFactory.createOrganism(getOrganismClass());
            child.setLocation(getLocation());
            boolean successfullyPopulated = getBoard().tryToPopulateCell(child, getLocation());
            if (!successfullyPopulated) {
                child.die(NO_SPACE_TO_LIVE);
            }
        }
    }

    @Override
    public boolean canReproduce() {
        boolean enoughSaturation = maxSaturation * 0.01 * SATURATION_PERCENTAGE_TO_REPRODUCE - currentSaturation <= 0.001;
        return isAlive() && enoughSaturation && !reproduced();
    }

    @Override
    public void move() {
        if (!isAlive()) {
            recordEvent("I am dead. Can't move");
            return;
        }
        for (int i = 0; i < speed; i++) {
            tryToMoveSomewhere();
        }
    }

    private void tryToMoveSomewhere() {
        if (!isAlive()) {
            recordEvent("I am dead. Can't move");
            return;
        }
        Location currentLocation = getLocation();
        Location newLocation = getRandomNeighboringLocation(currentLocation);
        recordEvent(String.format("Trying to move from %s to %s", currentLocation, newLocation));
        if (getBoard().moveOrganism(this, newLocation)) {
            recordEvent(String.format("Successfully moved from %s to %s", currentLocation, newLocation));
        }
    }

    @Override
    public void eat() {
        if (!isAlive()) {
            recordEvent("I am dead. Can't eat");
            return;
        }
        recordEvent("Trying to eat something");
        getBoard().forEachOrganismWhile(getLocation(), this::tryToEat, this::canEat);
    }

    private void tryToEat(Organism other) {
        if (other.equals(this)) {
            return;
        }
        OrganismClass organismClass = other.getOrganismClass();
        Integer probability = probabilityToEat.get(organismClass);
        boolean canEat = Math.random() <= 0.01*probability;
        if (!canEat) {
            if (probability != 0) {
                recordEvent(String.format("Tried to eat %s with probability %s percent but couldn't", other, probability));
            }
            return;
        }
        recordEvent(String.format("Has eaten %s", other));
        other.die(EATEN);
        currentSaturation += other.getWeight();
        if (currentSaturation > maxSaturation) {
            currentSaturation = maxSaturation;
        }
        recordEvent(String.format("Saturation is %f/%f", currentSaturation, maxSaturation));
    }

    @Override
    public boolean isStarving() {
        return currentSaturation < maxSaturation * 0.01 * SATURATION_PERCENTAGE_TO_BE_ALIVE;
    }

    @Override
    public boolean canEat() {
        return isAlive() && maxSaturation - currentSaturation > 0.000001;
    }

    public int getSpeed() {
        return speed;
    }

    public double getMaxSaturation() {
        return maxSaturation;
    }
}
