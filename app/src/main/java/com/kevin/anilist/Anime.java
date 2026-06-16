package com.kevin.anilist;

import java.io.Serializable;

public class Anime implements Serializable {
    private int id; // SQLite ID
    private int malId;
    private String title;
    private String imageUrl;
    private double score;
    private String synopsis;
    private String status;

    public Anime() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getMalId() { return malId; }
    public void setMalId(int malId) { this.malId = malId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

    public String getSynopsis() { return synopsis; }
    public void setSynopsis(String synopsis) { this.synopsis = synopsis; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
