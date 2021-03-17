package com.example.chat

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.setPadding
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chat.recycler.Adapter
import com.example.chat.recycler.ChatHolderFactory
import com.example.chat.recycler.ViewTyped
import com.example.chat.recycler.messageToUi
import com.example.chat.views.EmojiView
import com.example.chat.views.MessageViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    companion object {
        const val THIS_USER_ID = 1234567890L
        const val THIS_USER_NAME = "Alexey Anastasyev"
        const val THIS_USER_AVATAR_URL = "https://sun9-62.userapi.com/impf/c841630/v841630065/113e0/lpOMX1Dm8Ao.jpg?size=225x225&quality=96&sign=5c18b2e9ed3f0f0dd9795f4e37012341&type=album"

        const val MESSAGES_LIST_KEY = "messages"
        private const val DIVIDER_FOR_GENERATING_ID = 1_000_000_000
    }

    private lateinit var messages: ArrayList<Message>
    private lateinit var messageUis: ArrayList<ViewTyped>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: Adapter<ViewTyped>
    private lateinit var clickedMessageViewGroup: MessageViewGroup
    private lateinit var emojisDialog: BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_activity)

        restoreOrReceiveMessages(savedInstanceState)
        messageUis = messageToUi(messages) as ArrayList<ViewTyped>

        val holderFactory = ChatHolderFactory(
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
    }

    private fun restoreOrReceiveMessages(savedInstanceState: Bundle?) {
        messages = if (savedInstanceState == null) {
            ArrayList(getMessagesList())
        } else {
            savedInstanceState.getSerializable(MESSAGES_LIST_KEY) as ArrayList<Message>
        }
    }

    private fun getMessagesList(): List<Message> {
        return listOf(
                Message("Hello, world!", "John Smith", Date(10000), 1, 1),

                Message("Look\n" +
                        "If you had\n" +
                        "One shot\n" +
                        "Or one opportunity\n" +
                        "To seize everything you ever wanted\n" +
                        "In one moment\n" +
                        "Would you capture it\n" +
                        "Or just let it slip?", "Marshall Bruce Mathers III", Date(20000), 2, 2,
                        reactions = arrayListOf(
                                Reaction(Emoji.FACE_IN_LOVE, 3, arrayListOf(1, 3, THIS_USER_ID)),
                                Reaction(Emoji.FACE_WITH_SUNGLASSES, 3, arrayListOf(1, 2, 3)),
                                Reaction(Emoji.FACE_SMILING, 4, arrayListOf(1, 2, 3, 4))
                        )),

                Message("Nice text, bro", "Dr Dre", Date(30000), 3, 3),

                Message("Wanna apple?", "Steve Jobs", Date(700_000_000_000), 4, 4,
                        avatarUrl = "https://cdn.vox-cdn.com/thumbor/gD8CFUq4EEdI8ux04KyGMmuIgcA=/0x86:706x557/920x613/filters:focal(0x86:706x557):format(webp)/cdn.vox-cdn.com/imported_assets/847184/stevejobs.png"),
                Message("When something is important enough, you do it even if the odds are not in your favor.", "Elon Musk", Date(701_000_000_000), 5, 5,
                        avatarUrl = "https://upload.wikimedia.org/wikipedia/commons/8/85/Elon_Musk_Royal_Society_%28crop1%29.jpg")
        )
    }

    private fun getActionForMessageViewGroups() : (View) -> Unit {
        return { message ->
            val messageViewGroup = message as MessageViewGroup
            messageViewGroup.setOnClickListenerForEmojiViews(getOnClickListenerForEmojiView(messageViewGroup))
            messageViewGroup.setOnLongClickListenerForMessages(getOnLongClickListenerForMessages(messageViewGroup))
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
        emojiView.isSelected = true
        emojiView.amount += 1

        addReaction(messageViewGroup, emojiView)
    }

    private fun addReaction(messageViewGroup: MessageViewGroup, emojiView: EmojiView) {
        val messageIndex = getIndexOfMessage(messageViewGroup)
        val emojiIndex = getIndexOfEmojiView(messageViewGroup, emojiView)

        messages[messageIndex].reactions[emojiIndex].reactedUsersId.add(THIS_USER_ID)
        messages[messageIndex].reactions[emojiIndex].amount += 1
    }

    private fun removeEmojiView(messageViewGroup: MessageViewGroup, emojiView: EmojiView) {
        messageViewGroup.removeEmojiView(emojiView)
        removeReactionFromMessagesList(messageViewGroup, emojiView)
        refreshSelectedEmojis(messageViewGroup)
        messageViewGroup.setOnPlusClickListener(getOnPlusClickListener(messageViewGroup))
    }

    private fun removeReactionFromMessagesList(messageViewGroup: MessageViewGroup, emojiView: EmojiView) {
        val messageIndex = getIndexOfMessage(messageViewGroup)

        var indexOfReactionToRemove = -1
        for (i in 0 until messages[messageIndex].reactions.size) {
            if (messages[messageIndex].reactions[i].emoji == emojiView.emoji) {
                indexOfReactionToRemove = i
            }
        }
        messages[messageIndex].reactions.removeAt(indexOfReactionToRemove)
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
                messages.add(newMessage)
                messageUis.add(messageToUi(listOf(newMessage))[0])
                adapter.items.add(messageToUi(listOf(newMessage))[0])
                clearEditText()
                recyclerView.scrollToPosition(adapter.itemCount - 1)
            }
        }
    }

    private fun generateNewMessage(): Message {
        val editText = findViewById<EditText>(R.id.editText)
        val text = editText.text.toString().trim()
        val author = THIS_USER_NAME
        val date = Calendar.getInstance().time
        val authorId = THIS_USER_ID
        val messageId = "${THIS_USER_ID % DIVIDER_FOR_GENERATING_ID}${date.time % DIVIDER_FOR_GENERATING_ID}".toLong()
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
            val newEmojiView = createNewEmojiView(emojiView.emoji)
            newEmojiView.setOnClickListener(getOnClickListenerForEmojiView(messageViewGroup))
            messageViewGroup.addEmojiView(newEmojiView)
            createReaction(messageViewGroup, emojiView)
            refreshSelectedEmojis(messageViewGroup)
        }
    }

    private fun emojiAlreadyExist(messageViewGroup: MessageViewGroup, emojiView: EmojiView): Boolean {
        return messageViewGroup.reactions.map { it.first.unicode }.contains(emojiView.emoji.unicode)
    }

    private fun createNewEmojiView(emoji: Emoji): EmojiView {
        val newEmojiView = EmojiView(this)
        newEmojiView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        newEmojiView.amount = 1
        newEmojiView.emoji = emoji
        newEmojiView.isSelected = true
        newEmojiView.textColor = ContextCompat.getColor(this, R.color.white)
        newEmojiView.background = ContextCompat.getDrawable(this, R.drawable.emoji_view_bg)
        newEmojiView.setPadding(dpToPx(MessageViewGroup.EMOJIS_PADDING))
        return newEmojiView
    }

    private fun createReaction(messageViewGroup: MessageViewGroup, emojiView: EmojiView) {
        val messageIndex = getIndexOfMessage(messageViewGroup)
        messages[messageIndex].reactions.add(Reaction(emojiView.emoji, 1, arrayListOf(THIS_USER_ID)))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(MESSAGES_LIST_KEY, messages)
    }

}