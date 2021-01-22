package com.alvarozarza.ryanair.services.impl;

import com.alvarozarza.ryanair.facades.RouteFacade;
import com.alvarozarza.ryanair.facades.ScheduleFacade;
import com.alvarozarza.ryanair.models.Route;
import com.alvarozarza.ryanair.models.Schedule;
import com.alvarozarza.ryanair.services.InterconnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InterconnectionServiceImpl implements InterconnectionService {

    @Autowired
    private RouteFacade routeFacade;

    @Autowired
    private ScheduleFacade scheduleFacade;

    @Override
    public void getInterconnections(String departure, String arrival, LocalDateTime departureDateTime, LocalDateTime arrivalDateTime) {
        List<Route> routes = routeFacade.getRoutes();
        Schedule schedule = scheduleFacade.getSchedules(departure, arrival, departureDateTime.getYear(), departureDateTime.getMonthValue());
        System.out.println(routes);
        System.out.println(schedule);
        //TODO: Implement the business logic of the interconnections
    }
}
