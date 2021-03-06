package com.example.homework2

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.marginLeft
import androidx.core.view.marginRight

class FlexboxLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    init {
        setWillNotDraw(true)
    }

    var displayWidth = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var currentWidth = 0
        var currentHeight = 0

        var contentWidth = 0
        var contentHeight = 0

        displayWidth = MeasureSpec.getSize(widthMeasureSpec)

        children.forEach {

            val layoutParams = it.layoutParams as MarginLayoutParams
            measureChildWithMargins(it, widthMeasureSpec, currentWidth, heightMeasureSpec, currentHeight)

            val childrenHeight = it.measuredHeight + layoutParams.topMargin + layoutParams.bottomMargin
            val childrenWidth = it.measuredWidth + layoutParams.leftMargin + layoutParams.rightMargin

            if (currentWidth + childrenWidth > displayWidth) {
                contentWidth = maxOf(contentWidth, currentWidth)
                currentWidth = 0
                currentHeight = maxOf(currentHeight, childrenHeight)
                contentHeight += currentHeight
                currentHeight = 0
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

        var busyWidth = 0;
        var busyHeight = 0

        children.forEach {
            val layoutParams = it.layoutParams as MarginLayoutParams
            var rect = Rect()

            if (busyWidth + it.measuredWidth + layoutParams.leftMargin + layoutParams.rightMargin > displayWidth) {
                busyHeight += it.measuredHeight + layoutParams.topMargin + layoutParams.bottomMargin
                busyWidth = 0
            }

            rect.left = busyWidth + layoutParams.leftMargin
            rect.top = busyHeight + layoutParams.topMargin
            rect.right = rect.left + it.measuredWidth + layoutParams.rightMargin
            rect.bottom = rect.top + it.measuredHeight + layoutParams.bottomMargin
            it.layout(rect)
            busyWidth += it.measuredWidth + layoutParams.leftMargin + layoutParams.rightMargin
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