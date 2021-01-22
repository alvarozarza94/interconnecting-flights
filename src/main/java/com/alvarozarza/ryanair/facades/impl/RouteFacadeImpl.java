package com.alvarozarza.ryanair.facades.impl;

import com.alvarozarza.ryanair.facades.RouteFacade;
import com.alvarozarza.ryanair.models.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class RouteFacadeImpl implements RouteFacade {

    private static final String HTTPS_API_RYANAIR_ROUTES = "https://services-api.ryanair.com/locate/3/routes";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<Route> getRoutes() {

        ResponseEntity<List<Route>> response = restTemplate.exchange(HTTPS_API_RYANAIR_ROUTES,
                HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                });

        return response.getBody();
    }
}

