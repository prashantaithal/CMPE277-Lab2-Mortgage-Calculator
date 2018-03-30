package com.example.shruthinarayan.lab2;

/**
 * Created by shruthinarayan on 3/27/18.
 */

public class Data {
    private String property_type;
    private String street_address;
    private String city;
    private String state;
    private String zipcode;
    private String property_price;
    private String downpayment;
    private String rate;
    private String terms;
    private String mortgage;

    public String getType() {
        return property_type;
    }
    public String getAddress() {
        return street_address;
    }
    public String getCity() {
        return city;
    }
    public String getState() {
        return state;
    }
    public String getZip() {return zipcode;}
    public String getPropertyprice() {
        return property_price;
    }
    public String getDownpayment() {
        return downpayment;
    }
    public String getRate() {
        return rate;
    }
    public String getTerms() {
        return terms;
    }
    public String getMonthlyPayments() {
        return mortgage;
    }

    public void setFields(String[] fields){
        property_type = fields[0];
        street_address = fields[1];
        city = fields[2];
        state = fields[3];
        zipcode = fields[4];
        property_price = fields[5];
        downpayment = fields[6];
        rate = fields[7];
        terms = fields[8];
        mortgage = fields[9];
    }
    public String getFullAddress(){
        return street_address + "," + city + "," + state + "," + zipcode;
    }
}
