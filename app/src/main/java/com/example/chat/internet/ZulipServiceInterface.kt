package com.example.chat.internet

import com.example.chat.entities.Contact
import com.example.chat.internet.responses.GetAllChannelsResponse
import com.example.chat.internet.responses.GetContactsResponse
import com.example.chat.internet.responses.GetMyChannelsResponse
import com.example.chat.internet.responses.GetTopicsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ZulipServiceInterface {

    @GET("streams")
    fun getChannels(): Call<GetAllChannelsResponse>

    @GET("users/me/subscriptions")
    fun getMyChannels(): Call<GetMyChannelsResponse>

    @GET("users/me/{channelId}/topics")
    fun getTopics(@Path("channelId") channelId: Int): Call<GetTopicsResponse>

    @GET("users")
    fun getContacts(): Call<GetContactsResponse>

    @GET("users/me")
    fun getMyProfileDetails(): Call<Contact>
}