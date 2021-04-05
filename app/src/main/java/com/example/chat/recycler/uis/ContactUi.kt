package com.example.chat.recycler.uis

import com.example.chat.R
import com.example.chat.entities.Status
import com.example.chat.recycler.ViewTyped

class ContactUi(
        var imageUrl: String?,
        var name: String,
        var status: Status = Status.OFFLINE,
        override val viewType: Int = R.layout.item_contact,
        override val uid: String = "contact$name"
) : ViewTyped