package com.martdev.android.crudretrofit;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @GET("posts")
    Call<List<UserInfo>> getUsers();

    @POST("posts")
    Call<UserInfo> saveUser(@Body UserInfo info);

    @PUT("posts/{id}")
    Call<UserInfo> updateUser(@Path("id") int id, @Body UserInfo info);

    @DELETE("posts/{id}")
    Call<UserInfo> deleteUser(@Path("id") int id);
}
