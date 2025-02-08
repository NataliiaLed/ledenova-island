package com.ledenova.island.hierarchy;

import com.ledenova.island.game.Board;
import com.ledenova.island.config.Actor;
import com.ledenova.island.config.Configuration;
import com.ledenova.island.config.OrganismClass;
import com.ledenova.island.util.OrganismNameGenerator;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.util.Map;
import java.util.stream.Collectors;

public class OrganismFactory {
    private final Configuration configuration;
    private final Map<OrganismClass, Class<? extends Organism>> classMap;
    private final OrganismNameGenerator nameGenerator = new OrganismNameGenerator();
    private final Board board;

    public OrganismFactory(Configuration configuration, Board board) {
        this.configuration = configuration;
        this.board = board;

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(new SubTypesScanner())
                .setUrls(ClasspathHelper.forPackage("com.ledenova.island")));

        //        new Reflections(this.getClass().getClassLoader());
        classMap = reflections.getSubTypesOf(Organism.class)
                .stream()
                .filter(subClass -> subClass.isAnnotationPresent(Actor.class))
                .collect(Collectors.toMap(
                        value -> value.getAnnotation(Actor.class).value(),
                        value -> value));
    }

    public Organism createOrganism(OrganismClass organismClass) {
        try {
            Class<? extends Organism> clazz = classMap.get(organismClass);
            String name = nameGenerator.generateName(organismClass);
            return clazz.getConstructor(Configuration.class, String.class, Board.class, OrganismFactory.class)
                    .newInstance(configuration, name, board, this);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create organism", e);
        }
    }
}
