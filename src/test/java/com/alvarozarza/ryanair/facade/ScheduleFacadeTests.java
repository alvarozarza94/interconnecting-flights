package com.alvarozarza.ryanair.facade;


import com.alvarozarza.ryanair.facades.impl.ScheduleFacadeImpl;
import com.alvarozarza.ryanair.models.Schedule;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScheduleFacadeTests {

    private static final PodamFactory factory = new PodamFactoryImpl();

    @InjectMocks
    private ScheduleFacadeImpl scheduleFacade;

    @Mock
    private RestTemplate restTemplate;


    @Test
    public void getScheduleTest() {

        // Create mocked objects
        Schedule mockedSchedules = factory.manufacturePojo(Schedule.class);
        ResponseEntity<Schedule> responseEntityMocked = new ResponseEntity<Schedule>(mockedSchedules, HttpStatus.OK);

        // When
        when(restTemplate.exchange(any(), any(HttpMethod.class), any(), any(ParameterizedTypeReference.class))).thenReturn(responseEntityMocked);


        Schedule response = scheduleFacade.getSchedules("MAD", "DUB", 2021, 02);

        // Then
        assertThat(response).isNotNull();
        assertThat(response).isEqualTo(mockedSchedules);
    }


}
