package com.ledenova.island.hierarchy;

import com.ledenova.island.game.Board;
import com.ledenova.island.game.Location;
import com.ledenova.island.config.OrganismClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class AbstractOrganism implements Trackable, Organism {
    private static final int[][] DIRECTIONS = {{0, 0}, {-1, 0}, {0, -1}, {1, 0}, {0, 1}};

    private final Logger log = LoggerFactory.getLogger(AbstractOrganism.class);
    private final List<String> events = new ArrayList<>();
    private final Random random = new Random();

    private final String name;
    private final OrganismClass organismClass;
    private final double weight;
    private final String icon;
    private final Board board;
    protected final OrganismFactory organismFactory;
    private Location location;

    private boolean alive = true;
    private boolean alreadyReproduced = false;

    public AbstractOrganism(String name, OrganismClass organismClass, double weight, String icon, Board board, OrganismFactory organismFactory) {
        this.name = name;
        this.organismClass = organismClass;
        this.weight = weight;
        this.icon = icon;
        this.board = board;
        this.organismFactory = organismFactory;
    }

    @Override
    public void recordEvent(String message) {
        events.add(message);
        log.info("{}: {}", this, message);
    }

    @Override
    public List<String> getEvents() {
        return new ArrayList<>(events);
    }

    @Override
    public void die(CauseOfDeath causeOfDeath) {
        alive = false;
        recordEvent(String.format("Dying (%s)", causeOfDeath));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public OrganismClass getOrganismClass() {
        return organismClass;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public boolean isAlive() {
        return alive;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
    }

    public Board getBoard() {
        return board;
    }

    @Override
    public void setReproduced(boolean value) {
        this.alreadyReproduced = value;
    }

    @Override
    public boolean reproduced() {
        return this.alreadyReproduced;
    }

    protected Location getRandomNeighboringLocation(Location currentLocation) {
        int dirIndex = random.nextInt(5);
        int nextX = currentLocation.getX() + DIRECTIONS[dirIndex][0];
        int nextY = currentLocation.getY() + DIRECTIONS[dirIndex][1];
        return new Location(nextX, nextY);
    }

    @Override
    public String toString() {
        return String.format("%s %s", getIcon(), getName());
    }
}
