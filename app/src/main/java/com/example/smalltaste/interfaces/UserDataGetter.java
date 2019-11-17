package com.example.smalltaste.interfaces;

import com.example.smalltaste.model.Datum;
import com.example.smalltaste.model.Model;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserDataGetter {

    @GET("api/users")
    Call<Model> getUserData(@Query("page") int pageNumber);

    @PUT("api/users/{id}")
    Call<Datum> updateUserData(@Path("id") int id, @Body Datum responseDTO);

    @PUT("api/users/{id}")
    Call<Datum> addUserData(@Path("id") int id, @Body Datum responseDTO);

    @DELETE("api/users/{id}")
    Call<Datum> deleteUserData(@Path("id") int id);


}


