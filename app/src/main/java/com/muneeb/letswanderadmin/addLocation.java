package com.muneeb.letswanderadmin;

public class addLocation {
    private String id;
    private double latitude;
    private double longitude;
    private String title;
    private String description;
    private Boolean star;

    public addLocation(String id, double latitude, double longitude, String title, String description, Boolean star) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.description = description;
        this.star = star;
    }

    public addLocation() {
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getStar() {
        return star;
    }

    public void setStar(Boolean star) {
        this.star = star;
    }
}