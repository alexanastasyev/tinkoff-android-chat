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
    private var modeWidth = AT_MOST
    private var modeHeight = AT_MOST

    init {
        context.obtainStyledAttributes(attrs, R.styleable.EmojiView).apply {
            textSize = getDimensionPixelSize(
                R.styleable.EmojiView_cl_text_size, context.spToPx(
                    DEFAULT_FONT_SIZE_PX
                )
            )
            val textColor = getColor(R.styleable.EmojiView_cl_text_color, Color.BLACK)
            textPaint.color = textColor
            val emojiCode = getInt(R.styleable.EmojiView_cl_emoji, DEFAULT_EMOJI)
            val emoji = getEmojiByUnicode(emojiCode)
            val amount = getInt(R.styleable.EmojiView_cl_amount, DEFAULT_AMOUNT)
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
        val textWidth = textBounds.width()
        textHeight = textBounds.height()
        contentWidth = textWidth + paddingStart + paddingEnd
        contentHeight = textHeight + paddingTop + paddingBottom

        modeWidth = MeasureSpec.getMode(widthMeasureSpec)
        modeHeight = MeasureSpec.getMode(heightMeasureSpec)

        setMeasuredDimension(contentWidth, contentHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {

        val x = when(modeWidth) {
            AT_MOST -> width / 2F //textBounds.centerX().toFloat() + paddingStart
            EXACTLY -> width / 2F
            UNSPECIFIED -> width / 2F
            else -> width / 2F
        }

        val y = when(modeHeight) {
            AT_MOST -> height / 2F + paddingTop //(textBounds.height() + paddingTop).toFloat() //- textHeight / 5F
            EXACTLY -> height / 2F + paddingTop //+ (textHeight / 4F)
            UNSPECIFIED -> height / 2F + paddingTop //(textBounds.height() + paddingTop).toFloat() //- textHeight / 5F
            else -> height / 2F + paddingTop //(textBounds.height() + paddingTop).toFloat() //- textHeight / 5F
        }

        textPoint.set(x, y)
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
        private val DRAWABLES_STATE = IntArray(1) {android.R.attr.state_selected}
    }
}