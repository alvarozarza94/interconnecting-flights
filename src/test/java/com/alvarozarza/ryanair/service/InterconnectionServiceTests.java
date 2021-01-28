package com.alvarozarza.ryanair.service;


import com.alvarozarza.ryanair.facades.RouteFacade;
import com.alvarozarza.ryanair.facades.ScheduleFacade;
import com.alvarozarza.ryanair.facades.impl.RouteFacadeImpl;
import com.alvarozarza.ryanair.models.*;
import com.alvarozarza.ryanair.services.InterconnectionService;
import com.alvarozarza.ryanair.services.impl.InterconnectionServiceImpl;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InterconnectionServiceTests {

    private static final PodamFactory factory = new PodamFactoryImpl();

    @Mock
    private RouteFacade routeFacade;

    @Mock
    private ScheduleFacade scheduleFacade;

    @InjectMocks
    private InterconnectionServiceImpl interconnectionService;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();


    @Test
    public void getInterconnectionsDirectFlightsTest() {


        LocalDateTime departureDate = LocalDateTime.of(2021, 2, 20, 14, 20);
        LocalDateTime arrivalDate = LocalDateTime.of(2021, 2, 20, 22, 00);

        // Create mocked objects
        Route route = new Route();
        route.setAirportFrom("MAD");
        route.setAirportTo("DUB");
        route.setConnectingAirport(null);
        route.setOperator("RYANAIR");


        Flight flight = new Flight();
        flight.setDepartureTime(LocalTime.of(15, 0, 0));
        flight.setArrivalTime(LocalTime.of(21, 00, 0));
        Day day = new Day();
        day.setDay(20);
        day.setFlights(Arrays.asList(flight));


        Schedule schedule = new Schedule();
        schedule.setMonth(2);
        schedule.setDays(Arrays.asList(day));


        // When
        when(routeFacade.getRoutes()).thenReturn(Arrays.asList(route));
        when(scheduleFacade.getSchedules("MAD", "DUB", 2021, 2)).thenReturn(schedule);

        List<Interconnection> response = interconnectionService.getInterconnections("MAD", "DUB", departureDate, arrivalDate);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.get(0).getStops()).isEqualTo(0);
        assertThat(response.get(0).getLegs().size()).isEqualTo(1);
    }


    @Test
    public void getInterconnectionsNonDirectFlightsTest() {


        LocalDateTime departureDate = LocalDateTime.of(2021, 2, 20, 14, 20);
        LocalDateTime arrivalDate = LocalDateTime.of(2021, 2, 20, 23, 50);


        Route firstRoute = new Route();
        firstRoute.setAirportFrom("MAD");
        firstRoute.setAirportTo("ACE");
        firstRoute.setConnectingAirport(null);
        firstRoute.setOperator("RYANAIR");

        Route secondRoute = new Route();
        secondRoute.setAirportFrom("ACE");
        secondRoute.setAirportTo("DUB");
        secondRoute.setConnectingAirport(null);
        secondRoute.setOperator("RYANAIR");


        Flight flight = new Flight();
        flight.setDepartureTime(LocalTime.of(15, 0, 0));
        flight.setArrivalTime(LocalTime.of(18, 00, 0));
        Day day = new Day();
        day.setDay(20);
        day.setFlights(Arrays.asList(flight));


        Flight secondFlight = new Flight();
        secondFlight.setDepartureTime(LocalTime.of(20, 30, 0));
        secondFlight.setArrivalTime(LocalTime.of(23, 30, 0));
        Day secondDay = new Day();
        secondDay.setDay(20);
        secondDay.setFlights(Arrays.asList(secondFlight));


        Schedule firstSchedule = new Schedule();
        firstSchedule.setMonth(2);
        firstSchedule.setDays(Arrays.asList(day));

        Schedule secondSchedule = new Schedule();
        secondSchedule.setMonth(2);
        secondSchedule.setDays(Arrays.asList(secondDay));

        // When
        when(routeFacade.getRoutes()).thenReturn(Arrays.asList(firstRoute, secondRoute));
        when(scheduleFacade.getSchedules("MAD", "ACE", 2021, 2)).thenReturn(firstSchedule);
        when(scheduleFacade.getSchedules("ACE", "DUB", 2021, 2)).thenReturn(secondSchedule);

        List<Interconnection> response = interconnectionService.getInterconnections("MAD", "DUB", departureDate, arrivalDate);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.get(0).getStops()).isEqualTo(1);
        assertThat(response.get(0).getLegs().size()).isEqualTo(2);
    }

    @Test
    public void getInterconnectionsNotValidDatesTest() {
        // Expected exception
        expectedEx.expect(RuntimeException.class);

        interconnectionService.getInterconnections("MAD", "DUB", LocalDateTime.now().plusDays(4), LocalDateTime.now());
    }

    @Test
    public void getInterconnectionsNotFoundTest() {

        // Create mocked objects
        List<Route> mockedRoutes = factory.manufacturePojo(ArrayList.class, Route.class);
        Schedule schedule = factory.manufacturePojo(Schedule.class);

        // When
        when(routeFacade.getRoutes()).thenReturn(mockedRoutes);

        List<Interconnection> response = interconnectionService.getInterconnections("MAD", "DUB", LocalDateTime.now(), LocalDateTime.now());

        // Then
        assertThat(response).isNotNull();
    }


}
