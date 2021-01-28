package com.alvarozarza.ryanair.facade;


import com.alvarozarza.ryanair.facades.impl.RouteFacadeImpl;
import com.alvarozarza.ryanair.models.Route;
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
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RouteFacadeTests {

    private static final PodamFactory factory = new PodamFactoryImpl();

    @InjectMocks
    private RouteFacadeImpl routeFacade;

    @Mock
    private RestTemplate restTemplate;


    @Test
    public void getRoutesTest() {

        // Create mocked objects
        List<Route> mockedRoutes = factory.manufacturePojo(ArrayList.class, Route.class);
        ResponseEntity<List<Route>> responseEntityMocked = new ResponseEntity<List<Route>>(mockedRoutes, HttpStatus.OK);

        // When
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(ParameterizedTypeReference.class))).thenReturn(responseEntityMocked);


        List<Route> response = routeFacade.getRoutes();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.size()).isEqualTo(mockedRoutes.size());
        assertThat(response.get(0).getGroup()).isEqualTo(mockedRoutes.get(0).getGroup());
    }


}
