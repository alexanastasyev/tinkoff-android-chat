package com.example.chat.screens.people

import com.example.chat.entities.Contact

interface PeopleView {
    fun showData(contacts: List<Contact>)
    fun showError()
}