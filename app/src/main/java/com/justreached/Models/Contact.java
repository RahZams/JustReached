package com.justreached.Models;

/**
 * Created by SYED_ZAKRIYA on 3/7/2017.
 */

public class Contact {
    private int Id;
    private String Name;
    private String Number;
    private String EmailId;

    public Contact(String name, String number, String email)
    {
        this.Name=name;
        this.Number=number;
        this.EmailId = email;
    }

    public Contact(int id, String name, String number, String emailId)
    {
        this.Id = id;
        this.Name=name;
        this.Number=number;
        this.EmailId = emailId;
    }

    public Contact()
    {

    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getEmailId(){return EmailId;}

    public void setEmailId(String emailId){EmailId = emailId;}

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }
}
