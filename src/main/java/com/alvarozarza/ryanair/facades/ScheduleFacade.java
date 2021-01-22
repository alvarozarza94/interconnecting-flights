package com.alvarozarza.ryanair.facades;

import com.alvarozarza.ryanair.models.Schedule;


public interface ScheduleFacade {

    Schedule getSchedules(String departure, String arrival, Integer year, Integer month);
}
