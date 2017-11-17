package com.example.dalibor.katalog.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Employee on 15.11.2017..
 */

public class AccessToken {

    @SerializedName("access_token")
    @Expose
    public  String accessToken;
    @SerializedName("token_type")
    @Expose
    private  String tokenType;
    @SerializedName("expires_in")
    @Expose
    private  Integer expiresIn;
    @SerializedName("refresh_token")
    @Expose
    private  String refreshToken;
    @SerializedName("userName")
    @Expose
    private  String userName;
    @SerializedName(".issued")
    @Expose
    private  String issued;
    @SerializedName(".expires")
    @Expose
    private  String expires;

    public  String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public  String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public  Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    public  String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public  String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public  String getIssued() {
        return issued;
    }

    public void setIssued(String issued) {
        this.issued = issued;
    }

    public  String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }


}
