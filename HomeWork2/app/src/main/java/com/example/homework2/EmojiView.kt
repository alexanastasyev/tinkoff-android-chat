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

    private var text: String = getEmojiByUnicode(DEFAULT_EMOJI) + DEFAULT_AMOUNT
        set(value) {
            if (field != value) {
                field = value
                requestLayout()
            }
        }

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
            val emojiCode = getInt(R.styleable.EmojiView_emoji, DEFAULT_EMOJI)
            val emoji = getEmojiByUnicode(emojiCode)
            val amount = getInt(R.styleable.EmojiView_emoji_amount, DEFAULT_AMOUNT)
            if (amount >= 0) {
                this@EmojiView.text = "$emoji $amount"
            } else {
                this@EmojiView.text = emoji
            }
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

    private fun getEmojiByUnicode(unicode: Int): String {
        return String(Character.toChars(unicode))
    }

    companion object {
        private const val DEFAULT_EMOJI = 0x1F600
        private const val DEFAULT_AMOUNT = 0
        private const val DEFAULT_FONT_SIZE_PX = 14F
        private const val DEFAULT_COLOR = Color.BLACK
        private val DRAWABLES_STATE = IntArray(1) {android.R.attr.state_selected}
    }
}