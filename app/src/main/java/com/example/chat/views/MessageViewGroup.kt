package com.example.chat.views

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
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
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.*
import com.example.chat.Emoji
import com.example.chat.R
import com.example.chat.dpToPx
import com.example.chat.spToPx

class MessageViewGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    companion object {
        private const val AVATAR_SIZE = 50F
        private const val MESSAGE_MINIMAL_SIDE_MARGIN = 80F
        private const val EMOJIS_LAYOUT_MARGIN = 8F

        const val EMOJIS_PADDING = 2F

        private const val DEFAULT_TEXT_SIZE = 16
        private const val DEFAULT_EMOJIS_TEXT_SIZE = 14F

        const val ALIGN_LEFT = 0
        const val ALIGN_RIGHT = 1
    }

    var messageId: Long? = null

    var textSize: Int = DEFAULT_TEXT_SIZE
        set(value) {
            if (field != value) {
                field = value
                requestLayout()
            }
        }

    var align: Int = ALIGN_LEFT
        set(value) {
            if (field != value) {
                field = value
                requestLayout()
            }
        }

    var avatar: Drawable? = null
    set(value) {
        if (field != value) {
            field = value
            if (align == ALIGN_LEFT) {
                avatarImageView.setImageDrawable(field)
            }
            requestLayout()
        }
    }

    // All these null-checks below are necessary. Otherwise the app crushes.

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

    var messageBackground: Drawable? = null
        set(value) {
            if (field != value) {
                field = value
                if (nameAndTextLayout != null) {
                    nameAndTextLayout.background = field
                }
                requestLayout()
            }
        }

    var reactions: ArrayList<Pair<Emoji, Int>> = arrayListOf()
        set(value) {
            if (field != value) {
                field = value
                if (emojisLayout != null) {
                    setReactionsInEmojisLayout(reactions)
                    setOnClickListenerForEmojiViews(clickListenerForEmojis)
                    setOnPlusClickListener(emojiPlusClickListener)
                }
                requestLayout()
            }
        }

    private lateinit var clickListenerForEmojis: OnClickListener
    private lateinit var emojiPlusClickListener: OnClickListener

    private val nameTextView: TextView
    private val messageTextView: TextView
    private val nameAndTextLayout: ConstraintLayout
    val avatarImageView: ImageView
    val emojisLayout: FlexBoxLayout

    private val avatarImageViewRect = Rect()
    private val nameAndTextLayoutRect = Rect()
    private val emojisLayoutRect = Rect()

    init {
        LayoutInflater.from(context).inflate(R.layout.message_view_group, this, true)

        context.obtainStyledAttributes(attrs, R.styleable.MessageViewGroup).apply {

            textSize = getDimensionPixelSize(R.styleable.MessageViewGroup_text_size,  DEFAULT_TEXT_SIZE)
            userName = getString(R.styleable.MessageViewGroup_user_name).toString()
            messageText = getString(R.styleable.MessageViewGroup_message_text).toString()
            avatar = getDrawable(R.styleable.MessageViewGroup_avatar_src)
            align = getInt(R.styleable.MessageViewGroup_align, 0)
            messageBackground = getDrawable(R.styleable.MessageViewGroup_message_background)
            if (messageBackground == null) {
                messageBackground = ResourcesCompat.getDrawable(resources, R.drawable.message_name_and_text_bg, null)
            }
            recycle()
        }

        avatarImageView = findViewById(R.id.avatarView)
        setAvatarPicture()

        emojisLayout = findViewById(R.id.emojisLayout)

        nameTextView = findViewById(R.id.name)
        nameTextView.textSize = textSize.toFloat()
        nameTextView.text = userName

        messageTextView = findViewById(R.id.messageText)
        messageTextView.textSize = textSize.toFloat()
        messageTextView.text = messageText

        nameAndTextLayout = findViewById(R.id.nameAndTextLayout)
        nameAndTextLayout.background = messageBackground

        setAvatarSize()
    }

    private fun setAvatarPicture() {
        if (align == ALIGN_LEFT) {
            if (avatar != null) {
                avatarImageView.setImageDrawable(avatar)
            } else {
                avatarImageView.setImageDrawable(getDrawable(context, R.drawable.default_avatar))
            }
        }
    }

    private fun setAvatarSize() {
        when (align) {
            ALIGN_LEFT -> {
                layoutParams = LinearLayout.LayoutParams(dpToPx(AVATAR_SIZE, resources), dpToPx(AVATAR_SIZE, resources))
            }
            ALIGN_RIGHT -> {
                layoutParams = LinearLayout.LayoutParams(dpToPx(0F, resources), dpToPx(0F, resources))
                avatarImageView.visibility = INVISIBLE
            }
        }
        avatarImageView.layoutParams = layoutParams
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val avatarImageViewLayoutParams = avatarImageView.layoutParams as MarginLayoutParams
        when (align) {
            ALIGN_LEFT -> {
                avatarImageViewLayoutParams.width = dpToPx(AVATAR_SIZE, resources)
                avatarImageViewLayoutParams.height = dpToPx(AVATAR_SIZE, resources)
            }
            ALIGN_RIGHT -> {
                avatarImageViewLayoutParams.width = 0
                avatarImageViewLayoutParams.height = 0
            }
        }
        measureChildWithMargins(avatarImageView, widthMeasureSpec, 0, heightMeasureSpec, 0)
        val avatarImageViewHeight = avatarImageView.measuredHeight + avatarImageViewLayoutParams.topMargin + avatarImageViewLayoutParams.bottomMargin
        val avatarImageViewWidth = avatarImageView.measuredWidth + avatarImageViewLayoutParams.leftMargin + avatarImageViewLayoutParams.rightMargin

        val nameAndTextLayoutParams = nameAndTextLayout.layoutParams as MarginLayoutParams
        when (align) {
            ALIGN_LEFT -> {
                nameAndTextLayoutParams.setMargins(nameAndTextLayoutParams.leftMargin, nameAndTextLayoutParams.topMargin, dpToPx(MESSAGE_MINIMAL_SIDE_MARGIN, resources), 0)
            }
            ALIGN_RIGHT -> {
                nameAndTextLayoutParams.setMargins(dpToPx(MESSAGE_MINIMAL_SIDE_MARGIN, resources), nameAndTextLayoutParams.topMargin, nameAndTextLayoutParams.rightMargin, 0)
            }
        }
        measureChildWithMargins(nameAndTextLayout, widthMeasureSpec, avatarImageViewWidth, heightMeasureSpec, 0)
        val nameAndTextLayoutHeight = nameAndTextLayout.measuredHeight + nameAndTextLayoutParams.topMargin + nameAndTextLayoutParams.bottomMargin
        val nameAndTextLayoutWidth = nameAndTextLayout.measuredWidth + nameAndTextLayoutParams.leftMargin + nameAndTextLayoutParams.rightMargin

        val emojisLayoutParams = emojisLayout.layoutParams as MarginLayoutParams
        when (align) {
            ALIGN_LEFT -> {
                val parentWidth = MeasureSpec.getSize(widthMeasureSpec)
                val rightMargin = parentWidth - nameAndTextLayoutWidth - avatarImageViewWidth - avatarImageViewLayoutParams.leftMargin - avatarImageViewLayoutParams.rightMargin - nameAndTextLayoutParams.leftMargin
                emojisLayoutParams.setMargins(avatarImageView.measuredWidth + nameAndTextLayoutParams.marginStart, dpToPx(0F, resources), rightMargin + nameAndTextLayoutParams.rightMargin, dpToPx(EMOJIS_LAYOUT_MARGIN, resources))
            }
            ALIGN_RIGHT -> {
                val parentWidth = MeasureSpec.getSize(widthMeasureSpec)
                val leftMargin = parentWidth - nameAndTextLayoutWidth
                emojisLayoutParams.setMargins(leftMargin + nameAndTextLayoutParams.leftMargin, dpToPx(0F, resources), 0, dpToPx(EMOJIS_LAYOUT_MARGIN, resources))
            }
        }
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
        emojisLayoutParams.setMargins(avatarImageView.measuredWidth + nameAndTextLayoutParams.marginStart, dpToPx(EMOJIS_LAYOUT_MARGIN, resources), 0, 0)

        when (align) {
            ALIGN_LEFT -> {
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
            ALIGN_RIGHT -> {
                nameAndTextLayoutRect.right = width - (nameAndTextLayoutParams.rightMargin + paddingEnd)
                nameAndTextLayoutRect.top = nameAndTextLayoutParams.topMargin + paddingTop
                nameAndTextLayoutRect.left = nameAndTextLayoutRect.right - nameAndTextLayout.measuredWidth
                nameAndTextLayoutRect.bottom = nameAndTextLayoutRect.top + nameAndTextLayout.measuredHeight
                nameAndTextLayout.layout(nameAndTextLayoutRect)

                emojisLayoutRect.left = nameAndTextLayoutRect.left
                emojisLayoutRect.top = maxOf(avatarImageViewRect.bottom, nameAndTextLayoutRect.bottom) + emojisLayoutParams.topMargin
                emojisLayoutRect.right = nameAndTextLayoutRect.right
                emojisLayoutRect.bottom = emojisLayoutRect.top + emojisLayout.measuredHeight
                emojisLayout.layout(emojisLayoutRect)
            }
        }

    }

    override fun generateDefaultLayoutParams(): LayoutParams = MarginLayoutParams(MATCH_PARENT, WRAP_CONTENT)

    override fun generateLayoutParams(attrs: AttributeSet?) = MarginLayoutParams(context, attrs)

    override fun generateLayoutParams(p: LayoutParams?): LayoutParams = MarginLayoutParams(p)

    private fun View.layout(rect: Rect) {
        layout(rect.left, rect.top, rect.right, rect.bottom)
    }

    private fun setReactionsInEmojisLayout(reactions: List<Pair<Emoji, Int>>) {
        emojisLayout.removeAllViews()
        for (i in reactions.indices) {
            val newEmojiView = EmojiView(context)
            newEmojiView.emoji = reactions[i].first
            newEmojiView.amount = reactions[i].second
            setDefaultEmojiViewParams(newEmojiView)
            emojisLayout.addView(newEmojiView)
        }
        setLastEmojiPlus()
    }

    private fun setLastEmojiPlus() {
        if (reactions.isEmpty()) {
            return
        }
        val emojiPlus = EmojiView(context)
        emojiPlus.emoji = Emoji.SIGN_PLUS
        emojiPlus.amount = -1
        setOnPlusClickListener(emojiPlusClickListener)
        setDefaultEmojiViewParams(emojiPlus)
        emojisLayout.addView(emojiPlus)
    }

    private fun setDefaultEmojiViewParams(emojiView: EmojiView) {
        val emojiLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        )
        emojiLayoutParams.setMargins(
                dpToPx(0F, resources),
                dpToPx(2F, resources),
                dpToPx(EMOJIS_LAYOUT_MARGIN, resources),
                dpToPx(EMOJIS_LAYOUT_MARGIN, resources)
        )
        emojiView.layoutParams = emojiLayoutParams
        emojiView.size = spToPx(DEFAULT_EMOJIS_TEXT_SIZE, resources)
        emojiView.background = ResourcesCompat.getDrawable(resources, R.drawable.emoji_view_bg, null)
        emojiView.setPadding(dpToPx(EMOJIS_PADDING, resources))
        emojiView.textColor = ResourcesCompat.getColor(resources, R.color.text_color, null)
    }

    fun setOnClickListenerForEmojiViews(clickListener: OnClickListener) {
        this.clickListenerForEmojis = clickListener
        val emojiLayout = findViewById<FlexBoxLayout>(R.id.emojisLayout)
        for (i in 0 until emojiLayout.childCount - 1) {
            emojiLayout.getChildAt(i).setOnClickListener(clickListener)
        }
    }

    fun setOnLongClickListenerForMessages(clickListener: OnLongClickListener) {
        this.nameAndTextLayout.setOnLongClickListener(clickListener)
    }

    fun setOnPlusClickListener(clickListener: OnClickListener) {
        this.emojiPlusClickListener = clickListener
        val emojiLayoutLocal = findViewById<FlexBoxLayout>(R.id.emojisLayout)
        if (emojiLayoutLocal.childCount != 0) {
            emojiLayoutLocal.getChildAt(emojiLayoutLocal.childCount - 1).setOnClickListener(clickListener)
        }
    }

    fun addEmojiView(emojiView: EmojiView) {
        reactions.add(Pair(emojiView.emoji, emojiView.amount))
        setReactionsInEmojisLayout(reactions)
        setOnClickListenerForEmojiViews(clickListenerForEmojis)
        setOnPlusClickListener(emojiPlusClickListener)
    }

    fun removeEmojiView(emojiView: EmojiView) {
        var emojiToRemoveIndex = -1
        for (i in 0 until reactions.size) {
            if (reactions[i].first == emojiView.emoji) {
                emojiToRemoveIndex = i
            }
        }
        reactions.remove(reactions[emojiToRemoveIndex])

        setReactionsInEmojisLayout(reactions)
        setOnClickListenerForEmojiViews(clickListenerForEmojis)
    }
}