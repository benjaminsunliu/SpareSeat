package com.example.spareseat.api;

import com.example.spareseat.model.EventResponse;
import com.example.spareseat.model.LoginRequest;
import com.example.spareseat.model.ReservationRequest;
import com.example.spareseat.model.ReservationResponse;
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
import retrofit2.http.Query;

public interface ApiService {
    @POST("api/users/login")
    Call<UserResponse> login(@Body LoginRequest request);

    @POST("api/users/register")
    Call<UserResponse> register(@Body UserRegistrationRequest request);

    @GET("api/events")
    Call<List<EventResponse>> getAllEvents();

    @GET("api/events/{id}")
    Call<EventResponse> getEventById(@Path("id") long id);

    @GET("api/events/organizer/{organizerId}")
    Call<List<EventResponse>> getEventsByOrganizer(@Path("organizerId") long organizerId);

    @POST("api/events/create")
    Call<EventResponse> createEvent(@Body EventCreateRequest request);

    @POST("api/reservations")
    Call<ReservationResponse> createReservation(@Body ReservationRequest request);

    @GET("api/reservations/user/{userId}")
    Call<List<ReservationResponse>> getReservationsForUser(@Path("userId") long userId);

    @DELETE("api/reservations/{reservationId}")
    Call<ReservationResponse> cancelReservation(@Path("reservationId") long reservationId,
                                                @Query("userId") long userId);

    @PUT("api/events/update/{id}")
    Call<EventResponse> updateEvent(@Path("id") long id, @Body EventCreateRequest request);

    @PUT("api/events/cancel/{id}")
    Call<EventResponse> cancelEvent(@Path("id") long id);

    @DELETE("api/events/delete/{id}")
    Call<Void> deleteEvent(@Path("id") long id);
}
