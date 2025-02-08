package com.ledenova.island.hierarchy;

import java.util.List;

public interface Trackable {
    void recordEvent(String message);
    List<String> getEvents();
}
