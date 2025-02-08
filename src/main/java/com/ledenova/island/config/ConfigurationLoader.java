package com.ledenova.island.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ConfigurationLoader {
    Logger log = LoggerFactory.getLogger(ConfigurationLoader.class);
    public Configuration load() {
        try {
            Properties properties = new Properties();
            properties.load(getClass().getResourceAsStream("/config/config.properties"));
            int height = Integer.parseInt((String)properties.get("board.height"));
            int width = Integer.parseInt((String)properties.get("board.width"));
            int maxTurns = Integer.parseInt((String)properties.get("maxTurns"));
            int schedulePeriodSec = Integer.parseInt((String)properties.get("schedulePeriodSec"));
            int awaitTerminationMin = Integer.parseInt((String)properties.get("awaitTerminationMin"));
            Map<OrganismClass, Characteristics> characteristicsMap = loadCharacteristics();
            log.info("Loaded characteristics: {}", characteristicsMap);
            Map<OrganismClass, Map<OrganismClass, Integer>> probabilitiesToEat = loadProbabilitiesToEat();
            log.info("Loaded probabilities to eat: {}", probabilitiesToEat);
            Map<OrganismClass, Integer> initialPopulation = loadInitialPopulation();
            log.info("Loaded initial population: {}", initialPopulation);
            return new Configuration(height, width, maxTurns, schedulePeriodSec, awaitTerminationMin, characteristicsMap, initialPopulation, probabilitiesToEat);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load configuration", e);
        }
    }

    private Map<OrganismClass, Integer> loadInitialPopulation() {
        Map<OrganismClass, Integer> result = new HashMap<>();
        Scanner scanner = new Scanner(Objects.requireNonNull(getClass().getResourceAsStream("/config/initial-population.txt")));
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] tokens = line.split("\\s+");
            OrganismClass organismClass = OrganismClass.valueOf(tokens[0]);
            int num = Integer.parseInt(tokens[1]);
            result.put(organismClass, num);

        }
        return result;
    }

    private Map<OrganismClass, Map<OrganismClass, Integer>> loadProbabilitiesToEat() {
        Map<OrganismClass, Map<OrganismClass, Integer>> result = new HashMap<>();
        Scanner scanner = new Scanner(Objects.requireNonNull(getClass().getResourceAsStream("/config/ecosystem.txt")));
        String header = scanner.nextLine().trim();
        List<OrganismClass> classesToEat = Arrays.stream(header.split("\\s+"))
                .map(OrganismClass::valueOf).collect(Collectors.toList());

        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] tokens = line.split("\\s+");
            OrganismClass organismClass = OrganismClass.valueOf(tokens[0]);
            result.put(organismClass, new HashMap<>());
            for(int i = 0; i < classesToEat.size(); i++) {
                int probability = Integer.parseInt(tokens[i + 1]);
                result.get(organismClass).put(classesToEat.get(i), probability);
            }
        }
        return result;
    }

    private Map<OrganismClass, Characteristics> loadCharacteristics() {
        Map<OrganismClass, Characteristics> result = new HashMap<>();
        Scanner scanner = new Scanner(Objects.requireNonNull(getClass().getResourceAsStream("/config/characteristics.txt")));
        scanner.nextLine(); // skipping header line
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] tokens = line.split("\\s+");
            OrganismClass organismClass = OrganismClass.valueOf(tokens[0]);
            double weight = Double.parseDouble(tokens[1]);
            int maxPopulation = Integer.parseInt(tokens[2]);
            int speed = Integer.parseInt(tokens[3]);
            double maxSaturation = Double.parseDouble(tokens[4]);
            String icon = tokens[5];
            result.put(organismClass, new Characteristics(organismClass, weight, maxPopulation, speed, maxSaturation, icon));

        }
        return result;
    }
}
