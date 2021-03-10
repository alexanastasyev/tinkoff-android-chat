package com.example.homework2

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.Drawable
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
import androidx.appcompat.content.res.AppCompatResources.getDrawable
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

    var avatar: Drawable? = null
    set(value) {
        if (field != value) {
            field = value
            if (avatarImageView != null) {
                avatarImageView.setImageDrawable(field)
            }
            requestLayout()
        }
    }

    var userName: String = ""
        set(value) {
            if (field != value) {
                field = value
                if (nameTextView != null) {
                    nameTextView.text = field
                }
                requestLayout()
            }
        }

    var messageText: String = ""
        set(value) {
            if (field != value) {
                field = value
                if (messageTextView != null) {
                    messageTextView.text = field
                }
                requestLayout()
            }
        }

    var emojis: List<Pair<Emoji, Int>> = emptyList()
        set(value) {
            if (field != value) {
                field = value
                if (emojisLayout != null) {
                    emojisLayout.setEmojis(emojis)
                    setOnCLickListenerForEmojiViews()
                }
                requestLayout()
            }
        }

    private val nameTextView: TextView
    private val messageTextView: TextView
    private val avatarImageView: ImageView
    private val nameAndTextLayout: ConstraintLayout
    private val emojisLayout: FlexBoxLayout

    private val avatarImageViewRect = Rect()
    private val nameAndTextLayoutRect = Rect()
    private val emojisLayoutRect = Rect()

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

        context.obtainStyledAttributes(attrs, R.styleable.MessageViewGroup).apply {
            userName = getString(R.styleable.MessageViewGroup_user_name).toString()
            messageText = getString(R.styleable.MessageViewGroup_message_text).toString()
            avatar = getDrawable(R.styleable.MessageViewGroup_avatar_src)
            recycle()
        }

        avatarImageView = findViewById(R.id.avatarView)
        if (avatar != null) {
            avatarImageView.setImageDrawable(avatar)
        } else {
            avatarImageView.setImageDrawable(getDrawable(context, R.drawable.default_avatar))
        }

        emojisLayout = findViewById(R.id.emojisLayout)
//        var i = 0
//        emojisLayout.children.forEach {
//            val emojiView = it as EmojiView
//            emojiView.emoji = emojis[i]
//            i++
//        }

        nameTextView = findViewById<TextView>(R.id.name)
        nameTextView.text = userName

        messageTextView = findViewById<TextView>(R.id.messageText)
        messageTextView.text = messageText

        nameAndTextLayout = findViewById(R.id.nameAndTextLayout)

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

        val nameAndTextLayoutParams = nameAndTextLayout.layoutParams as MarginLayoutParams
        measureChildWithMargins(nameAndTextLayout, widthMeasureSpec, avatarImageViewWidth, heightMeasureSpec, 0)
        val nameAndTextLayoutHeight = nameAndTextLayout.measuredHeight + nameAndTextLayoutParams.topMargin + nameAndTextLayoutParams.bottomMargin
        val nameAndTextLayoutWidth = nameAndTextLayout.measuredWidth + nameAndTextLayoutParams.leftMargin + nameAndTextLayoutParams.rightMargin

        val emojisLayoutParams = emojisLayout.layoutParams as MarginLayoutParams
        measureChildWithMargins(emojisLayout, widthMeasureSpec, 0, heightMeasureSpec, maxOf(avatarImageViewHeight, nameAndTextLayoutHeight))
        val emojisLayoutHeight = emojisLayout.measuredHeight + emojisLayoutParams.topMargin + emojisLayoutParams.bottomMargin
        val emojisLayoutWidth = emojisLayout.measuredWidth + emojisLayoutParams.leftMargin + emojisLayoutParams.rightMargin

        val contentWidth = maxOf((avatarImageViewWidth + nameAndTextLayoutWidth), emojisLayoutWidth) + paddingLeft + paddingRight
        val contentHeight = maxOf(avatarImageViewHeight, nameAndTextLayoutHeight) + emojisLayoutHeight + paddingTop + paddingBottom

        setMeasuredDimension(
            resolveSize(contentWidth,  widthMeasureSpec),
            resolveSize(contentHeight, heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val avatarLayoutParams = avatarImageView.layoutParams as MarginLayoutParams
        val nameAndTextLayoutParams = nameAndTextLayout.layoutParams as MarginLayoutParams
        val emojisLayoutParams = emojisLayout.layoutParams as MarginLayoutParams

        avatarImageViewRect.left = avatarLayoutParams.leftMargin + paddingLeft
        avatarImageViewRect.top = avatarLayoutParams.topMargin + paddingTop
        avatarImageViewRect.right = avatarImageViewRect.left + avatarImageView.measuredWidth
        avatarImageViewRect.bottom = avatarImageViewRect.top + avatarImageView.measuredHeight
        avatarImageView.layout(avatarImageViewRect)

        nameAndTextLayoutRect.left = nameAndTextLayout.marginLeft + avatarImageViewRect.right + avatarImageView.marginRight
        nameAndTextLayoutRect.top = nameAndTextLayoutParams.topMargin + paddingTop
        nameAndTextLayoutRect.right = nameAndTextLayoutRect.left + nameAndTextLayout.measuredWidth
        nameAndTextLayoutRect.bottom = nameAndTextLayoutRect.top + nameAndTextLayout.measuredHeight
        nameAndTextLayout.layout(nameAndTextLayoutRect)

        emojisLayoutRect.left = emojisLayoutParams.leftMargin + paddingLeft
        emojisLayoutRect.top = maxOf(avatarImageViewRect.bottom, nameAndTextLayoutRect.bottom) + emojisLayoutParams.topMargin
        emojisLayoutRect.right = emojisLayoutRect.left + emojisLayout.measuredWidth
        emojisLayoutRect.bottom = emojisLayoutRect.top + emojisLayout.measuredHeight
        emojisLayout.layout(emojisLayoutRect)

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

        val emojisLayout: FlexBoxLayout = findViewById(R.id.emojisLayout)
        outState.putSerializable(EMOJIS_ARRAY_KEY, saveEmojis(emojisLayout))
        outState.putIntArray(AMOUNTS_ARRAY_KEY, saveEmojisAmounts(emojisLayout))
        outState.putBooleanArray(emojis_STATE_SELECTED_KEY, saveEmojisStates(emojisLayout))

        return outState
    }

    private fun saveImage(imageView: ImageView): Bitmap {
        val imageDrawable = imageView.drawable
        return imageDrawable.toBitmap()
    }

    private fun saveText(textView: TextView): String {
        return textView.text.toString()
    }

    private fun saveEmojis(emojisLayout: FlexBoxLayout): Array<Emoji?> {
        val emojisAmount = emojisLayout.childCount
        val emojis = arrayOfNulls<Emoji>(emojisAmount)
        var i = 0
        emojisLayout.children.forEach {
            val emojiView: EmojiView = it as EmojiView
            val emoji = emojiView.emoji
            emojis[i] = emoji
            i++
        }
        return emojis
    }

    private fun saveEmojisAmounts(emojisLayout: FlexBoxLayout): IntArray {
        val emojisAmount = emojisLayout.childCount
        val amounts = IntArray(emojisAmount)
        var i = 0
        emojisLayout.children.forEach {
            val emojiView: EmojiView = it as EmojiView
            val amount = emojiView.amount
            amounts[i] = amount
            i++
        }
        return amounts
    }

    private fun saveEmojisStates(emojisLayout: FlexBoxLayout): BooleanArray {
        val emojisAmount = emojisLayout.childCount
        val emojisStates = BooleanArray(emojisAmount)
        var i = 0
        emojisLayout.children.forEach {
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

        val emojisLayout: FlexBoxLayout = findViewById(R.id.emojisLayout)
        val emojis = savedInstanceState.getSerializable(EMOJIS_ARRAY_KEY) as Array<Emoji>
        restoreEmojis(emojis, emojisLayout)

        val emojisAmounts = savedInstanceState.getIntArray(AMOUNTS_ARRAY_KEY) ?: return
        restoreEmojisAmounts(emojisAmounts, emojisLayout)

        val emojisStates = savedInstanceState.getBooleanArray(emojis_STATE_SELECTED_KEY) ?: return
        restoreEmojisStates(emojisStates, emojisLayout)
    }

    private fun restoreImage(image: Bitmap?, imageView: ImageView) {
        imageView.setImageBitmap(image)
    }

    private fun restoreText(text: String?, textView: TextView) {
        textView.text = text
    }

    private fun restoreEmojis(emojis: Array<Emoji>, emojisLayout: FlexBoxLayout) {
        for (i in emojis.indices) {
            val emojiView: EmojiView = emojisLayout.getChildAt(i) as EmojiView
            emojiView.emoji = emojis[i]
        }
    }

    private fun restoreEmojisAmounts(amounts: IntArray, emojisLayout: FlexBoxLayout) {
        for (i in amounts.indices) {
            val emojiView: EmojiView = emojisLayout.getChildAt(i) as EmojiView
            emojiView.amount = amounts[i]
        }
    }

    private fun restoreEmojisStates(states: BooleanArray, emojisLayout: FlexBoxLayout) {
        for (i in states.indices) {
            val emojiView: EmojiView = emojisLayout.getChildAt(i) as EmojiView
            emojiView.isSelected = states[i]
        }
    }

    fun setOnCLickListenerForEmojiViews() {
        val emojiLayout = findViewById<FlexBoxLayout>(R.id.emojisLayout)
        for (i in 0 until emojiLayout.childCount - 1) {
            val emojiView = emojiLayout.getChildAt(i) as EmojiView
            emojiView.setOnClickListener {
                if (emojiView.isSelected) {
                    emojiView.isSelected = false
                    emojiView.amount -= 1
                } else {
                    emojiView.isSelected = true
                    emojiView.amount += 1
                }
            }
        }
    }
}