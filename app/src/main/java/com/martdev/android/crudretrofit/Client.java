package com.martdev.android.crudretrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class Client {
    private static final String BASE_URL = "https://jsonplaceholder.typicode.com/";

    private static Retrofit sRetrofit = null;

    static Retrofit getRetrofit() {
        if (sRetrofit == null) {
            sRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return sRetrofit;
    }
}
