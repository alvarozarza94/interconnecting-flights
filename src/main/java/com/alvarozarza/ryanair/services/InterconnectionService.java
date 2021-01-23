package com.alvarozarza.ryanair.services;

import com.alvarozarza.ryanair.models.Interconnection;

import java.time.LocalDateTime;
import java.util.List;

public interface InterconnectionService {
    List<Interconnection> getInterconnections(String departure, String arrival, LocalDateTime departureDateTime, LocalDateTime arrivalDateTime);
}
