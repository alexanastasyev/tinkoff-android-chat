package com.example.chat.recycler.holders

import android.view.View
import android.widget.TextView
import com.example.chat.R
import com.example.chat.recycler.uis.ContactUi
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ContactViewHolder(
    view: View,
    onClickListener: ((View) -> Unit)?,
) : BaseViewHolder<ContactUi>(view) {

    private val contactNameHolder = view.findViewById<TextView>(R.id.contactName)
    private val contactPictureHolder = view.findViewById<CircleImageView>(R.id.imageContact)
    private val onlineIndicator = view.findViewById<CircleImageView>(R.id.onlineIndicator)

    init {
        if (onClickListener != null) {
            view.setOnClickListener(onClickListener)
        }
    }

    override fun bind(item: ContactUi) {
        contactNameHolder.text = item.name
        Picasso
            .with(contactPictureHolder.context)
            .load(item.imageUrl)
            .placeholder(R.drawable.default_avatar)
            .into(contactPictureHolder)
        if (item.isOnline) {
            onlineIndicator.visibility = View.VISIBLE
        } else {
            onlineIndicator.visibility = View.GONE
        }
    }
}