package com.example.dalibor.katalog.remote;

import com.example.dalibor.katalog.model.AccessToken;
import com.example.dalibor.katalog.model.Artists;
import com.example.dalibor.katalog.model.Songs;
import com.example.dalibor.katalog.model.User;

import java.util.ArrayList;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by Employee on 09.11.2017..
 */

public interface ApiService {

    @POST("token")
    Call<AccessToken> getAccesToken(@Body RequestBody body);


    @POST("account/register")
    Call<String> registerUser(@Body User user);

    @GET("artists/get")
    Call<ArrayList<Artists>> getArtists();

    @POST("artists/add")
    @FormUrlEncoded
    Call<Void> postArtists(@Field("ArtistName") String ArtistName);

    @DELETE("artists/delete/{ArtistId}")
    Call<String> deleteArtists(@Path("ArtistId") int ArtistId);

    @PUT("artists/rename/{ArtistId}")
    @FormUrlEncoded
    Call<Void> editArtists(@Path("ArtistId") int ArtistId,
                           @Field("ArtistName") String ArtistName);

    @GET("songs/get")
    Call<ArrayList<Songs>> getSongs();

    @POST("songs/add")
    @FormUrlEncoded
    Call<Void> postSongs(@Field("SongName") String SongName);

    @DELETE("songs/delete/{SongId}")
    Call<String> deleteSongs(@Path("SongId") int SongId);

    @PUT("songs/rename/{SongId}")
    @FormUrlEncoded
    Call<Void> editSongs(@Path("SongId") int SongId,
                           @Field("SongName") String SongName);


}
