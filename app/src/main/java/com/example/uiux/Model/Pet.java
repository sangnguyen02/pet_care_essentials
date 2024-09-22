package com.example.uiux.Model;

public class Pet {
    private String pet_id;
    private String pet_name;
    private int age;
    private  String gender;
    private int weight;
    private  String pet_type;
    private String color;

    public Pet() {
    }

    public Pet(String pet_id, String pet_name, int age, String gender, int weight, String pet_type, String color) {
        this.pet_id = pet_id;
        this.pet_name = pet_name;
        this.age = age;
        this.gender = gender;
        this.weight = weight;
        this.pet_type = pet_type;
        this.color = color;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
