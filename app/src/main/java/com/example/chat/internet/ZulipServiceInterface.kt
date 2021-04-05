package com.example.chat.internet

import com.example.chat.entities.Contact
import com.example.chat.internet.responses.*
import retrofit2.Call
import retrofit2.http.*

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

    @GET("users/{userId}/presence")
    fun getUserPresence(@Path("userId") userId: Int): Call<GetUserPresenceResponse>

    @FormUrlEncoded
    @POST("messages")
    fun sendMessage(
        @Field("type") type: String,
        @Field("to") channelName: String,
        @Field("content") text: String,
        @Field("topic") topicName: String
    ): Call<SendMessageResponse>

    @FormUrlEncoded
    @POST("messages/{messageId}/reactions")
    fun addReaction(
        @Path("messageId") messageId: Int,
        @Field("emoji_name") emojiName: String,
        @Field("emoji_code") emojiCode: String,
        @Field("reaction_type") reactionType: String
    ): Call<AddReactionResponse>

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "messages/{messageId}/reactions", hasBody = true)
    fun removeReaction(
        @Path("messageId") messageId: Int,
        @Field("emoji_name") emojiName: String,
        @Field("emoji_code") emojiCode: String,
        @Field("reaction_type") reactionType: String
    ): Call<RemoveReactionResponse>
}