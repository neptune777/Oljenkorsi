package com.example.android.oljenkorsi;

public class Person {

    private String name;
    private String phoneNumber;

    public Person(String name, String phoneNum){

        this.name=name;
        this.phoneNumber=phoneNum;

    }


    public String getName(){

        return this.name;
    }

    public String getPhoneNumber(){

        return this.phoneNumber;
    }
}
