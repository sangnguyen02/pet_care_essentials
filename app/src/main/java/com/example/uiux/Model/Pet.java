package com.example.uiux.Model;

import java.util.List;

public class Pet {
    private String pet_id;
    private String pet_name;
    private int age;
    private  String gender;
    private int weight;
    private  String pet_type;
    private  String pet_breed;
    private String color;
    private List<String> imageUrls;

    public Pet() {
    }

    public Pet(String pet_id, String pet_name, int age, String gender, int weight, String pet_type, String pet_breed, String color, List<String> imageUrls) {
        this.pet_id = pet_id;
        this.pet_name = pet_name;
        this.age = age;
        this.gender = gender;
        this.weight = weight;
        this.pet_type = pet_type;
        this.pet_breed = pet_breed;
        this.color = color;
        this.imageUrls = imageUrls;
    }

    public String getPet_id() {
        return pet_id;
    }

    public void setPet_id(String pet_id) {
        this.pet_id = pet_id;
    }

    public String getPet_name() {
        return pet_name;
    }

    public void setPet_name(String pet_name) {
        this.pet_name = pet_name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getPet_type() {
        return pet_type;
    }

    public void setPet_type(String pet_type) {
        this.pet_type = pet_type;
    }

    public String getPet_breed() {
        return pet_breed;
    }

    public void setPet_breed(String pet_breed) {
        this.pet_breed = pet_breed;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}
