package com.example.grasstrimmer.Fragments;

public class TrimmingData {
    String personName;
    String personFamilyName;
    String personEmail;
    String personId;
    String date;
    String Minutes;
    String name;

    public TrimmingData(String personId,String personName,String personFamilyName,String personEmail,String date, String Minutes){
        this.personName=personName;
        this.personFamilyName=personFamilyName;
        this.personId=personId;
        this.personEmail=personEmail;
        this.date=date;
        this.Minutes=Minutes;


    }
    public String getDate(){
        return date;
    }
    public String getMinutes(){
        return Minutes;
    }
    public String getPersonName(){ return personName;}

    public String getPersonEmail() {
        return personEmail;
    }

    public String getPersonFamilyName() {
        return personFamilyName;
    }

    public void setPersonEmail(String personEmail) {
        this.personEmail = personEmail;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public void setPersonFamilyName(String personFamilyName) {
        this.personFamilyName = personFamilyName;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getPersonId() {
        return personId;
    }


    public void setDate(String date){
        this.date=date;
    }
    public void setMinutes(String Minutes){
        this.Minutes=Minutes;
    }

}
