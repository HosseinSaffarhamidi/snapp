package com.example.adrom.snapp;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class LocationAddress {
    public class results
    {
        @SerializedName("results")
        public List<LocationAddress.address> results=new ArrayList<>();

        public List<address> getResults() {
            return results;
        }

        public void setResults(List<address> results) {
            this.results = results;
        }
    }
    public class address
    {
        @SerializedName("address_components")
        public List<LocationAddress.address_params> address_components=new ArrayList<>();

        public List<address_params> getAddress_components() {
            return address_components;
        }

        public void setAddress_components(List<address_params> address_components) {
            this.address_components = address_components;
        }
    }
    public class address_params
    {
        @SerializedName("short_name")
        String short_name;

        public String getShort_name() {
            return short_name;
        }

        public void setShort_name(String short_name) {
            this.short_name = short_name;
        }
    }
}
