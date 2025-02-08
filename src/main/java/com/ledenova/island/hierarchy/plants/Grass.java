package com.ledenova.island.hierarchy.plants;

import com.ledenova.island.game.Board;
import com.ledenova.island.config.Actor;
import com.ledenova.island.config.Configuration;
import com.ledenova.island.hierarchy.OrganismFactory;

import static com.ledenova.island.config.OrganismClass.GRASS;

@Actor(GRASS)
public class Grass extends AbstractPlant {
    public Grass(Configuration configuration, String name, Board board, OrganismFactory organismFactory) {
        super(configuration, name, GRASS, board, organismFactory);
    }
}
