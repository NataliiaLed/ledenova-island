### Notes
0. Entry point: src/main/java/com/ledenova/island/Main.java
1. Each organism maintains a list of events to debug what has happened during its lifetime
2. Multithreading. 2 levels of multithreading.
    - Higher level ScheduledThreadPool executor. Runs every x seconds maximum n times.
        - Cleans up garbage from the Board.
        - Schedules game turn for each organism on the Board
        - Prints Board statistics
    - Lower level FixedThreadPool executor. Schedules game turn for a particular organism.
3. Logs are printed to ledenova-island.log.
4. Besides log file, statistics is printed to STDOUT
5. src/main/resources/config - configuration
    - characteristics.txt - characteristics of each animal
    - config.properties - basic game settings
    - ecosystem.txt - probabilities to eat
    - initial-population.txt - initial board population before game starts


### Description
When game starts, it tries to populate the board with initial population (initial-population.txt). It tries to put each organism to a random cell on the board. If it fails (reached max population in the cell), the organism dies right away.
Organisms are basically divided into Animal-s and Plant-s.
All animals have the same behavior now which is defined in AbstractAnimal class and its parent classes. But it can be overridden for the specific animal.
On its turn each animal does the following:
    - Resets it's state (saturation level, reproduction state)
    - Moves x steps. For each step, if it can't move for some reason (target cell does not exist or full) it stays on the cell.
    - Tries to eat other organisms in the cell until it's saturated.
    - Tries to find pair to reproduce with and reproduces if their saturation is 100%. If the baby is born, it is put to the same cell. If the cell is full the baby dies.
    - Dies if it is starving (saturation < 50%)
There's only one Plant right now: Grass. Behavior is mainly defined in AbstractPlant.
On its turn each plant does the following:
    - Resets it's state (reproduction state)
    - Tries to reproduce. When Plant is reproduced it can put its sprout to the same or neighboring cell. Each plant has 1 sprout and does not need a pair to reproduce. If the cell where it puts sprout is full, the sprout dies.
When an organism dies, it's not removed from the board right a way. It is marked as DEAD instead and does not participate in any activities. Then all the bodies are collected by garbage collection process which is synchronized on the Cell level. When cell is garbage collected no other operation can happen on this Cell. This is done to avoid ConcurrentModificationException.
