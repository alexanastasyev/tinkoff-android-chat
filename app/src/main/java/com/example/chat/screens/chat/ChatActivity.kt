package com.example.chat.screens.chat

import android.os.Build
import android.os.Bundle
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
import com.example.chat.recycler.Adapter
import com.example.chat.recycler.ChatHolderFactory
import com.example.chat.recycler.ViewTyped
import com.example.chat.recycler.converters.convertMessageToUi
import com.example.chat.recycler.uis.MessageUi
import com.example.chat.views.EmojiView
import com.example.chat.views.MessageViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.reactivex.disposables.CompositeDisposable
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity(), ChatView {

    companion object {
        const val THIS_USER_ID = ThisUserInfo.THIS_USER_ID.toLong()
        const val THIS_USER_NAME = ThisUserInfo.THIS_USER_NAME
        const val THIS_USER_AVATAR_URL = ThisUserInfo.THIS_USER_AVATAR_URL

        const val TOPIC_KEY = "topic"
        const val CHANNEL_KEY = "channel"
    }

    private val disposeBag = CompositeDisposable()

    private lateinit var messageUis: ArrayList<ViewTyped>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: Adapter<ViewTyped>
    private lateinit var holderFactory: ChatHolderFactory
    private lateinit var clickedMessageViewGroup: MessageViewGroup
    private lateinit var emojisDialog: BottomSheetDialog
    private lateinit var progressBar: ProgressBar

    private var topicName = ""
    private var channelName = ""

    private var isBeingUpdated = false
    private var justHaveUpdated = false

    private lateinit var presenter: ChatActivityPresenter

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

        presenter = ChatActivityPresenter(this, this, channelName, topicName)
        presenter.loadData()

        messageUis = arrayListOf()

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

        progressBar = findViewById(R.id.progressBarMessagesUploading)
        progressBar.visibility = View.GONE
        recyclerView.addOnScrollListener(getRecyclerScrollListener())
    }

    private fun getRecyclerScrollListener(): RecyclerView.OnScrollListener {
        return object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy < -5) {
                    justHaveUpdated = false
                }
                val position = (recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                if (position <= 5 && !isBeingUpdated && !justHaveUpdated) {
                    progressBar.visibility = View.VISIBLE
                    isBeingUpdated = true
                    presenter.loadNextMessages()
                }
            }
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
                presenter.removeReaction(messageViewGroup, emojiView)
            } else {
                presenter.sendReaction(messageViewGroup, emojiView)
            }
        }
    }

    private fun getIndexOfEmojiView(messageViewGroup: MessageViewGroup, emojiView: EmojiView): Int {
        return messageViewGroup.emojisLayout.indexOfChild(emojiView)
    }

    private fun getIndexOfMessage(messageViewGroup: MessageViewGroup): Int {
        var messageIndex = -1
        for (i in messageUis.indices) {
            if ((messageUis[i] as MessageUi).messageId == messageViewGroup.messageId) {
                messageIndex = i
            }
        }
        return messageIndex
    }

    private fun emojiHasVotes(emojiView: EmojiView): Boolean {
        return emojiView.amount > 0
    }

    private fun messageHasReactions(messageViewGroup: MessageViewGroup): Boolean {
        return messageViewGroup.emojisLayout.childCount > 1
    }

    private fun getOnPlusClickListener(messageViewGroup: MessageViewGroup): View.OnClickListener {
        return View.OnClickListener {
            showEmojisDialog(messageViewGroup)
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
                val dateAsStringOfCurrent = (messageUis[currentIndex] as MessageUi).date
                val dateAsStringOfPrevious = (messageUis[previousIndex] as MessageUi).date
                dateAsStringOfCurrent != dateAsStringOfPrevious
            }
        }
    }

    private fun setClickListenerForSendImage() {
        findViewById<ImageView>(R.id.imageSend).setOnClickListener {
            val newMessage = generateNewMessage()
            if (newMessage.text.isNotEmpty()) {
                presenter.sendMessage(newMessage)
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
            text = text,
            author = author,
            date = date,
            authorId = authorId,
            messageId = messageId,
            avatarUrl = avatarUrl,
            reactions = reactions,
            channelName = channelName.substring(1),
            topicName = topicName
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
            presenter.sendReaction(messageViewGroup, emojiView, true)
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

    override fun onDestroy() {
        presenter.disposeDisposable()
        disposeBag.clear()
        super.onDestroy()
    }

    override fun addMessages(newMessages: List<Message>, addToTop: Boolean) {
        if (addToTop) {
            messageUis = ((convertMessageToUi(newMessages.subList(0, newMessages.size - 1)) + messageUis) as ArrayList<ViewTyped>)
            adapter.items = messageUis
        } else {
            messageUis.addAll(convertMessageToUi(newMessages))
            recyclerView.scrollToPosition(adapter.itemCount - 1)
        }
        adapter.notifyDataSetChanged()
        findViewById<ConstraintLayout>(R.id.layoutContent).visibility = View.VISIBLE
        findViewById<ProgressBar>(R.id.progressBarChat).visibility = View.GONE

        progressBar.visibility = View.GONE
        isBeingUpdated = false
        justHaveUpdated = true
    }

    override fun addSentMessage(message: Message) {
        messageUis.addAll(convertMessageToUi(listOf(message)))
        recyclerView.scrollToPosition(adapter.itemCount - 1)
        adapter.notifyDataSetChanged()
        clearEditText()
    }

    override fun removeAllMessages() {
        messageUis.clear()
        adapter.notifyDataSetChanged()
    }

    override fun createNewEmojiReaction(messageViewGroup: MessageViewGroup, emojiView: EmojiView) {

        val messageIndex = getIndexOfMessage(messageViewGroup)

        (messageUis[messageIndex] as MessageUi).reactions.add(Reaction(emojiView.emoji, 1, arrayListOf(ThisUserInfo.THIS_USER_ID.toLong())))

        val newEmojiView = createNewEmojiView(emojiView.emoji)
        newEmojiView.setOnClickListener(
            getOnClickListenerForEmojiView(
                messageViewGroup
            )
        )
        messageViewGroup.addEmojiView(newEmojiView)

        adapter.items = messageUis
        adapter.notifyDataSetChanged()
    }

    override fun addEmojiReaction(messageViewGroup: MessageViewGroup, emojiView: EmojiView) {
        emojiView.isSelected = true
        emojiView.amount += 1

        val messageIndex = getIndexOfMessage(messageViewGroup)
        val emojiIndex = getIndexOfEmojiView(messageViewGroup, emojiView)

        (messageUis[messageIndex] as MessageUi).reactions[emojiIndex].reactedUsersId.add(
            ThisUserInfo.THIS_USER_ID.toLong()
        )
        (messageUis[messageIndex] as MessageUi).reactions[emojiIndex].amount += 1

        adapter.items = messageUis
        adapter.notifyDataSetChanged()
    }

    override fun removeEmojiReaction(messageViewGroup: MessageViewGroup, emojiView: EmojiView) {
        emojiView.isSelected = false
        emojiView.amount -= 1

        val messageIndex = getIndexOfMessage(messageViewGroup)
        val emojiIndex = getIndexOfEmojiView(messageViewGroup, emojiView)

        (messageUis[messageIndex] as MessageUi).reactions[emojiIndex].reactedUsersId.remove(
            ThisUserInfo.THIS_USER_ID.toLong()
        )
        (messageUis[messageIndex] as MessageUi).reactions[emojiIndex].amount -= 1

        if (!emojiHasVotes(emojiView)) {
            messageViewGroup.removeEmojiView(emojiView)
            (messageUis[messageIndex] as MessageUi).reactions.removeAt(emojiIndex)
        }

        if (!messageHasReactions(messageViewGroup)) {
            messageViewGroup.emojisLayout.removeAllViews()
        }

        refreshSelectedEmojis(messageViewGroup)

        adapter.items = messageUis
        adapter.notifyDataSetChanged()
    }

    override fun showError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun refreshSelectedEmojis(messageViewGroup: MessageViewGroup) {
        for (i in 0 until messageViewGroup.emojisLayout.childCount - 1) {
            val emojiView = messageViewGroup.emojisLayout.getChildAt(i) as EmojiView
            val messageIndex = getIndexOfMessage(messageViewGroup)

            if ((messageUis[messageIndex] as MessageUi).reactions[i].reactedUsersId.contains<Long>(
                    ThisUserInfo.THIS_USER_ID.toLong()
                )) {
                emojiView.isSelected = true
            }
        }
    }
}