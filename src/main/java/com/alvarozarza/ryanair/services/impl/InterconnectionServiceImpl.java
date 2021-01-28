package com.alvarozarza.ryanair.services.impl;

import com.alvarozarza.ryanair.facades.RouteFacade;
import com.alvarozarza.ryanair.facades.ScheduleFacade;
import com.alvarozarza.ryanair.models.*;
import com.alvarozarza.ryanair.services.InterconnectionService;
import com.alvarozarza.ryanair.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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

    @Autowired
    private RouteFacade routeFacade;

    @Autowired
    private ScheduleFacade scheduleFacade;


    @Override
    public List<Interconnection> getInterconnections(String departure, String arrival, LocalDateTime departureDateTime, LocalDateTime arrivalDateTime) {


        //1. Validation, get routes and build predicate to filter valid routes
        if (arrivalDateTime.isBefore(departureDateTime))
            throw new RuntimeException("The arrival time is before the arrival time");

        Predicate<Route> validRoutes = route ->
                route.getConnectingAirport() == null && route.getOperator().equals("RYANAIR");

        List<Route> routes = routeFacade.getRoutes().stream()
                .filter(validRoutes)
                .collect(Collectors.toList());


        //2. Build predicate to filter the direct flights interconnections
        Predicate<Route> directFlightsPredicate = route ->
                route.getAirportFrom().equals(departure) && route.getAirportTo().equals(arrival);

        List<Interconnection> directFlightsInterconnections = routes.stream()
                .filter(directFlightsPredicate)
                .flatMap(route -> getDirectFlights(route, departureDateTime, arrivalDateTime).stream())
                .collect(Collectors.toList());


        //3. Build predicate to filter the one step flights interconnections
        Predicate<Route> oneStepFlightsPredicate = route ->
                route.getAirportFrom().equals(departure) && !route.getAirportTo().equals(arrival);

        List<Interconnection> oneStopFlightsInterconnections = routes.stream()
                .filter(oneStepFlightsPredicate)
                .flatMap(route -> getNotDirectFlights(route, arrival, departureDateTime, arrivalDateTime).stream())
                .collect(Collectors.toList());


        //4. Join both collections and return it to the controller
        return Stream.of(directFlightsInterconnections, oneStopFlightsInterconnections)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

    }

    private List<Interconnection> getDirectFlights(Route route, LocalDateTime departureDateTime, LocalDateTime arrivalDateTime) {

        //Get the schedules for all the months
        long monthsBetween = ChronoUnit.MONTHS.between(
                YearMonth.from(departureDateTime),
                YearMonth.from(arrivalDateTime)
        ) + 1;

        //Loop over all the months and get all the schedules and direct flights
        List<InterconnectionFlight> directFlights = new ArrayList<>();
        for (int i = 0, monthNumber = departureDateTime.getMonthValue(), year = departureDateTime.getYear(); i < monthsBetween; i++, monthNumber++) {
            Schedule schedule = scheduleFacade.getSchedules(route.getAirportFrom(), route.getAirportTo(), year, monthNumber);
            directFlights.addAll(getFlightsBySchedule(schedule, route.getAirportFrom(), route.getAirportTo(), departureDateTime, arrivalDateTime));
            if (monthNumber == 12) {
                year++;
                monthNumber = 1;
            }
        }

        //Map the direct flights to interconnection flights
        return directFlights.stream()
                .map(directFlight -> mapInterconnectionFlightToInterconnection(Arrays.asList(directFlight)))
                .collect(Collectors.toList());

    }

    private List<Interconnection> getNotDirectFlights(Route route, String arrival, LocalDateTime departureDateTime, LocalDateTime arrivalDateTime) {

        //1. Predicate to check the stepover
        BiPredicate<InterconnectionFlight, InterconnectionFlight> stepoversPredicate = (firstFlight, secondFlight) ->
                TimeUtils.durationBetweenDates(firstFlight.getArrivalDateTime(), secondFlight.getDepartureDateTime()).toHours() >= 2
                        && firstFlight.getArrivalDateTime().isBefore(secondFlight.getDepartureDateTime());

        //2. Get the first and the second flights and schedules
        Schedule firstSchedule = scheduleFacade.getSchedules(route.getAirportFrom(), route.getAirportTo(), departureDateTime.getYear(), departureDateTime.getMonthValue());
        List<InterconnectionFlight> firstFlights = getFlightsBySchedule(firstSchedule, route.getAirportFrom(), route.getAirportTo(), departureDateTime, arrivalDateTime);

        Schedule secondSchedule = scheduleFacade.getSchedules(route.getAirportTo(), arrival, departureDateTime.getYear(), departureDateTime.getMonthValue());
        List<InterconnectionFlight> secondFlights = getFlightsBySchedule(secondSchedule, route.getAirportTo(), arrival, departureDateTime, arrivalDateTime);

        //3. Build the interconnection object
        return firstFlights.stream().flatMap(firstFlight -> secondFlights.stream()
                .filter(secondFlight -> stepoversPredicate.test(firstFlight, secondFlight))
                .map(secondFlight -> mapInterconnectionFlightToInterconnection(Arrays.asList(firstFlight, secondFlight))))
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
                    schedule.getMonth(), day.getDay(),
                    flight.getDepartureTime().getHour(), flight.getDepartureTime().getMinute()));

            //1.2 Calculate arrival date taking into account the next day
            interconnectionFlight.setArrivalDateTime(interconnectionFlight.getDepartureDateTime()
                    .plusMinutes(TimeUtils.durationBetweenTimes(flight.getDepartureTime(), flight.getArrivalTime()).toMinutes()));

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


    private Interconnection mapInterconnectionFlightToInterconnection(List<InterconnectionFlight> interconnectionFlights) {

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
