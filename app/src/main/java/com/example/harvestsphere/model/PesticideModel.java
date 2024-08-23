package com.example.harvestsphere.model;

import java.util.List;

public class PesticideModel {
    private String name;
    private double pricePerLiter;
    private String image;
    private List<String> crops;
    private String description;

    // Constructor
    public PesticideModel(String name, double pricePerLiter, String image, List<String> crops, String description) {
        this.name = name;
        this.pricePerLiter = pricePerLiter;
        this.image = image;
        this.crops = crops;
        this.description = description;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public double getPricePerLiter() {
        return pricePerLiter;
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

    public void setPricePerLiter(double pricePerLiter) {
        this.pricePerLiter = pricePerLiter;
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
