package com.ledenova.island.hierarchy.animals;

import com.ledenova.island.game.Board;
import com.ledenova.island.config.Actor;
import com.ledenova.island.config.Configuration;
import com.ledenova.island.hierarchy.OrganismFactory;

import static com.ledenova.island.config.OrganismClass.RABBIT;

@Actor(RABBIT)
public class Rabbit extends AbstractAnimal{
    public Rabbit(Configuration configuration, String name, Board board, OrganismFactory organismFactory) {
        super(configuration, name, RABBIT, board, organismFactory);
    }
}
