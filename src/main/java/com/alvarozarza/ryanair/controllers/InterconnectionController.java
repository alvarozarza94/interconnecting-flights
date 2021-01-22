package com.alvarozarza.ryanair.controllers;


import com.alvarozarza.ryanair.services.InterconnectionService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

import java.time.LocalDateTime;



@RestController
@RequestMapping("/api/interconnections")
public class InterconnectionController {


    @Autowired
    InterconnectionService interconnectionService;


    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, value = "")
    @ApiResponses({ //
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "OK", response = Character[].class),
            @ApiResponse(code = HttpServletResponse.SC_NOT_FOUND, message = "NOT_FOUND"), //
            @ApiResponse(code = HttpServletResponse.SC_BAD_REQUEST, message = "BAD_REQUEST"), //
            @ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "INTERNAL_SERVER_ERROR"), //
    })
    @ApiOperation(value = "Get list of flights departing from a given departure airport not\n" +
            "earlier than the specified departure datetime and arriving to a given arrival airport not\n" +
            "later than the specified arrival datetime")
    public void getInterconnections(@ApiParam(value = "Departure airport IATA code", name = "departure", required = true)
                                    @RequestParam(name = "departure") String departure,
                                    @ApiParam(value = "Arrival airport IATA code", name = "arrival", required = true)
                                    @RequestParam(name = "arrival") String arrival,
                                    @ApiParam(value = "Departure datetime in the departure airport timezone in ISO format", name = "departureDateTime", required = true, format = "yyyy-MM-ddTHH:mm")
                                    @RequestParam(name = "departureDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureDateTime,
                                    @ApiParam(value = "Arrival datetime in the arrival airport timezone in ISO format", name = "arrivalDateTime", required = true, format = "yyyy-MM-ddTHH:mm")
                                    @RequestParam(name = "arrivalDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime arrivalDateTime) {

        interconnectionService.getInterconnections(departure, arrival, departureDateTime, arrivalDateTime);

    }


}
