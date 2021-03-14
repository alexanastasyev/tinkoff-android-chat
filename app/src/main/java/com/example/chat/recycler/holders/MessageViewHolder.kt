package com.example.chat.recycler.holders

import android.view.View
import com.example.chat.MainActivity
import com.example.chat.R
import com.example.chat.Reaction
import com.example.chat.recycler.ViewTyped
import com.example.chat.views.MessageViewGroup
import com.squareup.picasso.Picasso
import java.util.*

class MessageUi(
        var messageId: Long,
        var text: String,
        var author: String,
        var authorId: Long,
        var avatarUrl: String?,
        var reactions: List<Reaction>,
        var date: Date,
        override val viewType: Int = R.layout.item_message
) : ViewTyped

class MessageViewHolder(
        view: View,
        click: ((View) -> Unit)?,
        action: ((View) -> Unit)?
    ) : BaseViewHolder<MessageUi>(view) {

    private val messageHolder = view.findViewById<MessageViewGroup>(R.id.message_from_other)

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

        if (item.authorId == MainActivity.THIS_USER_ID) {
            messageHolder.align = MessageViewGroup.ALIGN_RIGHT
            messageHolder.avatarImageView.setImageDrawable(null)

        } else {
            messageHolder.align = MessageViewGroup.ALIGN_LEFT
            Picasso
                    .with(context)
                    .load(item.avatarUrl)
                    .placeholder(R.drawable.default_avatar)
                    .into(messageHolder.avatarImageView)
        }
    }

    private fun bindReactions(item: MessageUi) {
        messageHolder.reactions = item.reactions.map { Pair(it.emoji, it.amount) }
        for (i in item.reactions.indices) {
            if (item.reactions[i].reactedUsersId.contains(MainActivity.THIS_USER_ID))
                messageHolder.emojisLayout.getChildAt(i).isSelected = true
        }
    }
}