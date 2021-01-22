package com.alvarozarza.ryanair.facades.impl;

import com.alvarozarza.ryanair.facades.ScheduleFacade;
import com.alvarozarza.ryanair.models.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ScheduleFacadeImpl implements ScheduleFacade {


    private String HTTPS_API_RYANAIR_SCHEDULES = "https://services-api.ryanair.com/timtbl/3/schedules/{departure}/{arrival}/years/{year}/months/{month}";
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Schedule getSchedules(String departure, String arrival, Integer year, Integer month) {

        HTTPS_API_RYANAIR_SCHEDULES = HTTPS_API_RYANAIR_SCHEDULES.replace("{departure}", departure)
                .replace("{arrival}", arrival).replace("{year}", year.toString())
                .replace("{month}", month.toString());

        ResponseEntity<Schedule> response = restTemplate.exchange(HTTPS_API_RYANAIR_SCHEDULES,
                HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }
}

