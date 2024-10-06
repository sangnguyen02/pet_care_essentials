package com.example.uiux.Model;

import java.util.List;

public class Service {
    private String service_id;
    private String name;
    private  double sell_price;
    private  String size;
    private  String description;
    private  int status;
    private int time_estimate;
    private  String category;
    private  String type;
    private List<String> imageUrls;

    public Service() {
    }

    public Service(String service_id, String name, double sell_price, String size, String description, int status, int time_estimate, String category, String type, List<String> imageUrls) {
        this.service_id = service_id;
        this.name = name;
        this.sell_price = sell_price;
        this.size = size;
        this.description = description;
        this.status = status;
        this.time_estimate = time_estimate;
        this.category = category;
        this.type = type;
        this.imageUrls = imageUrls;
    }

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSell_price() {
        return sell_price;
    }

    public void setSell_price(double sell_price) {
        this.sell_price = sell_price;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTime_estimate() {
        return time_estimate;
    }

    public void setTime_estimate(int time_estimate) {
        this.time_estimate = time_estimate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}
