package com.alvarozarza.ryanair.models;

import java.util.List;

public class Day {

    private Integer day;
    private List<Flight> flights;

    public Day() {
    }

    public Day(Integer day, List<Flight> flights) {
        this.day = day;
        this.flights = flights;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }
}
