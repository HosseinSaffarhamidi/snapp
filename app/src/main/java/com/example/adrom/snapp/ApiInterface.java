package com.example.adrom.snapp;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface ApiInterface {

//    String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjVkYjc3YjNiN2YwMWNiMWU4OGFhYjVhMSIsImlhdCI6MTU3MjMxMDczMCwiZXhwIjoxNTcyMzExMzMwfQ.k8xLPW29eE6PZIrfwf-iBue57qhlqGDtFVYuWvV14YA";
//    @GET("user/test")
//    @Headers("x-access-token:"+token)
//    Call<String> send_data();

    @FormUrlEncoded
    @POST("user/register")
    Call<ServerData.register> register(@Field("name") String name,@Field("mobile") String mobile,@Field("password") String password);

    @FormUrlEncoded
    @POST("user/active_mobile_number")
    Call<ServerData.active> active_mobile_number(@Field("active_code") String name,@Header("x-access-token") String token);

    @FormUrlEncoded
    @POST("driver/near_driver")
    Call<List<ServerData.location_driver>> send_location(@Field("lat") double lat, @Field("lng") double lng);



    @GET("maps/api/directions/json")
    Call<Place.routes> get_directions(@Query("origin") String origin,@Query("destination") String destination,@Query("sensor") String sensor,@Query("mode") String mode,@Query("key") String key);


    @FormUrlEncoded
    @POST("driver/request_driver")
    Call<ServerData.driver_info> request_driver(@Field("lat") double lat,@Field("lng") double lng);



    @GET("maps/api/geocode/json")
    Call<LocationAddress.results> get_directions(@Query("latlng") String latlng,@Query("sensor") String sensor,@Query("language") String language,@Query("key") String key);

    @GET("user/get_service")
    Call<List<ServerData.get_service>> get_service(@Header("x-access-token") String token,@Query("page") int page);

    @FormUrlEncoded
    @POST("user/login")
    Call<ServerData.login> login(@Field("mobile") String mobile,@Field("password") String password);

    @FormUrlEncoded
    @POST("get_service_price")
    Call<ServerData.Service_Price> get_service_price(@Field("lat") String lat,@Field("lng") String lng);

    @GET("user/check_runing_service")
    Call<ServerData.runing_service> runing_service(@Header("x-access-token") String token);

    @FormUrlEncoded
    @POST("user/cancel_service")
    Call<String> cancel_service(@Header("x-access-token") String tokent,@Field("id") String id);

    @FormUrlEncoded
    @POST("user/add_item_service")
    Call<String> add_item_service(@Header("x-access-token") String token,@Field("service_id") String service_id,@Field("price") int price,@Field("data") JSONObject jsonObject);

    @FormUrlEncoded
    @POST("user/payment")
    Call<ServerData.payment_data> payment(@Header("x-access-token") String token,@Field("price") int price,@Field("service_id") String service_id);


    @GET("user/inventory")
    Call<String> inventory(@Header("x-access-token") String token);


    @FormUrlEncoded
    @POST("user/add_score")
    Call<String> add_score(@Header("x-access-token") String token,@Field("service_id") String service_id,@Field("score") float score);


}
