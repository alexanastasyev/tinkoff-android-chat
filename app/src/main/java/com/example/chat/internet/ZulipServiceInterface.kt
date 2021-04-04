package com.example.chat.internet

import com.example.chat.entities.Contact
import com.example.chat.internet.responses.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

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

    @GET("messages")
    fun getMessages(
        @Query("anchor") anchor: String,
        @Query("num_before") numBefore: Int,
        @Query("num_after") numAfter: Int
    ): Call<GetMessagesResponse>
}