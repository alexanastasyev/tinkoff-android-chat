package com.example.chat.recycler.converters

import com.example.chat.entities.Contact
import com.example.chat.recycler.ViewTyped
import com.example.chat.recycler.holders.ContactUi

fun contactToUi (contacts: List<Contact>) : List<ViewTyped> {
    val contactUis: ArrayList<ContactUi> = ArrayList()
    for (contact in contacts) {
        contactUis.add(ContactUi(
                contact.imageUrl,
                contact.name
        ))
    }
    return contactUis
}