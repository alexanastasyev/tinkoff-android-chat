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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chat.recycler.Adapter
import com.example.chat.recycler.ChatHolderFactory
import com.example.chat.recycler.ViewTyped
import com.example.chat.recycler.messageToUi
import com.example.chat.views.EmojiView
import com.example.chat.views.MessageViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var messages: ArrayList<Message>
    private lateinit var messageUis: ArrayList<ViewTyped>
    private lateinit var adapter: Adapter<ViewTyped>
    private lateinit var globalMessageViewGroup: MessageViewGroup
    private lateinit var dialog: BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_activity)

        restoreOrReceiveMessages(savedInstanceState)
        messageUis = messageToUi(messages) as ArrayList<ViewTyped>

        val holderFactory = ChatHolderFactory(
                action = getActionForMessageViewGroups()
        )
        adapter = Adapter(holderFactory)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.adapter = adapter
        adapter.items = messageUis

        setClickListenerForSendImage()
    }

    private fun restoreOrReceiveMessages(savedInstanceState: Bundle?) {
        messages = if (savedInstanceState == null) {
            ArrayList(getMessagesList())
        } else {
            savedInstanceState.getSerializable(MESSAGES_LIST_KEY) as ArrayList<Message>
        }
    }

    private fun getActionForMessageViewGroups() : (View) -> Unit {
        return { message ->
            val messageViewGroup = message as MessageViewGroup
            messageViewGroup.setOnCLickListenerForEmojiViews(getOnCLickListenerForEmojiView(messageViewGroup))
            messageViewGroup.setOnLongClickListenerForMessages(getOnLongClickListenerForMessages(messageViewGroup))
            messageViewGroup.setOnPlusClickListener(getOnPlusClickListener(messageViewGroup))
        }
    }

    private fun getOnCLickListenerForEmojiView(messageViewGroup: MessageViewGroup): (View) -> Unit {
        return { emoji ->
            val emojiView = emoji as EmojiView

            val messageIndex = getIndexOfMessage(messageViewGroup)
            val emojiIndex = messageViewGroup.emojisLayout.indexOfChild(emojiView)

            if (messageIndex in messages.indices) {
                if (emojiIndex in messages[messageIndex].reactions.indices) {

                    if (emojiView.isSelected) {
                        emojiView.isSelected = false
                        emojiView.amount -= 1

                        if (emojiView.amount == 0) {
//                            messageViewGroup.emojisLayout.removeView(emojiView)
//                            messageViewGroup.reactions.removeAt(emojiIndex)
                            removeEmojiView(messageViewGroup, emojiView)
                        } else {
                            messages[messageIndex].reactions[emojiIndex].reactedUsersId.remove(THIS_USER_ID)
                            messages[messageIndex].reactions[emojiIndex].amount -= 1
                        }

                        if (messageViewGroup.emojisLayout.childCount == 1) {
                            messageViewGroup.emojisLayout.removeAllViews()
                        }

                    } else {
                        emojiView.isSelected = true
                        emojiView.amount += 1

                        messages[messageIndex].reactions[emojiIndex].reactedUsersId.add(THIS_USER_ID)
                        messages[messageIndex].reactions[emojiIndex].amount += 1
                    }
                }
            }
        }
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

    private fun getOnLongClickListenerForMessages(messageViewGroup: MessageViewGroup) : View.OnLongClickListener {
        return View.OnLongClickListener {
            showEmojisDialog(messageViewGroup)
            true
        }
    }

    private fun getOnPlusClickListener(messageViewGroup: MessageViewGroup): View.OnClickListener {
        return View.OnClickListener {
            showEmojisDialog(messageViewGroup)
        }
    }

    private fun showEmojisDialog(messageViewGroup: MessageViewGroup) {
        val bottomEmojisDialog = layoutInflater.inflate(R.layout.bottom_emojis_sheet, null)
        dialog = BottomSheetDialog(this)
        dialog.setContentView(bottomEmojisDialog)

        globalMessageViewGroup = messageViewGroup

        bottomEmojisDialog.setOnClickListener{
            dialog.dismiss()
        }

        dialog.show()
    }

    fun onDialogEmojiClick(view: View) {
        addEmojiView(
                messageViewGroup = globalMessageViewGroup,
                view as EmojiView
        )
        dialog.dismiss()
    }

    private fun addEmojiView(messageViewGroup: MessageViewGroup, emojiView: EmojiView) {
        if (messageViewGroup.reactions.map { it.first }.contains(emojiView.emoji)) {
            messageViewGroup.emojisLayout.children.forEach {
                val existingEmojiView = it as EmojiView
                if (existingEmojiView.emoji == emojiView.emoji && !existingEmojiView.isSelected) {
                    existingEmojiView.callOnClick()
                }
            }

        } else {

            val newEmojiView = EmojiView(this)
            newEmojiView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            newEmojiView.amount = 1
            newEmojiView.emoji = emojiView.emoji
            newEmojiView.isSelected = true
            newEmojiView.textColor = ContextCompat.getColor(this, R.color.white)
            newEmojiView.background = ContextCompat.getDrawable(this, R.drawable.emoji_view_bg)
            newEmojiView.setPadding(dpToPx(4F))

            newEmojiView.setOnClickListener(getOnCLickListenerForEmojiView(messageViewGroup))
            messageViewGroup.addEmojiView(newEmojiView)

            val messageIndex = getIndexOfMessage(messageViewGroup)
            messages[messageIndex].reactions.add(Reaction(emojiView.emoji, 1, arrayListOf(THIS_USER_ID)))

            refreshSelectedEmojis(messageViewGroup)
        }
    }

    private fun removeEmojiView(messageViewGroup: MessageViewGroup, emojiView: EmojiView) {
        messageViewGroup.removeEmojiView(emojiView)

        val messageIndex = getIndexOfMessage(messageViewGroup)

        var indexToRemove = -1
        for (i in 0 until messages[messageIndex].reactions.size) {
            if (messages[messageIndex].reactions[i].emoji == emojiView.emoji) {
                indexToRemove = i
            }
        }
        messages[messageIndex].reactions.removeAt(indexToRemove)

        refreshSelectedEmojis(messageViewGroup)
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

    private fun setClickListenerForSendImage() {
        findViewById<ImageView>(R.id.imageSend).setOnClickListener {
            val editText = findViewById<EditText>(R.id.editText)
            val text = editText.text.toString().trim()
            val author = THIS_USER_NAME
            val date = Calendar.getInstance().time
            val authorId = THIS_USER_ID
            val messageId = "${THIS_USER_ID % 1_000_000_000}${date.time % 1_000_000_000}".toLong()
            val avatarUrl = THIS_USER_AVATAR_URL
            val reactions = arrayListOf<Reaction>()

            val newMessage = Message(
                    text,
                    author,
                    date,
                    authorId,
                    messageId,
                    avatarUrl,
                    reactions
            )

            if (text.isNotEmpty()) {
                messages.add(newMessage)

                messageUis.add(messageToUi(listOf(newMessage))[0])
                adapter.items = messageUis

                editText.text.clear()
                adapter.notifyItemInserted(adapter.itemCount - 1)
                recyclerView.scrollToPosition(adapter.itemCount - 1)
            }
        }
    }
    
    private fun getMessagesList(): List<Message> {
        return listOf(
                Message("Hello, world!", "John Smith", Date(10000), 2384758, 1),

                Message("Look\n" +
                        "If you had\n" +
                        "One shot\n" +
                        "Or one opportunity\n" +
                        "To seize everything you ever wanted\n" +
                        "In one moment\n" +
                        "Would you capture it\n" +
                        "Or just let it slip?", "Marshall Bruce Mathers III", Date(20000), 2334412, 2,
                        reactions = arrayListOf(
                                Reaction(Emoji.FACE_IN_LOVE, 3, arrayListOf(1, 2, 3, THIS_USER_ID)),
                                Reaction(Emoji.FACE_WITH_SUNGLASSES, 3, arrayListOf(1, 2, 3)),
                                Reaction(Emoji.FACE_SMILING, 4, arrayListOf(1, 2, 3, 4))
                        )),

                Message("Nice text, bro", "Dr Dre", Date(3984), 13413, 3),

                Message("Wanna apple?", "Steve Jobs", Date(30000), 6534758, 4,
                        avatarUrl = "https://cdn.vox-cdn.com/thumbor/gD8CFUq4EEdI8ux04KyGMmuIgcA=/0x86:706x557/920x613/filters:focal(0x86:706x557):format(webp)/cdn.vox-cdn.com/imported_assets/847184/stevejobs.png"),

                Message("Hello, world!", "John Smith", Date(10000), 2384758, 5),

                Message("Look\n" +
                        "If you had\n" +
                        "One shot\n" +
                        "Or one opportunity\n" +
                        "To seize everything you ever wanted\n" +
                        "In one moment\n" +
                        "Would you capture it\n" +
                        "Or just let it slip?", "Marshall Bruce Mathers III", Date(20000), 2334412, 6),

                Message("Nice text, bro", "Dr Dre", Date(3984), 13413, 7),

                Message("Wanna apple?", "Steve Jobs", Date(30000), 6534758, 8,
                        avatarUrl = "https://cdn.vox-cdn.com/thumbor/gD8CFUq4EEdI8ux04KyGMmuIgcA=/0x86:706x557/920x613/filters:focal(0x86:706x557):format(webp)/cdn.vox-cdn.com/imported_assets/847184/stevejobs.png"),
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(MESSAGES_LIST_KEY, messages)
    }

    companion object {
        const val THIS_USER_ID = 123456789101112L
        const val THIS_USER_NAME = "Alexey Anastasyev"
        const val THIS_USER_AVATAR_URL = "https://sun9-62.userapi.com/impf/c841630/v841630065/113e0/lpOMX1Dm8Ao.jpg?size=225x225&quality=96&sign=5c18b2e9ed3f0f0dd9795f4e37012341&type=album"

        const val MESSAGES_LIST_KEY = "messages"
    }

}