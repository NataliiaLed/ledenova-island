package com.ledenova.island.game;

import com.ledenova.island.config.Configuration;
import com.ledenova.island.config.OrganismClass;
import com.ledenova.island.hierarchy.Organism;
import com.ledenova.island.hierarchy.OrganismFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.ledenova.island.hierarchy.CauseOfDeath.NO_SPACE_TO_LIVE;

public class Board {
    private final Logger log = LoggerFactory.getLogger(Board.class);
    private final Random random = new Random();
    private final Configuration configuration;
    private final Cell[][] cells;
    private final OrganismFactory organismFactory;


    public Board(Configuration configuration) {
        this.configuration = configuration;
        this.cells = new Cell[configuration.getBoardHeight()][configuration.getBoardWidth()];
        this.organismFactory = new OrganismFactory(configuration, this);
        Map<OrganismClass, Integer> maxPopulation = configuration
                .getCharacteristicsMap()
                .entrySet()
                .stream()
                .collect(
                        Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getMaxPopulation())
                );

        for(int i = 0; i < configuration.getBoardHeight(); i++) {
            for(int j = 0; j < configuration.getBoardWidth(); j++) {
                cells[i][j] = new Cell(new Location(i, j), maxPopulation);
            }
        }
    }

    // Tries to put each organism to a random cell. If it fails because of overpopulation,
    // it does not retry and the organism just dies
    public synchronized void populate(Map<OrganismClass, Integer> population) {
        log.info("Starting initial population");
        for (OrganismClass organismClass : population.keySet()) {
            for (int i = 0; i < population.get(organismClass); i++) {
                Organism newBorn = organismFactory.createOrganism(organismClass);
                Location location = getRandomLocation();
                boolean successfullyPopulated = tryToPopulateCell(newBorn, location);
                if (!successfullyPopulated) {
                    log.info("Can't populate {} to {}", newBorn, location);
                    newBorn.die(NO_SPACE_TO_LIVE);
                } else {
                    newBorn.setLocation(location);
                    log.info("Successfully populated {} to {}", newBorn, location);
                }
            }
        }
    }

    private Location getRandomLocation() {
        int x = random.nextInt(configuration.getBoardHeight());
        int y = random.nextInt(configuration.getBoardWidth());
        return new Location(x, y);
    }

    public boolean tryToPopulateCell(Organism organism, Location location) {
        if (!isValidLocation(location)) {
            log.info("Can't populate cell {} because it does not exist", location);
            return false;
        }
        return getCell(location).add(organism);
    }

    private Optional<Cell> getCellOptional(Location location) {
        if (!isValidLocation(location)) {
            return Optional.empty();
        }
        return Optional.of(getCell(location));
    }

    private Cell getCell(Location location) {
        return cells[location.getX()][location.getY()];
    }

    private boolean isValidLocation(Location location) {
        return location.getX() < configuration.getBoardHeight() && location.getX() >= 0 && location.getY() < configuration.getBoardWidth() && location.getY() >= 0;
    }

    public synchronized boolean moveOrganism(Organism organism, Location to) {
        Location from = organism.getLocation();
        if (!putOrganism(organism, to)) {
            log.info("Could not put organism {} to: {}. Staying in {}", organism, to, from);
            return false;
        }
        organism.setLocation(to);
        if (!removeOrganism(organism, from)) {
            throw new IllegalStateException(String.format("Failed to delete organism %s from %s. Organism should be there", organism, from));
        }
        return true;
    }


    public void forEachOrganism(Consumer<Organism> consumer) {
        forEachCell(cell -> cell.forEachOrganism(consumer));
    }

    public void forEachOrganismWhile(Location location, Consumer<Organism> consumer, Supplier<Boolean> continuationCondition) {
        getCellOptional(location).ifPresent(cell -> cell.forEachOrganismWhile(consumer, continuationCondition));
    }

    public void forEachOrganismOfClassWhile(Location location, OrganismClass organismClass, Consumer<Organism> consumer, Supplier<Boolean> continuationCndition) {
        getCellOptional(location).ifPresent(cell -> cell.forEachOrganismOfClassWhile(organismClass, consumer, continuationCndition));
    }

    private synchronized boolean putOrganism(Organism organism, Location to) {
        if (!isValidLocation(to)) {
            log.info("Invalid location to put {} : {}", organism, to);
            return false;
        }
        return getCell(to).add(organism);
    }

    private synchronized boolean removeOrganism(Organism organism, Location from) {
        Cell cell = getCell(from);
        return cell.delete(organism);
    }

    public Map<OrganismClass, Long> collectStats() {
        Map<OrganismClass, Long> stats = new HashMap<>();
        forEachCell(cell -> {
            for (OrganismClass organismClass : cell.getOrganismClasses()) {
                long organismsCountForClass = stats.getOrDefault(organismClass, 0L) + cell.getCurrentPopulation(organismClass);
                stats.put(organismClass, organismsCountForClass);
            }
        });
        return stats;
    }

    public void collectBones() {
        log.info("Initiating garbage collection");
        forEachCell(Cell::collectBones);
    }

    private void forEachCell(Consumer<Cell> consumer) {
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                consumer.accept(cell);
            }
        }
    }
}
