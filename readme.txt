1. Each organism maintains a list of events to debug what has happened during its lifetime
2. Multithreading. 2 levels of multithreading.
    - Higher level ScheduledThreadPool executor. Runs every x seconds maximum n times.
        - Cleans up garbage from the Board.
        - Schedules game turn for each organism on the Board
        - Prints Board statistics
    - Lover level FixedThreadPool executor. Schedules game turn for a particular organism.
3. Logs are printed to ledenova-island.log.
4. Besides log file, statistics is printed to STDOUT