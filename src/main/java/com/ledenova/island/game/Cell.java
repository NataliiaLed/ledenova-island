package com.ledenova.island.game;

import com.ledenova.island.config.OrganismClass;
import com.ledenova.island.hierarchy.Mortal;
import com.ledenova.island.hierarchy.Organism;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Cell {
    private final Logger log = LoggerFactory.getLogger(Cell.class);

    private final Map<OrganismClass, Set<Organism>> population = new ConcurrentHashMap<>();
    private final Map<OrganismClass, Integer> maxPopulation;
    private final Location location;

    public Cell(Location location, Map<OrganismClass, Integer> maxPopulation) {
        this.location = location;
        this.maxPopulation = maxPopulation;
    }

    public synchronized boolean delete(Organism organism) {
        log.info("Removing {} from cell {}", organism, this);
        return population.get(organism.getOrganismClass()).remove(organism);
    }

    public synchronized boolean add(Organism organism) {
        population.putIfAbsent(organism.getOrganismClass(), new HashSet<>());
        if (canPopulate(organism)) {
            int maxPopulationLevel = maxPopulation.get(organism.getOrganismClass());
            log.info("Can't add {} to cell {}. The cell has reached max population level {} for {}", organism, this, maxPopulationLevel, organism.getOrganismClass());
            return false;
        }
        log.info("Adding {} to cell {}", organism, this);
        return population.get(organism.getOrganismClass()).add(organism);
    }

    private boolean canPopulate(Organism organism) {
        return getCurrentPopulation(organism.getOrganismClass()) >= maxPopulation.get(organism.getOrganismClass());
    }

    public synchronized long getCurrentPopulation(OrganismClass organismClass) {
        if (!population.containsKey(organismClass)) {
            return 0;
        }
        return population.get(organismClass)
                .stream()
                .filter(Mortal::isAlive)
                .count();
    }

    public synchronized Set<OrganismClass> getOrganismClasses() {
        return new HashSet<>(population.keySet());
    }

    @Override
    public String toString() {
        return "Cell:" + location.toString();
    }

    public synchronized void forEachOrganismWhile(Consumer<Organism> consumer, Supplier<Boolean> continuationCondition) {
        for (Set<Organism> organisms : population.values()) {
            for (Organism organism : new HashSet<>(organisms)) {
                if (!continuationCondition.get()) {
                    return;
                }
                if (organism.isAlive()) {
                    consumer.accept(organism);
                }
            }
        }
    }

    public synchronized void forEachOrganism(Consumer<Organism> consumer) {
        forEachOrganismWhile(consumer, () -> true);
    }

    public synchronized void forEachOrganismOfClassWhile(OrganismClass organismClass, Consumer<Organism> consumer, Supplier<Boolean> continuationCondition) {
        for (Organism organism : new HashSet<>(population.get(organismClass))) {
            if (!continuationCondition.get()) {
                return;
            }
            if (organism.isAlive()) {
                consumer.accept(organism);
            }
        }
    }

    public synchronized void collectBones() {
        for (OrganismClass organismClass : population.keySet()) {
            Iterator<Organism> it = population.get(organismClass).iterator();
            while(it.hasNext()) {
                Organism o = it.next();
                if (!o.isAlive()) {
                    log.info("Collecting bones of {}", o);
                    it.remove();
                }
            }
        }
    }
}