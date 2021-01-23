package com.alvarozarza.ryanair.models;

import java.util.List;

public class Schedule {


    private Integer month;
    private List<Day> days;

    public Schedule() {
    }


    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public List<Day> getDays() {
        return days;
    }

    public void setDays(List<Day> days) {
        this.days = days;
    }
}
