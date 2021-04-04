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
import com.example.chat.entities.Emoji
import com.example.chat.R
import com.example.chat.spToPx
import java.io.Serializable

class EmojiView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes), Serializable {

    companion object {
        private val DEFAULT_EMOJI = Emoji(0x1F600)
        private const val DEFAULT_AMOUNT = 0
        private const val DEFAULT_FONT_SIZE_PX = 14F
        private const val DEFAULT_COLOR = Color.BLACK
        private val DRAWABLES_STATE = IntArray(1) {android.R.attr.state_selected}
    }

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
            val emojiUnicode = getInt(R.styleable.EmojiView_emoji_unicode, 0)
            emoji = Emoji(emojiUnicode)
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
        val textY = height / 2F + getOffsetOfTextWithEmoji(textHeight)
        textPoint.set(textX, textY)
    }

    private fun getOffsetOfTextWithEmoji(textHeight: Int) : Float {
        val offsetFactor = 3.5F
        return textHeight.toFloat() / offsetFactor
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawText(text, textPoint.x, textPoint.y, textPaint)
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isSelected) {
            mergeDrawableStates(drawableState, DRAWABLES_STATE)
        }
        return drawableState
    }
}