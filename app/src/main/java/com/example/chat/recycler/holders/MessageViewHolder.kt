package com.example.chat.recycler.holders

import android.view.View
import android.widget.TextView
import com.example.chat.*
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
        private val showDate: ((View) -> Boolean)
) : BaseViewHolder<MessageUi>(view) {

//    companion object {
//        private var lastDayUpdated: String = ""
//    }

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


        val formatter = SimpleDateFormat("dd MMM", Locale.getDefault())
        val dateAsString = formatter.format(item.date)
        dateHolder.text = dateAsString

        val shouldShowDate = showDate(messageHolder)

        if (shouldShowDate) {
            dateHolder.height = 50
        } else {
            dateHolder.height = 0
        }

//        if (dateAsString != lastDayUpdated) {
//            dateHolder.height = 50
//            dateHolder.text = dateAsString
//            lastDayUpdated = dateAsString
//        } else {
//            dateHolder.height = 0
//        }

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
        messageHolder.reactions = item.reactions.map { Pair(it.emoji, it.amount) } as ArrayList<Pair<Emoji, Int>>
        for (i in item.reactions.indices) {
            if (item.reactions[i].reactedUsersId.contains(MainActivity.THIS_USER_ID))
                messageHolder.emojisLayout.getChildAt(i).isSelected = true
        }
    }
}