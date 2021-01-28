package com.alvarozarza.ryanair.service;


import com.alvarozarza.ryanair.facades.RouteFacade;
import com.alvarozarza.ryanair.facades.ScheduleFacade;
import com.alvarozarza.ryanair.facades.impl.RouteFacadeImpl;
import com.alvarozarza.ryanair.models.Interconnection;
import com.alvarozarza.ryanair.models.InterconnectionFlight;
import com.alvarozarza.ryanair.models.Route;
import com.alvarozarza.ryanair.models.Schedule;
import com.alvarozarza.ryanair.services.InterconnectionService;
import com.alvarozarza.ryanair.services.impl.InterconnectionServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
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


    @Test
    public void getInterconnectionsNotFoundTest() {

        // Create mocked objects
        List<Route> mockedRoutes = factory.manufacturePojo(ArrayList.class, Route.class);
        Schedule schedule = factory.manufacturePojo(Schedule.class);

        // When
//        when(scheduleFacade.getSchedules(any(), any(), any(), any())).thenReturn(schedule);
        when(routeFacade.getRoutes()).thenReturn(mockedRoutes);

        List<Interconnection> response = interconnectionService.getInterconnections("MAD", "DUB", LocalDateTime.now(), LocalDateTime.now());

        // Then
        assertThat(response).isNotNull();
    }


}
