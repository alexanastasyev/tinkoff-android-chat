package com.example.chat

import com.example.chat.activities.ChatActivity
import com.example.chat.entities.*
import io.reactivex.Observable
import io.reactivex.Single
import java.util.*

object Database {

    const val THIS_USER_ID = 1234567890L
    const val THIS_USER_NAME = "Alexey Anastasyev"
    const val THIS_USER_AVATAR_URL = "https://sun9-62.userapi.com/impf/c841630/v841630065/113e0/lpOMX1Dm8Ao.jpg?size=225x225&quality=96&sign=5c18b2e9ed3f0f0dd9795f4e37012341&type=album"

    fun getMessagesList(): Observable<Message> {
        return Observable.create{ subscriber ->
            Thread.sleep(3000)
            subscriber.onNext(
                Message("Hello, world!", "John Smith", Date(10000), 1, 1)
            )
            subscriber.onNext(
                Message("Look\n" +
                        "If you had\n" +
                        "One shot\n" +
                        "Or one opportunity\n" +
                        "To seize everything you ever wanted\n" +
                        "In one moment\n" +
                        "Would you capture it\n" +
                        "Or just let it slip?", "Marshall Bruce Mathers III", Date(20000), 2, 2,
                    reactions = arrayListOf(
                        Reaction(Emoji.FACE_IN_LOVE, 3, arrayListOf(1, 3, ChatActivity.THIS_USER_ID)),
                        Reaction(Emoji.FACE_WITH_SUNGLASSES, 3, arrayListOf(1, 2, 3)),
                        Reaction(Emoji.FACE_SMILING, 4, arrayListOf(1, 2, 3, 4))
                    ))
            )
            subscriber.onNext(
                Message("Nice text, bro", "Dr Dre", Date(30000), 3, 3)
            )
            subscriber.onNext(
                Message("I agree", "Alexey Anastasyev", Date(40000), ChatActivity.THIS_USER_ID, 4)
            )
            subscriber.onNext(
                Message("Wanna apple?", "Steve Jobs", Date(700_000_000_000), 4, 5,
                    avatarUrl = "https://cdn.vox-cdn.com/thumbor/gD8CFUq4EEdI8ux04KyGMmuIgcA=/0x86:706x557/920x613/filters:focal(0x86:706x557):format(webp)/cdn.vox-cdn.com/imported_assets/847184/stevejobs.png")
            )
            subscriber.onNext(
                Message("When something is important enough, you do it even if the odds are not in your favor.", "Elon Musk", Date(701_000_000_000), 5, 6,
                    avatarUrl = "https://upload.wikimedia.org/wikipedia/commons/8/85/Elon_Musk_Royal_Society_%28crop1%29.jpg")
            )
        }
    }

    fun getAllChannels(): Observable<Channel> {
        return Observable.create{ subscriber ->
            for (i in 1..10) {
                Thread.sleep(100)
                val channel = Channel("Channel $i", i.toLong())
                subscriber.onNext(channel)
            }
            subscriber.onComplete()
        }
    }

    fun getMyChannels(): Observable<Channel> {
        return Observable.create{ subscriber ->
            for (i in 1..10 step 3) {
                Thread.sleep(300)
                val channel = Channel("Channel $i", i.toLong())
                subscriber.onNext(channel)
            }
        }
    }

    fun getContacts() : Single<List<Contact>> {
        return Single.create { subscriber ->
            Thread.sleep(3000)
            val contacts = listOf(
                    Contact(
                            "Sherlock Holmes",
                            "https://aif-s3.aif.ru/images/020/856/92c446222800f644b2a57f05a8025a9b.jpg",
                            true
                    ),
                    Contact(
                            "John Watson",
                            "https://cdn.fishki.net/upload/post/2017/12/03/2447213/tn/4de61c308551534ae848c984a4d7cb74.jpg",
                            false
                    )
            )
            subscriber.onSuccess(contacts)
        }
    }

    fun getProfileDetails(): Single<Contact> {
        return Single.create { subscriber ->
            Thread.sleep(3000)
            val profile = Contact(
                    "Alexey Anastasyev",
                    "https://assets.gitlab-static.net/uploads/-/system/user/avatar/8174750/avatar.png",
                true
            )
            subscriber.onSuccess(profile)
        }
    }

    fun getTopics(channelName: String): Single<List<Topic>> {
        return Single.create{ subscriber ->
            Thread.sleep(100)
            val topics = listOf(
                Topic("First topic of $channelName"),
                Topic("Second topic of $channelName"),
                Topic("Third topic of $channelName")
            )
            subscriber.onSuccess(topics)
        }
    }
}