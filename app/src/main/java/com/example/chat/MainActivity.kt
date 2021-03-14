package com.example.chat

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var messages: ArrayList<Message>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        messages = if (savedInstanceState == null) {
            ArrayList(getMessagesList())
        } else {
            savedInstanceState.getSerializable("messages") as ArrayList<Message>
        }

        val messageToUi = MessageToUi()
        val messageUis = messageToUi(messages) as ArrayList<ViewTyped>

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val holderFactory = TfsHolderFactory(action = { message ->
            val messageViewGroup = message as MessageViewGroup
            messageViewGroup.setOnCLickListenerForEmojiViews { emoji ->
                val emojiView = emoji as EmojiView
                if (emojiView.isSelected) {
                    emojiView.isSelected = false
                    emojiView.amount -= 1

//                    val messageUisId = messageUis.map { (it as MessageUi).messageId }
//                    val messagesId = messages.map { (it as Message).messageId }
                    var messageIndex = -1
                    for (i in messages.indices) {
                        if (messages[i].messageId == messageViewGroup.messageId) {
                            messageIndex = i
                        }
                    }
                    val emojiIndex = messageViewGroup.emojisLayout.indexOfChild(emojiView)
                    messages[messageIndex].reactions[emojiIndex].reactedUsersId.remove(THIS_USER_ID)
                    messages[messageIndex].reactions[emojiIndex].amount -= 1
                } else {
                    emojiView.isSelected = true
                    emojiView.amount += 1

                    var messageIndex = -1
                    for (i in messages.indices) {
                        if (messages[i].messageId == messageViewGroup.messageId) {
                            messageIndex = i
                        }
                    }
                    val emojiIndex = messageViewGroup.emojisLayout.indexOfChild(emojiView)
                    messages[messageIndex].reactions[emojiIndex].reactedUsersId.add(THIS_USER_ID)
                    messages[messageIndex].reactions[emojiIndex].amount += 1
                }
            }
        })
        val adapter = Adapter<ViewTyped>(holderFactory)
        recyclerView.adapter = adapter

        /*
        We need to save List<Message> in onSaveInstanceState.
        Then we check savedInstanceState here and load messages either
        from getMessagesList() or from savedInstanceState.
         */



        adapter.items = messageUis

        findViewById<ImageView>(R.id.imageSend).setOnClickListener {
            val editText = findViewById<EditText>(R.id.editText)
            val text = editText.text.toString().trim()
            val author = CURRENT_USER_NAME
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
                recyclerView.scrollToPosition(adapter.itemCount - 1)
                adapter.notifyItemInserted(adapter.itemCount - 1)
            }
//            this.currentFocus?.let { view ->
//                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
//                imm?.hideSoftInputFromWindow(view.windowToken, 0)
//            }
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

                Message("Good morning", "Anna Anastasyeva", Date(1), 269, 9,
                        avatarUrl = "https://sun9-17.userapi.com/impf/c845221/v845221644/202103/rv4qM1HcbIk.jpg?size=800x600&quality=96&sign=32c7b6f5e844201615170c2677f42805&type=album")
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("messages", messages)
    }

//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        super.onRestoreInstanceState(savedInstanceState)
//        val amount = savedInstanceState.getInt("amount")
//        for (i in 0 until amount) {
//            val messageViewGroup = findViewById<MessageViewGroup>(i)
//            messageViewGroup.restoreState(savedInstanceState.getBundle("message$i"))
//        }
//    }

    companion object {
        const val THIS_USER_ID = 100L
        const val CURRENT_USER_NAME = "Alexey Anastasyev"
        const val  THIS_USER_AVATAR_URL = "https://sun9-62.userapi.com/impf/c841630/v841630065/113e0/lpOMX1Dm8Ao.jpg?size=225x225&quality=96&sign=5c18b2e9ed3f0f0dd9795f4e37012341&type=album"
    }
}