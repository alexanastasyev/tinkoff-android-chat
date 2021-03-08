package com.example.homework2

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.View.MeasureSpec.*

class EmojiView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private val textPaint = Paint().apply {
        textAlign = Paint.Align.CENTER
    }

    private var textSize: Int
        get() = textPaint.textSize.toInt()
        set(value) {
            if (textPaint.textSize.toInt() != value) {
                textPaint.textSize = value.toFloat()
                requestLayout()
            }
        }

    var emoji: String = getEmojiByUnicode(DEFAULT_EMOJI_CODE)
        set(value) {
            if (field != value) {
                field = value
                refreshText()
                requestLayout()
            }
        }

    private fun refreshText() {
        if (amount >= 0) {
            this@EmojiView.text = "$emoji $amount"
        } else {
            this@EmojiView.text = emoji
        }
    }

    var amount: Int = DEFAULT_AMOUNT
        set(value) {
            if (field != value) {
                field = value
                refreshText()
                requestLayout()
            }
        }

    private var text: String = ""
    private val textPoint = PointF()
    private val textBounds = Rect()
    private var contentWidth = 0
    private var contentHeight = 0
    private var textHeight = 0
    private var textWidth = 0
    private var modeWidth = AT_MOST
    private var modeHeight = AT_MOST

    init {
        context.obtainStyledAttributes(attrs, R.styleable.EmojiView).apply {
            textSize = getDimensionPixelSize(
                R.styleable.EmojiView_size, context.spToPx(
                    DEFAULT_FONT_SIZE_PX
                )
            )
            val textColor = getColor(R.styleable.EmojiView_text_color, DEFAULT_COLOR)
            textPaint.color = textColor
            val emojiCode = getInt(R.styleable.EmojiView_emoji, DEFAULT_EMOJI_CODE)
            emoji = getEmojiByUnicode(emojiCode)
            amount = getInt(R.styleable.EmojiView_emoji_amount, DEFAULT_AMOUNT)
            refreshText()
            recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        textPaint.getTextBounds(text, 0, text.length, textBounds)
        textWidth = textBounds.width()
        textHeight = textBounds.height()
        contentWidth = textWidth + paddingStart + paddingEnd
        contentHeight = textHeight + paddingTop + paddingBottom
        modeWidth = getMode(widthMeasureSpec)
        modeHeight = getMode(heightMeasureSpec)
        setMeasuredDimension(contentWidth + paddingLeft + paddingRight, contentHeight + paddingTop + paddingBottom)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val textX = width / 2F

        // When we use an emoji, we have to to move it down on (textHeight / 3.5) to place it in the middle.
        val textY = height / 2F + textHeight / 3.5F
        textPoint.set(textX, textY)
    }

    override fun onDraw(canvas: Canvas) {
        val canvasCount = canvas.save()
        canvas.drawText(text, textPoint.x, textPoint.y, textPaint)
        canvas.restoreToCount(canvasCount)
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isSelected) {
            mergeDrawableStates(drawableState, DRAWABLES_STATE)
        }
        return drawableState
    }

    companion object {
        private const val DEFAULT_EMOJI_CODE = 0x1F600
        private const val DEFAULT_AMOUNT = 0
        private const val DEFAULT_FONT_SIZE_PX = 14F
        private const val DEFAULT_COLOR = Color.BLACK
        private val DRAWABLES_STATE = IntArray(1) {android.R.attr.state_selected}

        val EMOJI_FACE_SMILING = getEmojiByUnicode(0x1F600)
        val EMOJI_FACE_LAUGHING = getEmojiByUnicode(0x1F602)
        val EMOJI_FACE_WINKING = getEmojiByUnicode(0x1F609)
        val EMOJI_FACE_IN_LOVE = getEmojiByUnicode(0x1F60D)
        val EMOJI_FACE_KISSING = getEmojiByUnicode(0x1F618)
        val EMOJI_FACE_NEUTRAL = getEmojiByUnicode(0x1F610)
        val EMOJI_FACE_WITH_SUNGLASSES = getEmojiByUnicode(0x1F60E)
        val EMOJI_FACE_CRYING = getEmojiByUnicode(0x1F622)
        val EMOJI_FACE_WITH_TONGUE = getEmojiByUnicode(0x1F61B)
        val EMOJI_FACE_WITH_RAISED_EYEBROW = getEmojiByUnicode(0x1F928)
        val EMOJI_FACE_SMIRKING = getEmojiByUnicode(0x1F60F)
        val EMOJI_FACE_RELIEVED = getEmojiByUnicode(0x1F60C)
        val EMOJI_FACE_COWBOY_HAT = getEmojiByUnicode(0x1F920)
        val EMOJI_FACE_ASTONISHED = getEmojiByUnicode(0x1F632)
        val EMOJI_FACE_WEARY = getEmojiByUnicode(0x1F629)
        val EMOJI_SKULL = getEmojiByUnicode(0x1F480)
        val EMOJI_SIGN_PLUS = getEmojiByUnicode(0x2795)

        fun getEmojiByUnicode(unicode: Int): String {
            return String(Character.toChars(unicode))
        }
    }
}