package com.storerjoseph.mygarage;

public class Vehicle {

    public String nickName;
    public String vinNumber;
    public Integer year;
    public String make;
    public String model;
    public String trim;
    public String transmission;

    public Vehicle(){
        // default constructor
    }

    public Vehicle(String nickName, String vinNumber, Integer year, String make, String model, String trim, String transmission) {
        this.nickName = nickName;
        this.vinNumber = vinNumber;
        this.year = year;
        this.make = make;
        this.model = model;
        this.trim = trim;
        this.transmission = transmission;
    }

    public Vehicle(Integer year, String make, String model, String trim, String transmission) {
        this.year = year;
        this.make = make;
        this.model = model;
        this.trim = trim;
        this.transmission = transmission;
    }
}
