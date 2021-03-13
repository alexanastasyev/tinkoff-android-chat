package com.example.chat

import android.view.View
import com.squareup.picasso.Picasso

class MessageUi(
        var authorId: Long,
        var text: String,
        var author: String,
        var avatarUrl: String?,
        var reactions: List<Reaction>,
        var messageId: Long,
        override val viewType: Int = R.layout.item_message
) : ViewTyped

class MessageViewHolder(view: View) : BaseViewHolder<MessageUi>(view) {
    val messageHolder = view.findViewById<MessageViewGroup>(R.id.message_from_other)

    override fun bind(item: MessageUi) {
        val context = messageHolder.context
        if (item.authorId == MainActivity.THIS_USER_ID) {
            messageHolder.align = 1
            messageHolder.messageText = item.text
            messageHolder.userName = item.author
            messageHolder.reactions = item.reactions.map { Pair(it.emoji, it.amount) }
            for (i in item.reactions.indices) {
                if (item.reactions[i].reactedUsersId.contains(MainActivity.THIS_USER_ID))
                messageHolder.emojisLayout.getChildAt(i).isSelected = true
            }

            messageHolder.avatarImageView.setImageDrawable(null)

        } else {
            messageHolder.align = 0
            messageHolder.messageText = item.text
            messageHolder.userName = item.author
            messageHolder.reactions = item.reactions.map { Pair(it.emoji, it.amount) }
            for (i in item.reactions.indices) {
                if (item.reactions[i].reactedUsersId.contains(MainActivity.THIS_USER_ID))
                    messageHolder.emojisLayout.getChildAt(i).isSelected = true
            }

            Picasso
                    .with(context)
                    .load(item.avatarUrl)
                    .placeholder(R.drawable.default_avatar)
                    .into(messageHolder.avatarImageView)

        }
    }
}