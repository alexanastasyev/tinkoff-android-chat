package com.example.homework2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val messageViewGroup = findViewById<MessageViewGroup>(R.id.messageViewGroup)
        messageViewGroup.setOnCLickListenerForEmojiViews()

        messageViewGroup.messageText = "Avadacadabra"
        messageViewGroup.userName = "James Bond"
        messageViewGroup.emojis = listOf(Pair(Emoji.FACE_LAUGHING, 2), Pair(Emoji.FACE_SMILING, 1), Pair(Emoji.FACE_COWBOY_HAT, 99))
        messageViewGroup.avatar = ResourcesCompat.getDrawable(resources, R.drawable.default_avatar, null)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val messageViewGroup = findViewById<MessageViewGroup>(R.id.messageViewGroup)
        outState.putBundle(MESSAGE_VIEW_GROUP_STATE_KEY, messageViewGroup.saveState())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val messageViewGroup = findViewById<MessageViewGroup>(R.id.messageViewGroup)
        messageViewGroup.restoreState(savedInstanceState.getBundle(MESSAGE_VIEW_GROUP_STATE_KEY))
    }

    companion object {
        private const val MESSAGE_VIEW_GROUP_STATE_KEY = "messageViewGroup"
    }
}