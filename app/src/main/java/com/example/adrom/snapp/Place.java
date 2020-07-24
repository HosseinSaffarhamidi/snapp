package com.example.adrom.snapp;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by idehpardazanjavan on 8/19/18.
 */

public class Place
{
    public class routes
    {
        @SerializedName("routes")
        public List<Legs> routes=new ArrayList<>();
        public List<Legs> getRoutes() {
            return routes;
        }

        public void setRoutes(List<Legs> routes) {
            this.routes = routes;
        }
    }


    public class Legs
    {
        @SerializedName("legs")
        public List<Distance> legs=new ArrayList<>();

        public List<Distance> getLegs() {
            return legs;
        }

        public void setLegs(List<Distance> legs) {
            this.legs = legs;
        }
    }
    public class Distance
    {
        @SerializedName("distance")
        data distance;

        public data getDistance() {
            return distance;
        }

        public void setDistance(data distance) {
            this.distance = distance;
        }
    }
    public class data
    {
        @SerializedName("text")
        String text;

        @SerializedName("value")

        double value;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }
    }
}
