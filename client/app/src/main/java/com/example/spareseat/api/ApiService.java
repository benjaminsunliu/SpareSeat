package com.example.spareseat.api;

import com.example.spareseat.model.EventResponse;
import com.example.spareseat.model.LoginRequest;
import com.example.spareseat.model.UserRegistrationRequest;
import com.example.spareseat.model.UserResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api/users/login")
    Call<UserResponse> login(@Body LoginRequest request);

    @POST("api/users/register")
    Call<UserResponse> register(@Body UserRegistrationRequest request);

    @GET("api/events")
    Call<List<EventResponse>> getAllEvents();
}
