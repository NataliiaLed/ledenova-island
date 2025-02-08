package com.ledenova.island.game;

import com.ledenova.island.config.Configuration;
import com.ledenova.island.config.ConfigurationLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Game {
    private final Logger log = LoggerFactory.getLogger(Game.class);

    private final Configuration configuration;
    private final Board board;
    private final StatsPrinter statsPrinter;
    private final ExecutorService turnExecutor;
    private final ScheduledExecutorService scheduledExecutor;

    private final int maxTurns;
    private final int schedulePeriodSec;
    private final int awaitTerminationMin;

    private final AtomicInteger counter = new AtomicInteger();


    public Game() {
        configuration = new ConfigurationLoader().load();
        board = new Board(configuration);
        maxTurns = configuration.getMaxTurns();
        schedulePeriodSec = configuration.getSchedulePeriodSec();
        awaitTerminationMin = configuration.getAwaitTerminationMin();
        statsPrinter = new StatsPrinter(configuration, board);
        turnExecutor = Executors.newFixedThreadPool(5);
        scheduledExecutor = Executors.newScheduledThreadPool(1);
    }

    public void play() {
        board.populate(configuration.getInitialPopulation());
        scheduledExecutor.scheduleAtFixedRate(this::makeTurn, 0, schedulePeriodSec, TimeUnit.SECONDS);
    }

    private void makeTurn() {
        int currentTurn = counter.incrementAndGet();
        if (currentTurn > maxTurns) {
            shutdown();
            return;
        }
        statsPrinter.printStats();
        board.collectBones();
        board.forEachOrganism(organism -> turnExecutor.submit(organism::makeTurn));
    }

    private void shutdown() {
        try {
            turnExecutor.shutdown();
            scheduledExecutor.shutdown();
            turnExecutor.awaitTermination(awaitTerminationMin, TimeUnit.MINUTES);
            scheduledExecutor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            scheduledExecutor.shutdownNow();
            turnExecutor.shutdownNow();
        } finally {
            log.info("Final stats");
            statsPrinter.printStats();
        }
    }
}
