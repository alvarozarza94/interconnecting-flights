package com.alvarozarza.ryanair.models;


public class Flight {

    private String carrierCode;
    private String number;
    private String departureTime;
    private String arrivalTime;

    public Flight() {
    }

    public Flight(String carrierCode, String number, String departureTime, String arrivalTime) {
        this.carrierCode = carrierCode;
        this.number = number;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }


    public String getCarrierCode() {
        return carrierCode;
    }

    public void setCarrierCode(String carrierCode) {
        this.carrierCode = carrierCode;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
}
