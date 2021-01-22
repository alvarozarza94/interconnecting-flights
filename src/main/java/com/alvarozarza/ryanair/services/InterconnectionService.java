package com.alvarozarza.ryanair.services;

import java.time.LocalDateTime;

public interface InterconnectionService {
    void getInterconnections(String departure, String arrival, LocalDateTime departureDateTime, LocalDateTime arrivalDateTime);
}
