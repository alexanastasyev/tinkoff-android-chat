package com.example.homework2

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children

class FlexBoxLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    private var parentWidth = 0
    private val layoutRect = Rect()

    init {
        setWillNotDraw(true)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var currentWidth = 0
        var currentHeight = 0
        var contentWidth = 0
        var contentHeight = 0
        parentWidth = MeasureSpec.getSize(widthMeasureSpec)
        children.forEach {
            val layoutParams = it.layoutParams as MarginLayoutParams
            measureChildWithMargins(it, widthMeasureSpec, currentWidth, heightMeasureSpec, currentHeight)
            val childrenHeight = it.measuredHeight + layoutParams.topMargin + layoutParams.bottomMargin
            val childrenWidth = it.measuredWidth + layoutParams.leftMargin + layoutParams.rightMargin
            if (currentWidth + childrenWidth > parentWidth) {
                contentWidth = maxOf(contentWidth, currentWidth)
                currentWidth = 0
                currentHeight = maxOf(currentHeight, childrenHeight)
                contentHeight += currentHeight

                // Here we moved to a new line
                currentWidth += childrenWidth
                contentWidth = maxOf(contentWidth, currentWidth)
            } else {
                currentWidth += childrenWidth
                contentWidth = maxOf(contentWidth, currentWidth)
                currentHeight = maxOf(currentHeight, childrenHeight)
                contentHeight = maxOf(contentHeight, currentHeight)
            }
        }
        setMeasuredDimension(contentWidth, contentHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var currentWidth = 0
        var currentHeight = 0
        children.forEach {
            val layoutParams = it.layoutParams as MarginLayoutParams
            if (currentWidth + it.measuredWidth + layoutParams.leftMargin + layoutParams.rightMargin > parentWidth) {
                currentHeight += it.measuredHeight + layoutParams.topMargin + layoutParams.bottomMargin
                currentWidth = 0
            }
            layoutRect.left = currentWidth + layoutParams.leftMargin
            layoutRect.top = currentHeight + layoutParams.topMargin
            layoutRect.right = layoutRect.left + it.measuredWidth + layoutParams.rightMargin
            layoutRect.bottom = layoutRect.top + it.measuredHeight + layoutParams.bottomMargin
            it.layout(layoutRect)
            currentWidth += it.measuredWidth + layoutParams.leftMargin + layoutParams.rightMargin
        }
    }

    override fun generateDefaultLayoutParams(): LayoutParams =
        MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

    override fun generateLayoutParams(attrs: AttributeSet?) = MarginLayoutParams(context, attrs)

    override fun generateLayoutParams(p: LayoutParams?): LayoutParams = MarginLayoutParams(p)

    private fun View.layout(rect: Rect) {
        layout(rect.left, rect.top, rect.right, rect.bottom)
    }
}