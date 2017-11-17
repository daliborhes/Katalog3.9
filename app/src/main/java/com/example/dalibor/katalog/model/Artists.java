package com.example.dalibor.katalog.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Employee on 07.11.2017..
 */

public class Artists {


    public Artists(){}
    @SerializedName("ArtistId")
    @Expose
    private int ArtistId;
    @SerializedName("ArtistName")
    @Expose
    private String ArtistName;

    public int getArtistId() {
        return ArtistId;
    }

    public void setArtistId(int artistId) {
        ArtistId = artistId;
    }

    public String getArtistName() {
        return ArtistName;
    }

    public void setArtistName(String artistName) {
        ArtistName = artistName;
    }

    @Override
    public String toString() {
        return "Artists{" +
                "ArtistId=" + ArtistId +
                ", ArtistName='" + ArtistName + '\'' +
                '}';
    }
}
