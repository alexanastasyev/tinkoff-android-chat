package com.example.chat.internet

import com.example.chat.activities.ChatActivity
import com.example.chat.entities.*
import com.example.chat.internet.responses.GetAllChannelsResponse
import com.example.chat.internet.responses.GetContactsResponse
import com.example.chat.internet.responses.GetMyChannelsResponse
import com.example.chat.internet.responses.GetTopicsResponse
import io.reactivex.Observable
import io.reactivex.Single
import java.util.*

object ZulipService {

    fun getAllChannels(): GetAllChannelsResponse? {
        val zulipService = RetrofitZulipService.getInstance()
        val response = zulipService.getChannels().execute().body()
        return response
    }

    fun getMyChannels(): GetMyChannelsResponse? {
        val zulipService = RetrofitZulipService.getInstance()
        val response = zulipService.getMyChannels().execute().body()
        return response
    }

    fun getTopics(channelId: Int): GetTopicsResponse? {
        val zulipService = RetrofitZulipService.getInstance()
        val response = zulipService.getTopics(channelId).execute().body()
        return response
    }

    fun getContacts(): GetContactsResponse? {
        val zulipService = RetrofitZulipService.getInstance()
        val response = zulipService.getContacts().execute().body()
        return response
    }

    fun getProfileDetails(): Contact? {
        val zulipService = RetrofitZulipService.getInstance()
        val response = zulipService.getMyProfileDetails().execute().body()
        return response
    }

    fun getMessagesList(): Observable<Message> {
        return Observable.create { subscriber ->
            Thread.sleep(3000)
            subscriber.onNext(
                Message("Hello, world!", "John Smith", Date(10000), 1, 1)
            )
            subscriber.onNext(
                Message(
                    "Look\n" +
                            "If you had\n" +
                            "One shot\n" +
                            "Or one opportunity\n" +
                            "To seize everything you ever wanted\n" +
                            "In one moment\n" +
                            "Would you capture it\n" +
                            "Or just let it slip?", "Marshall Bruce Mathers III", Date(20000), 2, 2,
                    reactions = arrayListOf(
                        Reaction(
                            Emoji.FACE_IN_LOVE,
                            3,
                            arrayListOf(1, 3, ChatActivity.THIS_USER_ID)
                        ),
                        Reaction(Emoji.FACE_WITH_SUNGLASSES, 3, arrayListOf(1, 2, 3)),
                        Reaction(Emoji.FACE_SMILING, 4, arrayListOf(1, 2, 3, 4))
                    )
                )
            )
            subscriber.onNext(
                Message("Nice text, bro", "Dr Dre", Date(30000), 3, 3)
            )
            subscriber.onNext(
                Message("I agree", "Alexey Anastasyev", Date(40000), ChatActivity.THIS_USER_ID, 4)
            )
            subscriber.onNext(
                Message(
                    "Wanna apple?", "Steve Jobs", Date(700_000_000_000), 4, 5,
                    avatarUrl = "https://cdn.vox-cdn.com/thumbor/gD8CFUq4EEdI8ux04KyGMmuIgcA=/0x86:706x557/920x613/filters:focal(0x86:706x557):format(webp)/cdn.vox-cdn.com/imported_assets/847184/stevejobs.png"
                )
            )
            subscriber.onNext(
                Message(
                    "When something is important enough, you do it even if the odds are not in your favor.",
                    "Elon Musk",
                    Date(701_000_000_000),
                    5,
                    6,
                    avatarUrl = "https://upload.wikimedia.org/wikipedia/commons/8/85/Elon_Musk_Royal_Society_%28crop1%29.jpg"
                )
            )
        }
    }
}