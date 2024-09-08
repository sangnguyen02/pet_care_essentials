package com.example.uiux.Model;

public class District {
    private int district_id;
    private String name;
    private int city_id;

    public District() {
    }

    public District(int district_id, String name, int city_id) {
        this.district_id = district_id;
        this.name = name;
        this.city_id = city_id;
    }

    public int getDistrict_id() {
        return district_id;
    }

    public void setDistrict_id(int district_id) {
        this.district_id = district_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCity_id() {
        return city_id;
    }

    public void setCity_id(int city_id) {
        this.city_id = city_id;
    }
}
