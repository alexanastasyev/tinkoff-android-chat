package com.example.chat.activities

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.setPadding
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chat.*
import com.example.chat.entities.Emoji
import com.example.chat.entities.Message
import com.example.chat.entities.Reaction
import com.example.chat.internet.ZulipService
import com.example.chat.recycler.Adapter
import com.example.chat.recycler.BaseAdapter
import com.example.chat.recycler.ChatHolderFactory
import com.example.chat.recycler.ViewTyped
import com.example.chat.recycler.converters.convertMessageToUi
import com.example.chat.views.EmojiView
import com.example.chat.views.MessageViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity() {

    companion object {
        const val THIS_USER_ID = ThisUserInfo.THIS_USER_ID.toLong()
        const val THIS_USER_NAME = ThisUserInfo.THIS_USER_NAME
        const val THIS_USER_AVATAR_URL = ThisUserInfo.THIS_USER_AVATAR_URL

        const val TOPIC_KEY = "topic"
        const val CHANNEL_KEY = "channel"
        const val MESSAGES_LIST_KEY = "messages"
    }

    private val disposeBag = CompositeDisposable()

    private lateinit var messages: ArrayList<Message>
    private lateinit var messageUis: ArrayList<ViewTyped>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: Adapter<ViewTyped>
    private lateinit var holderFactory: ChatHolderFactory
    private lateinit var clickedMessageViewGroup: MessageViewGroup
    private lateinit var emojisDialog: BottomSheetDialog

    private var topicName = ""
    private var channelName = ""

    private var lastMessageId = 10000000000000000
    private var isBeingUpdated = false

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_activity)

        findViewById<ConstraintLayout>(R.id.layoutContent).visibility = View.INVISIBLE

        val extras = intent.extras
        if (extras != null) {
            topicName = extras.getString(TOPIC_KEY).toString()
            channelName = extras.getString(CHANNEL_KEY).toString()
            val toolbar = findViewById<Toolbar>(R.id.toolbarChat)
            toolbar?.title = ""
            val textViewTitle = toolbar?.findViewById<TextView>(R.id.titleChat)
            textViewTitle?.text = String.format("%s of %s", topicName, channelName)
            setSupportActionBar(toolbar)
        }

        restoreOrReceiveMessages(savedInstanceState)
        messageUis = convertMessageToUi(messages) as ArrayList<ViewTyped>

        holderFactory = ChatHolderFactory(
            action = getActionForMessageViewGroups(),
            shouldShowDate = getShouldDateBeShown()
        )
        adapter = Adapter(holderFactory)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.adapter = adapter
        adapter.items = messageUis

        setClickListenerForSendImage()
        setEditTextListener()

        recyclerView.scrollToPosition(adapter.itemCount - 1)

        findViewById<ImageView>(R.id.arrowBack).setOnClickListener {
            this.finish()
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val position = (recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                if (position == 5 && !isBeingUpdated) {
                    isBeingUpdated = true
                    val messagesDisposable = ZulipService.getMessages(
                        topicName,
                        channelName,
                        lastMessageId
                    )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ messagesFromServer ->
                            if (messagesFromServer.size > 1) {
                                recyclerView.scrollToPosition(adapter.itemCount - 1)

                                messages = (messagesFromServer + messages) as ArrayList<Message>
                                messageUis = convertMessageToUi(messages) as ArrayList<ViewTyped>
                                adapter.items = messageUis
                                adapter.notifyDataSetChanged()

                                lastMessageId = messages[0].messageId

                            }
                        }, {
                            Toast.makeText(
                                applicationContext,
                                getString(R.string.error_receive_messages),
                                Toast.LENGTH_SHORT
                            ).show()
                        })
                    disposeBag.add(messagesDisposable)
                    isBeingUpdated = false
                }
            }
        })
    }

    private fun restoreOrReceiveMessages(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            val messagesDisposable = ZulipService.getMessages(
                topicName,
                channelName
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ messagesFromServer ->
                    messages.clear()
                    messages.addAll(messagesFromServer)
                    messageUis.clear()
                    messageUis.addAll(convertMessageToUi(messages))
                    adapter.items = messageUis
                    recyclerView.scrollToPosition(adapter.itemCount - 1)
                    findViewById<ConstraintLayout>(R.id.layoutContent).visibility = View.VISIBLE
                    findViewById<ProgressBar>(R.id.progressBarChat).visibility = View.GONE

                    lastMessageId = messages[0].messageId
                }, {
                    Toast.makeText(
                        this,
                        getString(R.string.error_receive_messages),
                        Toast.LENGTH_SHORT
                    ).show()
                })
            disposeBag.add(messagesDisposable)
            messages = arrayListOf()
        } else {
            findViewById<ConstraintLayout>(R.id.layoutContent).visibility = View.VISIBLE
            messages = savedInstanceState.getSerializable(MESSAGES_LIST_KEY) as ArrayList<Message>
        }
    }

    private fun getActionForMessageViewGroups() : (View) -> Unit {
        return { message ->
            val messageViewGroup = message as MessageViewGroup
            messageViewGroup.setOnClickListenerForEmojiViews(
                getOnClickListenerForEmojiView(
                    messageViewGroup
                )
            )
            messageViewGroup.setOnLongClickListenerForMessages(
                getOnLongClickListenerForMessages(
                    messageViewGroup
                )
            )
            messageViewGroup.setOnPlusClickListener(getOnPlusClickListener(messageViewGroup))
        }
    }

    private fun getOnClickListenerForEmojiView(messageViewGroup: MessageViewGroup): (View) -> Unit {
        return { emoji ->
            val emojiView = emoji as EmojiView

            if (emojiView.isSelected) {
                unselectEmoji(messageViewGroup, emojiView)
            } else {
                selectEmoji(messageViewGroup, emojiView)
            }
        }
    }

    private fun getIndexOfEmojiView(messageViewGroup: MessageViewGroup, emojiView: EmojiView): Int {
        return messageViewGroup.emojisLayout.indexOfChild(emojiView)
    }

    private fun getIndexOfMessage(messageViewGroup: MessageViewGroup): Int {
        var messageIndex = -1
        for (i in messages.indices) {
            if (messages[i].messageId == messageViewGroup.messageId) {
                messageIndex = i
            }
        }
        return messageIndex
    }

    private fun unselectEmoji(messageViewGroup: MessageViewGroup, emojiView: EmojiView) {
        val removeReactionDisposable = Single.fromCallable{ZulipService.removeReaction(
            messageViewGroup.messageId.toInt(),
            emojiView.emoji
        )}
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it) {
                    emojiView.isSelected = false
                    emojiView.amount -= 1

                    if (!emojiHasVotes(emojiView)) {
                        removeEmojiView(messageViewGroup, emojiView)
                    } else {
                        removeReaction(messageViewGroup, emojiView)
                    }

                    if (!messageHasReactions(messageViewGroup)) {
                        messageViewGroup.emojisLayout.removeAllViews()
                    }
                }
            }, {

            })
        disposeBag.add(removeReactionDisposable)
    }

    private fun emojiHasVotes(emojiView: EmojiView): Boolean {
        return emojiView.amount > 0
    }

    private fun removeReaction(messageViewGroup: MessageViewGroup, emojiView: EmojiView) {
        val messageIndex = getIndexOfMessage(messageViewGroup)
        val emojiIndex = getIndexOfEmojiView(messageViewGroup, emojiView)

        messages[messageIndex].reactions[emojiIndex].reactedUsersId.remove(THIS_USER_ID)
        messages[messageIndex].reactions[emojiIndex].amount -= 1
    }

    private fun messageHasReactions(messageViewGroup: MessageViewGroup): Boolean {
        return messageViewGroup.emojisLayout.childCount > 1
    }

    private fun selectEmoji(messageViewGroup: MessageViewGroup, emojiView: EmojiView) {
        val addReactionDisposable = Single.fromCallable{ZulipService.addReaction(
            messageViewGroup.messageId.toInt(),
            emojiView.emoji
        )}
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it) {
                    emojiView.isSelected = true
                    emojiView.amount += 1
                    addReaction(messageViewGroup, emojiView)
                }
            }, {

            })
        disposeBag.add(addReactionDisposable)
    }

    private fun addReaction(messageViewGroup: MessageViewGroup, emojiView: EmojiView) {
        val messageIndex = getIndexOfMessage(messageViewGroup)
        val emojiIndex = getIndexOfEmojiView(messageViewGroup, emojiView)

        messages[messageIndex].reactions[emojiIndex].reactedUsersId.add(THIS_USER_ID)
        messages[messageIndex].reactions[emojiIndex].amount += 1

        messageUis = convertMessageToUi(messages) as ArrayList<ViewTyped>
        adapter.items = messageUis
    }

    private fun removeEmojiView(messageViewGroup: MessageViewGroup, emojiView: EmojiView) {
        messageViewGroup.removeEmojiView(emojiView)
        removeReactionFromMessagesList(messageViewGroup, emojiView)
        refreshSelectedEmojis(messageViewGroup)
        messageViewGroup.setOnPlusClickListener(getOnPlusClickListener(messageViewGroup))
    }

    private fun removeReactionFromMessagesList(
        messageViewGroup: MessageViewGroup,
        emojiView: EmojiView
    ) {
        val messageIndex = getIndexOfMessage(messageViewGroup)

        var indexOfReactionToRemove = -1
        for (i in 0 until messages[messageIndex].reactions.size) {
            if (messages[messageIndex].reactions[i].emoji == emojiView.emoji) {
                indexOfReactionToRemove = i
            }
        }
        (messages[messageIndex].reactions).removeAt(indexOfReactionToRemove)
    }

    private fun getOnPlusClickListener(messageViewGroup: MessageViewGroup): View.OnClickListener {
        return View.OnClickListener {
            showEmojisDialog(messageViewGroup)
        }
    }

    private fun refreshSelectedEmojis(messageViewGroup: MessageViewGroup) {
        for (i in 0 until messageViewGroup.emojisLayout.childCount - 1) {
            val emojiView = messageViewGroup.emojisLayout.getChildAt(i) as EmojiView
            val emojiIndex = messageViewGroup.emojisLayout.indexOfChild(emojiView)
            val messageIndex = getIndexOfMessage(messageViewGroup)

            if (messages[messageIndex].reactions[emojiIndex].reactedUsersId.contains(THIS_USER_ID)) {
                emojiView.isSelected = true
            }
        }
    }

    private fun getOnLongClickListenerForMessages(messageViewGroup: MessageViewGroup) : View.OnLongClickListener {
        return View.OnLongClickListener {
            showEmojisDialog(messageViewGroup)
            true
        }
    }

    private fun showEmojisDialog(messageViewGroup: MessageViewGroup) {
        val bottomEmojisDialog = layoutInflater.inflate(R.layout.bottom_emojis_sheet, null)
        emojisDialog = BottomSheetDialog(this)
        emojisDialog.setContentView(bottomEmojisDialog)
        clickedMessageViewGroup = messageViewGroup
        emojisDialog.show()
    }

    private fun getShouldDateBeShown() : (View) -> Boolean {
        return { message ->
            val messageViewGroup = message as MessageViewGroup
            val currentIndex = getIndexOfMessage(messageViewGroup)
            val previousIndex = currentIndex - 1
            if (currentIndex == 0) {
                true
            } else {
                val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                val dateAsStringOfCurrent = formatter.format(messages[currentIndex].date)
                val dateAsStringOfPrevious = formatter.format(messages[previousIndex].date)
                dateAsStringOfCurrent != dateAsStringOfPrevious
            }
        }
    }

    private fun setClickListenerForSendImage() {
        findViewById<ImageView>(R.id.imageSend).setOnClickListener {
            val newMessage = generateNewMessage()
            if (newMessage.text.isNotEmpty()) {
                val sendMessageDisposable = Single.fromCallable{ZulipService.sendMessage(
                    channelName.substring(
                        1
                    ), topicName, newMessage.text
                )}
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (it > 0) {
                            newMessage.messageId = it.toLong()
                            messages.add(newMessage)
                            messageUis.add(convertMessageToUi(listOf(newMessage))[0])
                            adapter.items = messageUis
                            clearEditText()
                            recyclerView.scrollToPosition(adapter.itemCount - 1)
                        } else {
                            Toast.makeText(
                                this,
                                getString(R.string.error_send_message),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }, {
                        Toast.makeText(
                            this,
                            getString(R.string.error_send_message),
                            Toast.LENGTH_SHORT
                        ).show()
                    })
                disposeBag.add(sendMessageDisposable)
            }
        }
    }

    private fun generateNewMessage(): Message {
        val editText = findViewById<EditText>(R.id.editText)
        val text = editText.text.toString().trim()
        val author = THIS_USER_NAME
        val date = Calendar.getInstance().time
        val authorId = THIS_USER_ID
        val messageId = -1L
        val avatarUrl = THIS_USER_AVATAR_URL
        val reactions = arrayListOf<Reaction>()

        return Message(
            text,
            author,
            date,
            authorId,
            messageId,
            avatarUrl,
            reactions
        )
    }

    private fun clearEditText() {
        findViewById<EditText>(R.id.editText).text.clear()
    }

    private fun setEditTextListener() {
        findViewById<EditText>(R.id.editText).doOnTextChanged { text, start, before, count ->
            if (text.isNullOrEmpty()) {
                findViewById<ImageView>(R.id.imageSend).setImageResource(R.drawable.close)
            } else {
                findViewById<ImageView>(R.id.imageSend).setImageResource(R.drawable.send)
            }
        }
    }

    // The function is used!
    fun onDialogEmojiClick(view: View) {
        addEmojiView(
            messageViewGroup = clickedMessageViewGroup,
            view as EmojiView
        )
        emojisDialog.dismiss()
    }

    private fun addEmojiView(messageViewGroup: MessageViewGroup, emojiView: EmojiView) {
        if (emojiAlreadyExist(messageViewGroup, emojiView)) {
            messageViewGroup.emojisLayout.children.forEach {
                val existingEmojiView = it as EmojiView
                if (existingEmojiView.emoji.unicode == emojiView.emoji.unicode && !existingEmojiView.isSelected) {
                    existingEmojiView.callOnClick()
                }
            }
        } else {
            val createReactionDisposable = Single.fromCallable{ZulipService.addReaction(
                messageViewGroup.messageId.toInt(),
                emojiView.emoji
            )}
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it) {
                        val newEmojiView = createNewEmojiView(emojiView.emoji)
                        newEmojiView.setOnClickListener(
                            getOnClickListenerForEmojiView(
                                messageViewGroup
                            )
                        )
                        messageViewGroup.addEmojiView(newEmojiView)
                        createReaction(messageViewGroup, emojiView)
                        refreshSelectedEmojis(messageViewGroup)
                    }
                }, {

                })
            disposeBag.add(createReactionDisposable)
        }
    }

    private fun emojiAlreadyExist(messageViewGroup: MessageViewGroup, emojiView: EmojiView): Boolean {
        return messageViewGroup.reactions.map { it.first.unicode }.contains(emojiView.emoji.unicode)
    }

    private fun createNewEmojiView(emoji: Emoji): EmojiView {
        val newEmojiView = EmojiView(this)
        newEmojiView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        newEmojiView.amount = 1
        newEmojiView.emoji = emoji
        newEmojiView.isSelected = true
        newEmojiView.textColor = ContextCompat.getColor(this, R.color.white)
        newEmojiView.background = ContextCompat.getDrawable(this, R.drawable.emoji_view_bg)
        newEmojiView.setPadding(dpToPx(MessageViewGroup.EMOJIS_PADDING_DP))
        return newEmojiView
    }

    private fun createReaction(messageViewGroup: MessageViewGroup, emojiView: EmojiView) {
        val messageIndex = getIndexOfMessage(messageViewGroup)
        messages[messageIndex].reactions.add(
            Reaction(
                emojiView.emoji,
                1,
                arrayListOf(THIS_USER_ID)
            )
        )
        messageUis = convertMessageToUi(messages) as ArrayList<ViewTyped>
        adapter.items = messageUis
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(MESSAGES_LIST_KEY, messages)
    }

    override fun onDestroy() {
        disposeBag.clear()
        super.onDestroy()
    }
}