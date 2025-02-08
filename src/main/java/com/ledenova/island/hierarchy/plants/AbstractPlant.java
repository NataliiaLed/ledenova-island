package com.ledenova.island.hierarchy.plants;

import com.ledenova.island.game.Board;
import com.ledenova.island.game.Location;
import com.ledenova.island.config.Configuration;
import com.ledenova.island.config.OrganismClass;
import com.ledenova.island.hierarchy.AbstractOrganism;
import com.ledenova.island.hierarchy.Organism;
import com.ledenova.island.hierarchy.OrganismFactory;

import static com.ledenova.island.hierarchy.CauseOfDeath.NO_SPACE_TO_LIVE;

public class AbstractPlant extends AbstractOrganism {
    public AbstractPlant(Configuration configuration, String name, OrganismClass organismClass, Board board, OrganismFactory organismFactory) {
        super(name,
                organismClass,
                configuration.getCharacteristicsMap().get(organismClass).getWeight(),
                configuration.getCharacteristicsMap().get(organismClass).getIcon(),
                board,
                organismFactory
        );
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
        reproduce();
    }

    private void reset() {
        setReproduced(false);
    }

    @Override
    public void reproduce() {
        // Can reproduce on this or neighboring cells
        recordEvent("Trying to reproduce");
        Organism sprout = organismFactory.createOrganism(getOrganismClass());
        Location currentLocation = getLocation();
        Location newLocation = getRandomNeighboringLocation(currentLocation);
        boolean successfullyPopulated = getBoard().tryToPopulateCell(sprout, newLocation);
        if (!successfullyPopulated) {
            sprout.die(NO_SPACE_TO_LIVE);
        } else {
            sprout.setLocation(newLocation);
        }
        setReproduced(true);
    }

    @Override
    public boolean canReproduce() {
        return !reproduced();
    }
}
