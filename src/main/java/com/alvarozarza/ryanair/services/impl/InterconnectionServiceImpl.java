package com.alvarozarza.ryanair.services.impl;

import com.alvarozarza.ryanair.facades.RouteFacade;
import com.alvarozarza.ryanair.facades.ScheduleFacade;
import com.alvarozarza.ryanair.models.*;
import com.alvarozarza.ryanair.services.InterconnectionService;
import com.alvarozarza.ryanair.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class InterconnectionServiceImpl implements InterconnectionService {

    public static final int STEPOVER_MAX_TIME = 2;

    @Autowired
    private RouteFacade routeFacade;

    @Autowired
    private ScheduleFacade scheduleFacade;


    @Override
    public List<Interconnection> getInterconnections(String departure, String arrival, LocalDateTime departureDateTime, LocalDateTime arrivalDateTime) {


        //1. Get routes and build predicate to filter valid routes
        Predicate<Route> validRoutes = route ->
                route.getConnectingAirport() == null && route.getOperator().equals("RYANAIR");

        List<Route> routes = routeFacade.getRoutes().stream()
                .filter(validRoutes)
                .collect(Collectors.toList());

        //2. Get the schedules
        Schedule schedule = scheduleFacade.getSchedules(departure, arrival, departureDateTime.getYear(),
                departureDateTime.getMonthValue());


        //3. Build predicate to filter the direct flights interconnections
        Predicate<Route> directFlightsPredicate = route ->
                route.getAirportFrom().equals(departure) && route.getAirportTo().equals(arrival);

        List<Interconnection> directFlightsInterconnections = routes.stream()
                .filter(directFlightsPredicate)
                .flatMap(route -> getDirectFlights(schedule, route, departureDateTime, arrivalDateTime).stream())
                .collect(Collectors.toList());


        //4. Build predicate to filter the one step flights interconnections
        Predicate<Route> oneStepFlightsPredicate = route ->
                route.getAirportFrom().equals(departure) && !route.getAirportTo().equals(arrival);

        List<Interconnection> oneStopFlightsInterconnections = routes.stream()
                .filter(oneStepFlightsPredicate)
                .flatMap(route -> getNotDirectFlights(schedule, route, arrival, departureDateTime, arrivalDateTime).stream())
                .collect(Collectors.toList());


        //5. Join both collections and return it to the controller
        return Stream.of(directFlightsInterconnections, oneStopFlightsInterconnections)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

    }

    private List<Interconnection> getDirectFlights(Schedule schedule, Route route, LocalDateTime departureDateTime, LocalDateTime arrivalDateTime) {

        List<InterconnectionFlight> directFlights = getFlightsBySchedule(schedule, route.getAirportFrom(), route.getAirportTo(), departureDateTime, arrivalDateTime);

        return directFlights.stream()
                .map(directFlight -> mapInterConnectionFlightToInterconnection(Arrays.asList(directFlight)))
                .collect(Collectors.toList());

    }

    private List<Interconnection> getNotDirectFlights(Schedule schedule, Route route, String arrival, LocalDateTime departureDateTime, LocalDateTime arrivalDateTime) {

        //1. Predicate to check the stepover
        BiPredicate<InterconnectionFlight, InterconnectionFlight> stepoversPredicate = (firstFlight, secondFlight) ->
                TimeUtils.durationBetweenDates(firstFlight.getArrivalDateTime().toLocalTime(), secondFlight.getDepartureDateTime().toLocalTime()).toHours() >= STEPOVER_MAX_TIME;

        //2. Get the first and the second flight
        List<InterconnectionFlight> firstFlights = getFlightsBySchedule(schedule, route.getAirportFrom(), route.getAirportTo(), departureDateTime, arrivalDateTime);
        List<InterconnectionFlight> secondFlights = getFlightsBySchedule(schedule, route.getAirportTo(), arrival, departureDateTime, arrivalDateTime);

        //3. Build the
        return firstFlights.stream().flatMap(firstFlight -> secondFlights.stream()
                .filter(secondFlight -> stepoversPredicate.test(firstFlight, secondFlight))
                .map(secondFlight -> mapInterConnectionFlightToInterconnection(Arrays.asList(firstFlight, secondFlight))))
                .collect(Collectors.toList());
    }

    private List<InterconnectionFlight> getFlightsBySchedule(Schedule schedule, String departure, String arrival, LocalDateTime departureDateTime, LocalDateTime arrivalDateTime) {

        //1. Build Bifunction as mapper to transorm to the InterconnectionFlight
        BiFunction<Day, Flight, InterconnectionFlight> flightMapperFunction = (day, flight) -> {

            InterconnectionFlight interconnectionFlight = new InterconnectionFlight();
            interconnectionFlight.setCarrierCode(flight.getCarrierCode());
            interconnectionFlight.setNumber(flight.getNumber());
            interconnectionFlight.setDeparture(departure);
            interconnectionFlight.setArrival(arrival);

            //1.1 Set departure time
            interconnectionFlight.setDepartureDateTime(LocalDateTime.of(departureDateTime.getYear(),
                    departureDateTime.getMonth(), day.getDay().intValue(),
                    flight.getDepartureTime().getHour(), flight.getDepartureTime().getMinute()));

            //1.2 Calculate arrival date taking into account the next day
            interconnectionFlight.setArrivalDateTime(interconnectionFlight.getDepartureDateTime()
                    .plusMinutes(TimeUtils.durationBetweenDates(flight.getDepartureTime(), flight.getArrivalTime()).toMinutes()));

            return interconnectionFlight;
        };

        //2. Build the predicate to test if is between the requested departure and arrival
        Predicate<InterconnectionFlight> predicateCheckFlightTimes = flight -> flight.getDepartureDateTime()
                .isAfter(departureDateTime) && flight.getArrivalDateTime().isBefore(arrivalDateTime);

        //3. Build the response applying the time rules
        return schedule.getDays().stream()
                .flatMap(day -> day.getFlights().stream()
                        .map(flight -> flightMapperFunction.apply(day, flight)))
                .filter(predicateCheckFlightTimes)
                .collect(Collectors.toList());
    }


    private Interconnection mapInterConnectionFlightToInterconnection(List<InterconnectionFlight> interconnectionFlights) {

        Interconnection interconnection = new Interconnection();
        interconnection.setStops(interconnectionFlights.size() - 1);

        interconnection.setLegs(interconnectionFlights.stream()
                .map(flight -> {
                    Leg leg = new Leg();
                    leg.setDepartureAirport(flight.getDeparture());
                    leg.setArrivalAirport(flight.getArrival());
                    leg.setArrivalDateTime(flight.getArrivalDateTime());
                    leg.setDepartureDateTime(flight.getDepartureDateTime());
                    return leg;
                })
                .collect(Collectors.toList()));
        return interconnection;
    }


}
