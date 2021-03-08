package com.example.homework2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.children

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<FlexBoxLayout>(R.id.flexBoxLayout).children.forEach { child ->
            child.setOnClickListener {
                child.isSelected = !child.isSelected
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBundle(MESSAGE_VIEW_GROUP_KEY, findViewById<MessageViewGroup>(R.id.messageViewGroup).saveState())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        findViewById<MessageViewGroup>(R.id.messageViewGroup).restoreState(savedInstanceState.getBundle(MESSAGE_VIEW_GROUP_KEY))
    }

    companion object {
        private const val MESSAGE_VIEW_GROUP_KEY = "messageViewGroup"
    }
}