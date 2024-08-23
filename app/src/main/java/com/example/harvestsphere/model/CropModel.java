package com.example.harvestsphere.model;

public class CropModel {
    private String name;
    private String season;
    private double pricePerKg;
    private String image; // URL or path to the image
    private String soilType;
    private String description;

    // Constructor
    public CropModel(String name, String season, double pricePerKg, String image, String soilType, String description) {
        this.name = name;
        this.season = season;
        this.pricePerKg = pricePerKg;
        this.image = image;
        this.soilType = soilType;
        this.description = description;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public String getSeason() {
        return season;
    }

    public double getPricePerKg() {
        return pricePerKg;
    }

    public String getImage() {
        return image;
    }

    public String getSoilType() {
        return soilType;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public void setPricePerKg(double pricePerKg) {
        this.pricePerKg = pricePerKg;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setSoilType(String soilType) {
        this.soilType = soilType;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "CropModel{" +
                "name='" + name + '\'' +
                ", season='" + season + '\'' +
                ", pricePerKg=" + pricePerKg +
                ", image='" + image + '\'' +
                ", soilType='" + soilType + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
