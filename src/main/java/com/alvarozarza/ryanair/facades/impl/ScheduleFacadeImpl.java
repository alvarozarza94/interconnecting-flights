package com.alvarozarza.ryanair.facades.impl;

import com.alvarozarza.ryanair.facades.ScheduleFacade;
import com.alvarozarza.ryanair.models.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;


@Service
public class ScheduleFacadeImpl implements ScheduleFacade {


    private String HTTPS_API_RYANAIR_SCHEDULES = "https://services-api.ryanair.com/timtbl/3/schedules/{departure}/{arrival}/years/{year}/months/{month}";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Schedule getSchedules(String departure, String arrival, Integer year, Integer month) {

        String uri = HTTPS_API_RYANAIR_SCHEDULES;

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("departure", departure);
        urlParams.put("arrival", arrival);
        urlParams.put("year", year.toString());
        urlParams.put("month", month.toString());

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri);


        ResponseEntity<Schedule> response = restTemplate.exchange(builder.buildAndExpand(urlParams).toUri(),
                HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }
}

