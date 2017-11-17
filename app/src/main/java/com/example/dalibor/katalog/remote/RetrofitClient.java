package com.example.dalibor.katalog.remote;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Employee on 09.11.2017..
 */

public class RetrofitClient {

    private static Retrofit retrofit = null;
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    public static  Retrofit getClient(String baseUrl){



            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();


        return retrofit;

    }

//    public static Retrofit getClient (String baseUrl, String username, String password) {
//        if (retrofit==null) {
//            if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
//
//                String authToken = Credentials.basic(username, password);
//            }
//            retrofit = new Retrofit.Builder()
//                    .baseUrl(baseUrl)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
//
//        }
//        return retrofit;
//    }

    public static  Retrofit getClient (String baseUrl, String authToken) {

            if (!TextUtils.isEmpty(authToken)) {
                AuthenticationInterceptor interceptor = new AuthenticationInterceptor(authToken);
                if (!httpClient.interceptors().contains(interceptor)) {
                    httpClient.addInterceptor(interceptor);
                    retrofit = new Retrofit.Builder()
                            .baseUrl(baseUrl)
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(httpClient.build())
                            .build();
                }
            }




        return retrofit;
    }

}
