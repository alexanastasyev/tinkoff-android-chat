package com.example.chat.screens.profile

import com.example.chat.entities.Contact

interface ProfileView {
    fun showData(profile: Contact)
    fun showError()
}