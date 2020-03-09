package com.example.msapp;

import java.util.List;

public class Movie {

    private String title;
    private String image;
    private double rating;
    private int releaseYear;
    private List<String> genre;

    public Movie(String title, String image, double rating, int releaseYear, List<String> genre) {
        this.title = title;
        this.image = image;
        this.rating = rating;
        this.releaseYear = releaseYear;
        this.genre = genre;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public double getRating() {
        return rating;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public List<String> getGenre() {
        return genre;
    }
}
