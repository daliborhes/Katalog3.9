package com.example.dalibor.katalog.remote;

/**
 * Created by Employee on 09.11.2017..
 */

public class ApiUtils {

    private ApiUtils(){}

    //public static final String BASE_URL = "http://d3ef8d1b.ngrok.io/api";
    public static final String BASE_URL = "http://10.3.201.205:3000/api/";

    //public static final String BASE_URL = "http://d1a26cfc.ngrok.io/api/";


    public static ApiService getApiService () {
        return RetrofitClient.getClient(BASE_URL).create(ApiService.class);
    }

    public static ApiService getApiService (String authToken) {
        return RetrofitClient.getClient(BASE_URL, authToken).create(ApiService.class);
    }

}
