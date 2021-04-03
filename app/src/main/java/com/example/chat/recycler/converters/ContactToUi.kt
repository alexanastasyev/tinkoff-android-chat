package com.example.chat.recycler.converters

import com.example.chat.entities.Contact
import com.example.chat.recycler.ViewTyped
import com.example.chat.recycler.uis.ContactUi

fun contactToUi (contacts: List<Contact>) : List<ViewTyped> {
    return contacts.map { ContactUi(
        it.imageUrl,
        it.name
    ) }
}