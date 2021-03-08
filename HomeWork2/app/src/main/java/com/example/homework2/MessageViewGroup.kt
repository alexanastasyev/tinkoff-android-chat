package com.example.homework2

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.children
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import de.hdodenhof.circleimageview.CircleImageView

class MessageViewGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    private val avatarImageView: ImageView
    private val messageText: TextView
    private val name: TextView
    private val flexBoxLayout: FlexBoxLayout
    private val avatarImageViewRect = Rect()
    private val messageTextRect = Rect()
    private val nameRect = Rect()
    private val flexBoxLayoutRect = Rect()

    companion object {
        private const val AVATAR_SIZE = 90F

        private const val AVATAR_IMAGE_KEY = "avatar"
        private const val NAME_KEY = "name"
        private const val MESSAGE_TEXT_KEY = "message"
        private const val EMOJIES_ARRAY_KEY = "emojies"
        private const val AMOUNTS_ARRAY_KEY = "amounts"
        private const val EMOJIES_STATE_SELECTED_KEY = "selected"
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.custom_view_group, this, true)
        avatarImageView = findViewById(R.id.avatarView)
        messageText = findViewById(R.id.messageText)
        name = findViewById(R.id.name)
        flexBoxLayout = findViewById(R.id.flexBoxLayout)
        layoutParams = LinearLayout.LayoutParams(dpToPx(AVATAR_SIZE, resources), dpToPx(AVATAR_SIZE, resources))
        avatarImageView.layoutParams = layoutParams
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val avatarImageViewLayoutParams = avatarImageView.layoutParams as MarginLayoutParams
        val messageTextLayoutParams = messageText.layoutParams as MarginLayoutParams
        val nameLayoutParams = name.layoutParams as MarginLayoutParams
        val flexBoxLayoutParams = flexBoxLayout.layoutParams as MarginLayoutParams

        measureChildWithMargins(avatarImageView, widthMeasureSpec, 0, heightMeasureSpec, 0)
        val avatarImageViewHeight = avatarImageView.measuredHeight + avatarImageViewLayoutParams.topMargin + avatarImageViewLayoutParams.bottomMargin
        val avatarImageViewWidth = avatarImageView.measuredWidth + avatarImageViewLayoutParams.leftMargin + avatarImageViewLayoutParams.rightMargin

        measureChildWithMargins(name, widthMeasureSpec, avatarImageViewWidth, heightMeasureSpec, 0)
        val nameHeight = name.measuredHeight + nameLayoutParams.topMargin + nameLayoutParams.bottomMargin
        val nameWidth = name.measuredWidth + nameLayoutParams.leftMargin + nameLayoutParams.rightMargin

        measureChildWithMargins(messageText, widthMeasureSpec, avatarImageViewWidth, heightMeasureSpec, nameHeight)
        val messageTextHeight = messageText.measuredHeight + messageTextLayoutParams.topMargin + messageTextLayoutParams.bottomMargin
        val messageTextWidth = messageText.measuredWidth + messageTextLayoutParams.leftMargin + messageTextLayoutParams.rightMargin

        measureChildWithMargins(flexBoxLayout, widthMeasureSpec, 0, heightMeasureSpec, maxOf(avatarImageViewHeight, (nameHeight + messageTextHeight)))
        val flexBoxLayoutHeight = flexBoxLayout.measuredHeight + flexBoxLayoutParams.topMargin + flexBoxLayoutParams.bottomMargin
        val flexBoxLayoutWidth = flexBoxLayout.measuredWidth + flexBoxLayoutParams.leftMargin + flexBoxLayoutParams.rightMargin

        setMeasuredDimension(
            resolveSize(maxOf((avatarImageViewWidth + messageTextWidth + nameWidth), flexBoxLayoutWidth) + paddingLeft + paddingRight,  widthMeasureSpec),
            resolveSize(maxOf(avatarImageViewHeight, messageTextHeight + nameHeight) + flexBoxLayoutHeight + paddingTop + 2 * paddingBottom, heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val imageViewLayoutParams = avatarImageView.layoutParams as MarginLayoutParams
        val textLayoutParams = messageText.layoutParams as MarginLayoutParams
        val nameLayoutParams = name.layoutParams as MarginLayoutParams
        val flexBoxLayoutParams = flexBoxLayout.layoutParams as MarginLayoutParams

        avatarImageViewRect.left = imageViewLayoutParams.leftMargin + paddingLeft
        avatarImageViewRect.top = imageViewLayoutParams.topMargin + paddingTop
        avatarImageViewRect.right = avatarImageViewRect.left + avatarImageView.measuredWidth
        avatarImageViewRect.bottom = avatarImageViewRect.top + avatarImageView.measuredHeight
        avatarImageView.layout(avatarImageViewRect)

        nameRect.left = name.marginLeft + avatarImageViewRect.right + avatarImageView.marginRight + paddingLeft
        nameRect.top = nameLayoutParams.topMargin + paddingTop
        nameRect.right = nameRect.left + name.measuredWidth
        nameRect.bottom = nameRect.top + name.measuredHeight
        name.layout(nameRect)

        messageTextRect.left = messageText.marginLeft + avatarImageViewRect.right + avatarImageView.marginRight + paddingLeft
        messageTextRect.top = textLayoutParams.topMargin + name.measuredHeight + paddingTop
        messageTextRect.right = messageTextRect.left + messageText.measuredWidth
        messageTextRect.bottom = messageTextRect.top + messageText.measuredHeight
        messageText.layout(messageTextRect)

        flexBoxLayoutRect.left = flexBoxLayoutParams.leftMargin + paddingLeft
        flexBoxLayoutRect.top = maxOf(avatarImageViewRect.bottom, messageTextRect.bottom) + flexBoxLayoutParams.topMargin + paddingTop
        flexBoxLayoutRect.right = flexBoxLayoutRect.left + flexBoxLayout.measuredWidth
        flexBoxLayoutRect.bottom = flexBoxLayoutRect.top + flexBoxLayout.measuredHeight
        flexBoxLayout.layout(flexBoxLayoutRect)

    }

    override fun generateDefaultLayoutParams(): LayoutParams =
        MarginLayoutParams(MATCH_PARENT, WRAP_CONTENT)

    override fun generateLayoutParams(attrs: AttributeSet?) = MarginLayoutParams(context, attrs)

    override fun generateLayoutParams(p: LayoutParams?): LayoutParams = MarginLayoutParams(p)

    private fun View.layout(rect: Rect) {
        layout(rect.left, rect.top, rect.right, rect.bottom)
    }

    fun saveState(): Bundle {
        val outState = Bundle()

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

        return outState
    }

    fun restoreState(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            return
        }

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