package com.example.chat.recycler.uis

import com.example.chat.R
import com.example.chat.recycler.ViewTyped

class ContactUi(
        var imageUrl: String?,
        var name: String,
        override val viewType: Int = R.layout.item_contact
) : ViewTyped