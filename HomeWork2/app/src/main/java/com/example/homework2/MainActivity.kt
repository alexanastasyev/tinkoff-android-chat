package com.example.homework2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val messageViewGroup = findViewById<MessageViewGroup>(R.id.messageViewGroup)
        messageViewGroup.setOnCLickListenerForEmojiViews()
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