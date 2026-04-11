package com.example.spareseat.api;

import com.example.spareseat.model.EventResponse;
import com.example.spareseat.model.LoginRequest;
import com.example.spareseat.model.UserRegistrationRequest;
import com.example.spareseat.model.UserResponse;

import java.util.List;

import com.example.spareseat.model.EventCreateRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @POST("api/users/login")
    Call<UserResponse> login(@Body LoginRequest request);

    @POST("api/users/register")
    Call<UserResponse> register(@Body UserRegistrationRequest request);

    @GET("api/events")
    Call<List<EventResponse>> getAllEvents();

    @GET("api/events/organizer/{organizerId}")
    Call<List<EventResponse>> getEventsByOrganizer(@Path("organizerId") long organizerId);

    @POST("api/events/create")
    Call<EventResponse> createEvent(@Body EventCreateRequest request);

    @PUT("api/events/update/{id}")
    Call<EventResponse> updateEvent(@Path("id") long id, @Body EventCreateRequest request);

    @DELETE("api/events/delete/{id}")
    Call<Void> deleteEvent(@Path("id") long id);
}
