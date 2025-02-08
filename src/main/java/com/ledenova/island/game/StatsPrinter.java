package com.ledenova.island.game;

import com.ledenova.island.config.Configuration;
import com.ledenova.island.config.OrganismClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class StatsPrinter {
    private final Logger log = LoggerFactory.getLogger(StatsPrinter.class);
    private final Board board;
    private final Configuration configuration;

    public StatsPrinter(Configuration configuration, Board board) {
        this.configuration = configuration;
        this.board = board;
    }

    public void printStats() {
        Map<OrganismClass, Long> stats = board.collectStats();
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<OrganismClass, Long> entry : stats.entrySet()) {
            OrganismClass organismClass = entry.getKey();
            long count = entry.getValue();
            String icon = configuration.getCharacteristicsMap().get(organismClass).getIcon();
            sb.append(icon).append(":").append(count).append("\t");
        }
        log.info(sb.toString());
    }
}
