package com.example.chat.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.View.MeasureSpec.*
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import com.example.chat.Emoji
import com.example.chat.R
import com.example.chat.spToPx

class EmojiView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private val textPaint = Paint().apply {
        textAlign = Paint.Align.CENTER
    }

    var textColor: Int = DEFAULT_COLOR
        set(value) {
            if (field != value) {
                field = value
                textPaint.color = field
                requestLayout()
            }
        }

    var size: Int
        get() = textPaint.textSize.toInt()
        set(value) {
            if (textPaint.textSize.toInt() != value) {
                textPaint.textSize = value.toFloat()
                requestLayout()
            }
        }

    var emoji: Emoji = DEFAULT_EMOJI
        set(value) {
            if (field != value) {
                field = value
                refreshText()
                requestLayout()
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

    private fun refreshText() {
        text = if (amount >= 0) {
            "$emoji $amount"
        } else {
            emoji.toString()
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
            size = getDimensionPixelSize(R.styleable.EmojiView_size, context.spToPx(DEFAULT_FONT_SIZE_PX))
            textColor = getColor(R.styleable.EmojiView_text_color, DEFAULT_COLOR)
            textPaint.color = textColor
            val emojiOrdinal = getInt(R.styleable.EmojiView_emoji, 0)
            emoji = Emoji.values()[emojiOrdinal]
            amount = getInt(R.styleable.EmojiView_emoji_amount, DEFAULT_AMOUNT)
            refreshText()
            recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        textPaint.getTextBounds(text, 0, text.length, textBounds)
        textWidth = textBounds.width()
        textHeight = textBounds.height()
        contentWidth = textWidth + paddingStart + paddingEnd + marginStart + marginEnd
        contentHeight = textHeight + paddingTop + paddingBottom + marginTop + marginBottom
        modeWidth = getMode(widthMeasureSpec)
        modeHeight = getMode(heightMeasureSpec)
        setMeasuredDimension(contentWidth, contentHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val textX = width / 2F
        val textY = height / 2F + getDisplacementOfTextWithEmoji(textHeight)
        textPoint.set(textX, textY)
    }

    private fun getDisplacementOfTextWithEmoji(textHeight: Number) : Float {
        val displacementFactor = 3.5F
        return textHeight.toFloat() / displacementFactor
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
        private val DEFAULT_EMOJI = Emoji.FACE_SMILING
        private const val DEFAULT_AMOUNT = 0
        private const val DEFAULT_FONT_SIZE_PX = 14F
        private const val DEFAULT_COLOR = Color.BLACK
        private val DRAWABLES_STATE = IntArray(1) {android.R.attr.state_selected}
    }
}