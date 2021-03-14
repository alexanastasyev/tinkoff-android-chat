package com.example.chat

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chat.recycler.Adapter
import com.example.chat.recycler.ChatHolderFactory
import com.example.chat.recycler.ViewTyped
import com.example.chat.recycler.messageToUi
import com.example.chat.views.EmojiView
import com.example.chat.views.MessageViewGroup
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var messages: ArrayList<Message>
    private lateinit var messageUis: ArrayList<ViewTyped>
    private lateinit var adapter: Adapter<ViewTyped>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        messages = if (savedInstanceState == null) {
            ArrayList(getMessagesList())
        } else {
            savedInstanceState.getSerializable(MESSAGES_LIST_KEY) as ArrayList<Message>
        }
        messageUis = messageToUi(messages) as ArrayList<ViewTyped>

        val holderFactory = ChatHolderFactory(action = getActionForMessageViewGroups())
        adapter = Adapter(holderFactory)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.adapter = adapter
        adapter.items = messageUis

        setClickListenerForSendImage()
    }

    private fun getActionForMessageViewGroups() : (View) -> Unit {
        return { message ->
            val messageViewGroup = message as MessageViewGroup
            messageViewGroup.setOnCLickListenerForEmojiViews(getOnCLickListenerForEmojiView(messageViewGroup))
        }
    }

    private fun getOnCLickListenerForEmojiView(messageViewGroup: MessageViewGroup): (View) -> Unit {
        return { emoji ->
            val emojiView = emoji as EmojiView

            var messageIndex = -1
            for (i in messages.indices) {
                if (messages[i].messageId == messageViewGroup.messageId) {
                    messageIndex = i
                }
            }
            val emojiIndex = messageViewGroup.emojisLayout.indexOfChild(emojiView)

            if (messageIndex in messages.indices) {
                if (emojiIndex in messages[messageIndex].reactions.indices) {

                    if (emojiView.isSelected) {
                        emojiView.isSelected = false
                        emojiView.amount -= 1

                        messages[messageIndex].reactions[emojiIndex].reactedUsersId.remove(THIS_USER_ID)
                        messages[messageIndex].reactions[emojiIndex].amount -= 1

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

    private fun setClickListenerForSendImage() {
        findViewById<ImageView>(R.id.imageSend).setOnClickListener {
            val editText = findViewById<EditText>(R.id.editText)
            val text = editText.text.toString().trim()
            val author = THIS_USER_NAME
            val date = Calendar.getInstance().time
            val authorId = THIS_USER_ID
            val messageId = THIS_USER_ID + date.time
            val avatarUrl = THIS_USER_AVATAR_URL
            val reactions = emptyList<Reaction>()

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
                        reactions = listOf(
                                Reaction(Emoji.FACE_IN_LOVE, 100, arrayListOf(1, 2, 3, THIS_USER_ID)),
                                Reaction(Emoji.FACE_WITH_SUNGLASSES, 12, arrayListOf(1, 2, 3))
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
        const val THIS_USER_ID = 100L
        const val THIS_USER_NAME = "Alexey Anastasyev"
        const val THIS_USER_AVATAR_URL = "https://sun9-62.userapi.com/impf/c841630/v841630065/113e0/lpOMX1Dm8Ao.jpg?size=225x225&quality=96&sign=5c18b2e9ed3f0f0dd9795f4e37012341&type=album"

        const val MESSAGES_LIST_KEY = "messages"
    }
}