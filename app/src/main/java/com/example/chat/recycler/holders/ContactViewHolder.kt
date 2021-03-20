package com.example.chat.recycler.holders

import android.view.View
import android.widget.TextView
import com.example.chat.R
import com.example.chat.recycler.ViewTyped
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ContactUi(
        var imageUrl: String?,
        var name: String,
        override val viewType: Int = R.layout.item_contact
) : ViewTyped

class ContactViewHolder(
        view: View,
        click: ((View) -> Unit)?,
) : BaseViewHolder<ContactUi>(view) {

    private val contactNameHolder = view.findViewById<TextView>(R.id.contactName)
    private val contactPictureHolder = view.findViewById<CircleImageView>(R.id.imageContact)

    init {
        if (click != null) {
            contactNameHolder.setOnClickListener(click)
            contactPictureHolder.setOnClickListener(click)
        }
    }

    override fun bind(item: ContactUi) {
        contactNameHolder.text = item.name
        Picasso
                .with(contactPictureHolder.context)
                .load(item.imageUrl)
                .placeholder(R.drawable.default_avatar)
                .into(contactPictureHolder)
    }

}