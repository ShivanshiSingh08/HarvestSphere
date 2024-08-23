package com.example.harvestsphere.model;

import java.util.List;

public class FertilizerModel {
    private String name;
    private double pricePerKg;
    private String image;
    private List<String> crops;
    private String description;

    // Constructor
    public FertilizerModel(String name, double pricePerKg, String image, List<String> crops, String description) {
        this.name = name;
        this.pricePerKg = pricePerKg;
        this.image = image;
        this.crops = crops;
        this.description = description;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public double getPricePerKg() {
        return pricePerKg;
    }

    public String getImage() {
        return image;
    }

    public List<String> getCrops() {
        return crops;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPricePerKg(double pricePerKg) {
        this.pricePerKg = pricePerKg;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setCrops(List<String> crops) {
        this.crops = crops;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
