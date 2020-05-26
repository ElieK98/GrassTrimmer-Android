package com.example.grasstrimmer.Model;

import com.google.gson.annotations.SerializedName;

public class InfoESP {
    @SerializedName("distance")
    private int distance;
    @SerializedName("vitesse")
    private int vitesse;

    public InfoESP(){}

    public int getDistance(){return this.distance;}
    public int getVitesse(){return this.vitesse;}

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setVitesse(int vitesse) {
        this.vitesse = vitesse;
    }
}
