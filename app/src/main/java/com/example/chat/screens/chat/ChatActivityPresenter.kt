package com.example.chat.screens.chat

import androidx.room.Room
import com.example.chat.R
import com.example.chat.database.AppDatabase
import com.example.chat.entities.Message
import com.example.chat.internet.ZulipService
import com.example.chat.views.EmojiView
import com.example.chat.views.MessageViewGroup
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ChatActivityPresenter(private val chatView: ChatView, private val activity: ChatActivity, private val channelName: String, private val topicName: String) {
    private var currentFirstMessageId = 10000000000000000
    private var lastMessageId = -1

    private val compositeDisposable = CompositeDisposable()

    private val db = Room.databaseBuilder(
        activity,
        AppDatabase::class.java,
        "database"
    ).fallbackToDestructiveMigration().build()

    fun loadData() {
        loadMessagesFromDatabase()
        loadFirstMessagesFromServer()
        subscribeToUpdates()
    }

    private fun loadMessagesFromDatabase() {
        val databaseDisposable = Single.fromCallable {
            db.messageDao().getByChannelAndTopic(channelName.substring(1), topicName) as ArrayList<Message>
        }
            .subscribeOn(Schedulers.io())
            .observeOn((AndroidSchedulers.mainThread()))
            .subscribe({ messagesFromDatabase ->
                if (messagesFromDatabase.isNotEmpty()) {
                    chatView.addMessages(messagesFromDatabase, false)
                    lastMessageId = messagesFromDatabase.last().messageId.toInt()
                }
            }, {
                chatView.showError(activity.getString(R.string.cant_load_messages))
            })
        compositeDisposable.add(databaseDisposable)
    }

    private fun loadFirstMessagesFromServer() {
        val messagesDisposable = ZulipService.getMessages(
            topicName,
            channelName
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ messagesFromServer ->
                if (isFirstLoading()) {
                    chatView.removeAllMessages()
                    addMessagesToDatabase(messagesFromServer)
                    lastMessageId = messagesFromServer.last().messageId.toInt()
                }

                chatView.addMessages(messagesFromServer, false)

                currentFirstMessageId = messagesFromServer[0].messageId
            }, {
                chatView.showError(activity.getString(R.string.cant_load_messages))
            })
        compositeDisposable.add(messagesDisposable)
    }

    private fun addMessagesToDatabase(messages: List<Message>) {
        val databaseDisposable = Single.just(Unit)
            .observeOn(Schedulers.io())
            .subscribe({
                for (message in messages) {
                    if (!(db.messageDao().getAll().map{it.messageId}.contains(message.messageId))){
                        db.messageDao().insert(message)
                    }
                }
            }, {
                chatView.showError(activity.getString(R.string.cant_save_messages))
            })
        compositeDisposable.add(databaseDisposable)
    }

    fun loadNextMessages() {
        val messagesDisposable = ZulipService.getMessages(
            topicName,
            channelName,
            currentFirstMessageId
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ messagesFromServer ->
                chatView.addMessages(messagesFromServer, true)

                currentFirstMessageId = messagesFromServer[0].messageId
            }, {
                chatView.showError(activity.getString(R.string.cant_load_messages))
            })
        compositeDisposable.add(messagesDisposable)
    }

    private fun isFirstLoading(): Boolean {
        return currentFirstMessageId == 10000000000000000
    }

    private fun subscribeToUpdates() {
        val updateMessagesDisposable = Single.just(Unit)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread())
            .subscribe({
                while (!activity.isDestroyed) {
                    Thread.sleep(5000)
                    val checkNewMessageDisposable = Single.fromCallable {
                        ZulipService.checkNewMessages(
                            topicName,
                            channelName,
                            lastMessageId
                        )
                    }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ newMessages ->
                            if (newMessages.size > 1) {
                                chatView.addMessages(newMessages.subList(1, newMessages.size), false)
                                lastMessageId = newMessages.last().messageId.toInt()
                            }
                        }, {
                            chatView.showError(activity.getString(R.string.cant_load_messages))
                        })
                    compositeDisposable.add(checkNewMessageDisposable)
                }
            }, {
                chatView.showError(activity.getString(R.string.cant_load_messages))
            })
        compositeDisposable.add(updateMessagesDisposable)
    }

    fun sendMessage(message: Message) {
        val sendMessageDisposable = Single.fromCallable{ZulipService.sendMessage(
            channelName.substring(1), topicName, message.text
        )}
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ newMessageId ->
                if (newMessageId > 0) {
                    message.messageId = newMessageId.toLong()
                    chatView.addSentMessage(message)
                    lastMessageId = newMessageId
                    addMessagesToDatabase(listOf(message))
                } else {
                    chatView.showError(activity.getString(R.string.error_send_message))
                }
            }, {
                chatView.showError(activity.getString(R.string.error_send_message))
            })
        compositeDisposable.add(sendMessageDisposable)
    }

    fun sendReaction(messageViewGroup: MessageViewGroup, emojiView: EmojiView, isFirst: Boolean = false) {
        val addReactionDisposable = Single.fromCallable{ZulipService.addReaction(
            messageViewGroup.messageId.toInt(),
            emojiView.emoji
        )}
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ isSuccessful ->
                if (isSuccessful) {
                    if (isFirst) {
                        chatView.createNewEmojiReaction(messageViewGroup, emojiView)
                    } else {
                        chatView.addEmojiReaction(messageViewGroup, emojiView)
                    }
                } else {
                    chatView.showError(activity.getString(R.string.cant_send_reaction))
                }
            }, {
                chatView.showError(activity.getString(R.string.cant_send_reaction))
            })
        compositeDisposable.add(addReactionDisposable)
    }

    fun removeReaction(messageViewGroup: MessageViewGroup, emojiView: EmojiView) {
        val removeReactionDisposable = Single.fromCallable{ZulipService.removeReaction(
            messageViewGroup.messageId.toInt(),
            emojiView.emoji
        )}
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ isSuccessful ->
                if (isSuccessful) {
                    chatView.removeEmojiReaction(messageViewGroup, emojiView)
                }
            }, {
                chatView.showError(activity.getString(R.string.cant_send_reaction))
            })
        compositeDisposable.add(removeReactionDisposable)
    }

    fun disposeDisposable() {
        compositeDisposable.dispose()
    }
}