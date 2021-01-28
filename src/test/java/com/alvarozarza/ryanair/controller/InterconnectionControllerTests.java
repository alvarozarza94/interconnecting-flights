package com.alvarozarza.ryanair.controller;


import com.alvarozarza.ryanair.controllers.InterconnectionController;
import com.alvarozarza.ryanair.models.Interconnection;
import com.alvarozarza.ryanair.services.InterconnectionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InterconnectionControllerTests {


    @InjectMocks
    private InterconnectionController interconnectionController;

    @Mock
    private InterconnectionService interconnectionService;

    private static final PodamFactory factory = new PodamFactoryImpl();


    @Test
    public void getInterconnectionTest() {

        // Create mocked objects
        List<Interconnection> mockedInterconnections = factory.manufacturePojo(ArrayList.class, Interconnection.class);
        LocalDateTime arrivalDate = LocalDateTime.now();
        LocalDateTime departureDate = LocalDateTime.now();

        // When
        when(interconnectionService.getInterconnections("MAD", "BCN", departureDate, arrivalDate)).thenReturn(mockedInterconnections);
        List<Interconnection> response = interconnectionController.getInterconnections("MAD", "BCN", departureDate, arrivalDate);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.size()).isEqualTo(mockedInterconnections.size());
        assertThat(response.get(0).getLegs()).isEqualTo(mockedInterconnections.get(0).getLegs());
    }


}
