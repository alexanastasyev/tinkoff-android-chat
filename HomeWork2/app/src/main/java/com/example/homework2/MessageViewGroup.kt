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
import androidx.constraintlayout.widget.ConstraintLayout
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
    private val constraintLayout: ConstraintLayout
    private val flexBoxLayout: FlexBoxLayout

    private val avatarImageViewRect = Rect()
    private val constraintLayoutRect = Rect()
    private val flexBoxLayoutRect = Rect()

    companion object {
        private const val AVATAR_SIZE = 50F

        private const val AVATAR_IMAGE_KEY = "avatar"
        private const val NAME_KEY = "name"
        private const val MESSAGE_TEXT_KEY = "message"
        private const val EMOJIS_ARRAY_KEY = "emojis"
        private const val AMOUNTS_ARRAY_KEY = "amounts"
        private const val emojis_STATE_SELECTED_KEY = "selected"
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.custom_view_group, this, true)

        avatarImageView = findViewById(R.id.avatarView)
        flexBoxLayout = findViewById(R.id.flexBoxLayout)
        constraintLayout = findViewById(R.id.constraintLayout)

        setAvatarSize()
    }

    private fun setAvatarSize() {
        layoutParams = LinearLayout.LayoutParams(dpToPx(AVATAR_SIZE, resources), dpToPx(AVATAR_SIZE, resources))
        avatarImageView.layoutParams = layoutParams
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val avatarImageViewLayoutParams = avatarImageView.layoutParams as MarginLayoutParams
        measureChildWithMargins(avatarImageView, widthMeasureSpec, 0, heightMeasureSpec, 0)
        val avatarImageViewHeight = avatarImageView.measuredHeight + avatarImageViewLayoutParams.topMargin + avatarImageViewLayoutParams.bottomMargin
        val avatarImageViewWidth = avatarImageView.measuredWidth + avatarImageViewLayoutParams.leftMargin + avatarImageViewLayoutParams.rightMargin

        val constraintLayoutParams = constraintLayout.layoutParams as MarginLayoutParams
        measureChildWithMargins(constraintLayout, widthMeasureSpec, avatarImageViewWidth, heightMeasureSpec, 0)
        val constraintLayoutHeight = constraintLayout.measuredHeight + constraintLayoutParams.topMargin + constraintLayoutParams.bottomMargin
        val constraintLayoutWidth = constraintLayout.measuredWidth + constraintLayoutParams.leftMargin + constraintLayoutParams.rightMargin

        val flexBoxLayoutParams = flexBoxLayout.layoutParams as MarginLayoutParams
        measureChildWithMargins(flexBoxLayout, widthMeasureSpec, 0, heightMeasureSpec, maxOf(avatarImageViewHeight, constraintLayoutHeight))
        val flexBoxLayoutHeight = flexBoxLayout.measuredHeight + flexBoxLayoutParams.topMargin + flexBoxLayoutParams.bottomMargin
        val flexBoxLayoutWidth = flexBoxLayout.measuredWidth + flexBoxLayoutParams.leftMargin + flexBoxLayoutParams.rightMargin

        val contentWidth = maxOf((avatarImageViewWidth + constraintLayoutWidth), flexBoxLayoutWidth) + paddingLeft + paddingRight
        val contentHeight = maxOf(avatarImageViewHeight, constraintLayoutHeight) + flexBoxLayoutHeight + paddingTop + paddingBottom

        setMeasuredDimension(
            resolveSize(contentWidth,  widthMeasureSpec),
            resolveSize(contentHeight, heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val avatarLayoutParams = avatarImageView.layoutParams as MarginLayoutParams
        val constraintLayoutParams = constraintLayout.layoutParams as MarginLayoutParams
        val flexBoxLayoutParams = flexBoxLayout.layoutParams as MarginLayoutParams

        avatarImageViewRect.left = avatarLayoutParams.leftMargin + paddingLeft
        avatarImageViewRect.top = avatarLayoutParams.topMargin + paddingTop
        avatarImageViewRect.right = avatarImageViewRect.left + avatarImageView.measuredWidth
        avatarImageViewRect.bottom = avatarImageViewRect.top + avatarImageView.measuredHeight
        avatarImageView.layout(avatarImageViewRect)

        constraintLayoutRect.left = constraintLayout.marginLeft + avatarImageViewRect.right + avatarImageView.marginRight
        constraintLayoutRect.top = constraintLayoutParams.topMargin + paddingTop
        constraintLayoutRect.right = constraintLayoutRect.left + constraintLayout.measuredWidth
        constraintLayoutRect.bottom = constraintLayoutRect.top + constraintLayout.measuredHeight
        constraintLayout.layout(constraintLayoutRect)

        flexBoxLayoutRect.left = flexBoxLayoutParams.leftMargin + paddingLeft
        flexBoxLayoutRect.top = maxOf(avatarImageViewRect.bottom, constraintLayoutRect.bottom) + flexBoxLayoutParams.topMargin
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

        val avatarImageView: CircleImageView = findViewById(R.id.avatarView)
        outState.putParcelable(AVATAR_IMAGE_KEY, saveImage(avatarImageView))

        val nameView: TextView = findViewById(R.id.name)
        outState.putString(NAME_KEY, saveText(nameView))

        val messageTextView: TextView = findViewById(R.id.messageText)
        outState.putString(MESSAGE_TEXT_KEY, saveText(messageTextView))

        val flexBoxLayout: FlexBoxLayout = findViewById(R.id.flexBoxLayout)
        outState.putSerializable(EMOJIS_ARRAY_KEY, saveEmojis(flexBoxLayout))
        outState.putIntArray(AMOUNTS_ARRAY_KEY, saveEmojisAmounts(flexBoxLayout))
        outState.putBooleanArray(emojis_STATE_SELECTED_KEY, saveEmojisStates(flexBoxLayout))

        return outState
    }

    private fun saveImage(imageView: ImageView): Bitmap {
        val imageDrawable = imageView.drawable
        return imageDrawable.toBitmap()
    }

    private fun saveText(textView: TextView): String {
        return textView.text.toString()
    }

    private fun saveEmojis(flexBoxLayout: FlexBoxLayout): Array<Emoji?> {
        val emojisAmount = flexBoxLayout.childCount
        val emojis = arrayOfNulls<Emoji>(emojisAmount)
        var i = 0
        flexBoxLayout.children.forEach {
            val emojiView: EmojiView = it as EmojiView
            val emoji = emojiView.emoji
            emojis[i] = emoji
            i++
        }
        return emojis
    }

    private fun saveEmojisAmounts(flexBoxLayout: FlexBoxLayout): IntArray {
        val emojisAmount = flexBoxLayout.childCount
        val amounts = IntArray(emojisAmount)
        var i = 0
        flexBoxLayout.children.forEach {
            val emojiView: EmojiView = it as EmojiView
            val amount = emojiView.amount
            amounts[i] = amount
            i++
        }
        return amounts
    }

    private fun saveEmojisStates(flexBoxLayout: FlexBoxLayout): BooleanArray {
        val emojisAmount = flexBoxLayout.childCount
        val emojisStates = BooleanArray(emojisAmount)
        var i = 0
        flexBoxLayout.children.forEach {
            val emojiView: EmojiView = it as EmojiView
            emojisStates[i] = emojiView.isSelected
            i++
        }
        return emojisStates
    }

    fun restoreState(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            return
        }

        val circleImageView: CircleImageView = findViewById(R.id.avatarView)
        val imageBitmap = savedInstanceState.getParcelable<Bitmap>(AVATAR_IMAGE_KEY)
        restoreImage(imageBitmap, circleImageView)

        val nameView: TextView = findViewById(R.id.name)
        val name = savedInstanceState.getString(NAME_KEY)
        restoreText(name, nameView)

        val messageTextView: TextView = findViewById(R.id.messageText)
        val messageText = savedInstanceState.getString(MESSAGE_TEXT_KEY)
        restoreText(messageText, messageTextView)

        val flexBoxLayout: FlexBoxLayout = findViewById(R.id.flexBoxLayout)
        val emojis = savedInstanceState.getSerializable(EMOJIS_ARRAY_KEY) as Array<Emoji>
        restoreEmojis(emojis, flexBoxLayout)

        val emojisAmounts = savedInstanceState.getIntArray(AMOUNTS_ARRAY_KEY) ?: return
        restoreEmojisAmounts(emojisAmounts, flexBoxLayout)

        val emojisStates = savedInstanceState.getBooleanArray(emojis_STATE_SELECTED_KEY) ?: return
        restoreEmojisStates(emojisStates, flexBoxLayout)
    }

    private fun restoreImage(image: Bitmap?, imageView: ImageView) {
        imageView.setImageBitmap(image)
    }

    private fun restoreText(text: String?, textView: TextView) {
        textView.text = text
    }

    private fun restoreEmojis(emojis: Array<Emoji>, flexBoxLayout: FlexBoxLayout) {
        for (i in emojis.indices) {
            val emojiView: EmojiView = flexBoxLayout.getChildAt(i) as EmojiView
            emojiView.emoji = emojis[i]
        }
    }

    private fun restoreEmojisAmounts(amounts: IntArray, flexBoxLayout: FlexBoxLayout) {
        for (i in amounts.indices) {
            val emojiView: EmojiView = flexBoxLayout.getChildAt(i) as EmojiView
            emojiView.amount = amounts[i]
        }
    }

    private fun restoreEmojisStates(states: BooleanArray, flexBoxLayout: FlexBoxLayout) {
        for (i in states.indices) {
            val emojiView: EmojiView = flexBoxLayout.getChildAt(i) as EmojiView
            emojiView.isSelected = states[i]
        }
    }

    fun setOnCLickListenerForEmojiViews() {
        findViewById<FlexBoxLayout>(R.id.flexBoxLayout).children.forEach { emojiView ->
            emojiView.setOnClickListener {
                emojiView.isSelected = !emojiView.isSelected
            }
        }
    }
}