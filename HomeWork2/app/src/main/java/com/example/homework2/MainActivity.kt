package com.example.homework2

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.children
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : AppCompatActivity() {

    companion object {
        private const val AVATAR_IMAGE_KEY = "avatar"
        private const val NAME_KEY = "name"
        private const val MESSAGE_TEXT_KEY = "message"
        private const val EMOJIES_ARRAY_KEY = "emojies"
        private const val AMOUNTS_ARRAY_KEY = "amounts"
        private const val EMOJIES_STATE_SELECTED_KEY = "selected"
    }

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
        val circleImageView: CircleImageView = findViewById(R.id.avatarView)
        val imageDrawable = circleImageView.drawable
        val imageBitmap = imageDrawable.toBitmap()
        outState.putParcelable(AVATAR_IMAGE_KEY, imageBitmap)

        val nameView: TextView = findViewById(R.id.name)
        outState.putString(NAME_KEY, nameView.text.toString())

        val messageTextView: TextView = findViewById(R.id.messageText)
        outState.putString(MESSAGE_TEXT_KEY, messageTextView.text.toString())

        val flexBoxLayout: FlexBoxLayout = findViewById(R.id.flexBoxLayout)
        val emojiesAmount = flexBoxLayout.childCount
        val emojies = arrayOfNulls<String>(emojiesAmount)
        var i = 0
        flexBoxLayout.children.forEach {
            val emojiView: EmojiView = it as EmojiView
            val emoji = emojiView.emoji
            emojies[i] = emoji
            i++
        }
        outState.putStringArray(EMOJIES_ARRAY_KEY, emojies)

        val amounts = IntArray(emojiesAmount)
        i = 0
        flexBoxLayout.children.forEach {
            val emojiView: EmojiView = it as EmojiView
            val amount = emojiView.amount
            amounts[i] = amount
            i++
        }
        outState.putIntArray(AMOUNTS_ARRAY_KEY, amounts)


        val emojiesAreSelected = BooleanArray(emojiesAmount)
        i = 0
        flexBoxLayout.children.forEach {
            val emojiView: EmojiView = it as EmojiView
            emojiesAreSelected[i] = emojiView.isSelected
            i++
        }
        outState.putBooleanArray(EMOJIES_STATE_SELECTED_KEY, emojiesAreSelected)

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        val circleImageView: CircleImageView = findViewById(R.id.avatarView)
        val imageBitmap = savedInstanceState.getParcelable<Bitmap>(AVATAR_IMAGE_KEY)
        circleImageView.setImageBitmap(imageBitmap)

        val nameView: TextView = findViewById(R.id.name)
        nameView.text = savedInstanceState.getString(NAME_KEY)

        val messageTextView: TextView = findViewById(R.id.messageText)
        messageTextView.text = savedInstanceState.getString(MESSAGE_TEXT_KEY)

        val flexBoxLayout: FlexBoxLayout = findViewById(R.id.flexBoxLayout)
        val emojies = savedInstanceState.getStringArray(EMOJIES_ARRAY_KEY) ?: return
        for (i in emojies.indices) {
            val emojiView: EmojiView = flexBoxLayout.getChildAt(i) as EmojiView
            emojiView.emoji = emojies[i]
        }

        val amounts = savedInstanceState.getIntArray(AMOUNTS_ARRAY_KEY) ?: return
        for (i in amounts.indices) {
            val emojiView: EmojiView = flexBoxLayout.getChildAt(i) as EmojiView
            emojiView.amount = amounts[i]
        }

        val emojiesAreSelected = savedInstanceState.getBooleanArray(EMOJIES_STATE_SELECTED_KEY) ?: return
        for (i in emojiesAreSelected.indices) {
            val emojiView: EmojiView = flexBoxLayout.getChildAt(i) as EmojiView
            emojiView.isSelected = emojiesAreSelected[i]
        }
    }

}