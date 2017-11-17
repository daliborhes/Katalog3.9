package com.example.dalibor.katalog.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Employee on 09.11.2017..
 */

public class Songs {

    public Songs (){}

    @SerializedName("SongId")
    @Expose
    private int SongId;
    @SerializedName("SongName")
    @Expose
    private String SongName;

    public int getSongId() {
        return SongId;
    }

    public void setSongId(int songId) {
        SongId = songId;
    }

    public String getSongName() {
        return SongName;
    }

    public void setSongName(String songName) {
        SongName = songName;
    }

    @Override
    public String toString() {
        return "Songs{" +
                "SongId=" + SongId +
                ", SongName='" + SongName + '\'' +
                '}';
    }

}
