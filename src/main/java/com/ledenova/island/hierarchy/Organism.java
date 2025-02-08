package com.ledenova.island.hierarchy;

import com.ledenova.island.game.Location;
import com.ledenova.island.config.OrganismClass;

public interface Organism extends Mortal, Reproducible, Player {

    String getName();

    OrganismClass getOrganismClass();

    double getWeight();

    String getIcon();

    Location getLocation();

    void setLocation(Location location);
}
