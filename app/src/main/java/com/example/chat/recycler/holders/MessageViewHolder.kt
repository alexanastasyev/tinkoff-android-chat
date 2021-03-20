package com.example.chat.recycler.holders

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.chat.*
import com.example.chat.activities.ChatActivity
import com.example.chat.recycler.ViewTyped
import com.example.chat.views.MessageViewGroup
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class MessageUi(
        var messageId: Long,
        var text: String,
        var author: String,
        var authorId: Long,
        var avatarUrl: String?,
        var reactions: ArrayList<Reaction>,
        var date: Date,
        override val viewType: Int = R.layout.item_message
) : ViewTyped

class MessageViewHolder(
        view: View,
        click: ((View) -> Unit)?,
        action: ((View) -> Unit)?,
        private val setBackground: ((View) -> Unit),
        private val shouldShowDate: ((View) -> Boolean)
) : BaseViewHolder<MessageUi>(view) {

    companion object {
        private const val MINIMAL_MESSAGE_WIDTH = 100
    }

    private val messageHolder = view.findViewById<MessageViewGroup>(R.id.message)
    private val dateHolder = view.findViewById<TextView>(R.id.date)

    init {
        if (click != null) {
            messageHolder.setOnClickListener(click)
        }
        if (action != null) {
            action(messageHolder)
        }
    }

    override fun bind(item: MessageUi) {
        val context = messageHolder.context

        messageHolder.messageId = item.messageId
        messageHolder.messageText = item.text
        messageHolder.userName = item.author
        bindReactions(item)
        bindAndShowDateIfNecessary(item)

        if (item.authorId == ChatActivity.THIS_USER_ID) {
            messageHolder.align = MessageViewGroup.ALIGN_RIGHT
            messageHolder.avatarImageView.setImageDrawable(null)
            messageHolder.findViewById<TextView>(R.id.name).setTextColor(ContextCompat.getColor(context, R.color.my_name_color))
        } else {
            messageHolder.align = MessageViewGroup.ALIGN_LEFT
            Picasso
                    .with(context)
                    .load(item.avatarUrl)
                    .placeholder(R.drawable.default_avatar)
                    .into(messageHolder.avatarImageView)

            messageHolder.findViewById<TextView>(R.id.name).setTextColor(ContextCompat.getColor(context, R.color.name_color))
        }
        setBackground(messageHolder)
    }

    private fun bindAndShowDateIfNecessary(item: MessageUi) {
        val formatter = SimpleDateFormat("d MMM", Locale.getDefault())
        val dateAsString = formatter.format(item.date)
        dateHolder.text = dateAsString

        if (!shouldShowDate(messageHolder)) {
            dateHolder.height = 0
        }
    }

    private fun bindReactions(item: MessageUi) {
        messageHolder.reactions = item.reactions.map { Pair(it.emoji, it.amount) } as ArrayList<Pair<Emoji, Int>>
        for (i in item.reactions.indices) {
            if (item.reactions[i].reactedUsersId.contains(ChatActivity.THIS_USER_ID))
                messageHolder.emojisLayout.getChildAt(i).isSelected = true
        }
    }
}