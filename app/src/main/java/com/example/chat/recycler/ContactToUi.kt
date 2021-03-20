package com.example.chat.recycler

import com.example.chat.Contact
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