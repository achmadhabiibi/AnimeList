package com.kelompok2.anilist;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AnimeResponse {
    @SerializedName("data")
    private List<AnimeData> data;

    public List<AnimeData> getData() {
        return data;
    }
}

class AnimeData {
    @SerializedName("mal_id")
    private int malId;
    @SerializedName("title")
    private String title;
    @SerializedName("images")
    private ImagesData images;
    @SerializedName("score")
    private double score;
    @SerializedName("synopsis")
    private String synopsis;

    public int getMalId() { return malId; }
    public String getTitle() { return title; }
    public ImagesData getImages() { return images; }
    public double getScore() { return score; }
    public String getSynopsis() { return synopsis; }
}

class ImagesData {
    @SerializedName("jpg")
    private JpgData jpg;
    public JpgData getJpg() { return jpg; }
}

class JpgData {
    @SerializedName("image_url")
    private String imageUrl;
    public String getImageUrl() { return imageUrl; }
}
