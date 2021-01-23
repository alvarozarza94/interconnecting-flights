package com.alvarozarza.ryanair.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel
public class Interconnection {

    @ApiModelProperty
    private Integer stops;

    @ApiModelProperty
    private List<Leg> legs;

    public Interconnection() {
    }


    public Integer getStops() {
        return stops;
    }

    public void setStops(Integer stops) {
        this.stops = stops;
    }

    public List<Leg> getLegs() {
        return legs;
    }

    public void setLegs(List<Leg> legs) {
        this.legs = legs;
    }
}

