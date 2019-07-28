package com.storerjoseph.mygarage;

import java.io.Serializable;

public class Vehicle implements Serializable {

    public String nickName;
    public String vinNumber;
    public Integer year;
    public String make;
    public String model;
    public String trim;
    public String transmission;
    public String engine;

    public Vehicle(){
        // default constructor
    }

    public Vehicle(String nickName, String vinNumber, Integer year, String make, String model, String trim, String transmission, String Engine) {
        this.nickName = nickName;
        this.vinNumber = vinNumber;
        this.year = year;
        this.make = make;
        this.model = model;
        this.trim = trim;
        this.transmission = transmission;
        this.engine = Engine;
    }

    public Vehicle(Integer year, String make, String model, String trim, String transmission, String engine) {
        this.year = year;
        this.make = make;
        this.model = model;
        this.trim = trim;
        this.transmission = transmission;
        this.engine = engine;
    }
}
