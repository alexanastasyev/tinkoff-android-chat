package com.example.homework2

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.marginLeft
import androidx.core.view.marginRight

class CustomViewGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    private val imageView: ImageView
    private val text: TextView
    private val name: TextView
    private val flexboxLayout: FlexboxLayout

    private val avatarRect = Rect()
    private val textRect = Rect()
    private val nameRect = Rect()
    private val flexboxLayoutRect = Rect()

    init {
        LayoutInflater.from(context).inflate(R.layout.custom_view_group, this, true)
        imageView = findViewById(R.id.avatarView)
        text = findViewById(R.id.text)
        name = findViewById(R.id.name)
        flexboxLayout = findViewById(R.id.flexbox)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        var avatarHeight = 0
        var textHeight = 0
        var nameHeight = 0
        var flexboxLayoutHeight = 0

        val circleImageViewLayoutParams = imageView.layoutParams as MarginLayoutParams
        val textLayoutParams = text.layoutParams as MarginLayoutParams
        val nameLayoutParams = name.layoutParams as MarginLayoutParams
        val flexBoxLayoutParams = flexboxLayout.layoutParams as MarginLayoutParams

        measureChildWithMargins(imageView, widthMeasureSpec, 0, heightMeasureSpec, 0)
        avatarHeight = imageView.measuredHeight + circleImageViewLayoutParams.topMargin + circleImageViewLayoutParams.bottomMargin
        val avatarWidth = imageView.measuredWidth + circleImageViewLayoutParams.leftMargin + circleImageViewLayoutParams.rightMargin

        measureChildWithMargins(name, widthMeasureSpec, avatarWidth, heightMeasureSpec, 0)
        nameHeight = name.measuredHeight + nameLayoutParams.topMargin + nameLayoutParams.bottomMargin
        val nameWidth = name.measuredWidth + nameLayoutParams.leftMargin + nameLayoutParams.rightMargin

        measureChildWithMargins(text, widthMeasureSpec, avatarWidth, heightMeasureSpec, nameHeight)
        textHeight = text.measuredHeight + textLayoutParams.topMargin + textLayoutParams.bottomMargin
        val textWidth = text.measuredWidth + textLayoutParams.leftMargin + textLayoutParams.rightMargin

        measureChildWithMargins(flexboxLayout, widthMeasureSpec, 0, heightMeasureSpec, maxOf(avatarHeight, (nameHeight + textHeight)))
        flexboxLayoutHeight = flexboxLayout.measuredHeight + flexBoxLayoutParams.topMargin + flexBoxLayoutParams.bottomMargin
        val flexboxLayoutWidth = flexboxLayout.measuredWidth + flexBoxLayoutParams.leftMargin + flexBoxLayoutParams.rightMargin

        setMeasuredDimension(
            resolveSize(maxOf((avatarWidth + textWidth + nameWidth), flexboxLayoutWidth) + paddingLeft + paddingRight,  widthMeasureSpec),
            resolveSize(maxOf(avatarHeight, textHeight + nameHeight) + flexboxLayoutHeight + paddingTop + 2*paddingBottom, heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val imageViewLayoutParams = imageView.layoutParams as MarginLayoutParams
        val textLayoutParams = text.layoutParams as MarginLayoutParams
        val nameLayoutParams = name.layoutParams as MarginLayoutParams
        val flexboxLayoutParams = flexboxLayout.layoutParams as MarginLayoutParams

        avatarRect.left = imageViewLayoutParams.leftMargin + paddingLeft
        avatarRect.top = imageViewLayoutParams.topMargin + paddingTop
        avatarRect.right = avatarRect.left + imageView.measuredWidth
        avatarRect.bottom = avatarRect.top + imageView.measuredHeight
        imageView.layout(avatarRect)

        nameRect.left = name.marginLeft + avatarRect.right + imageView.marginRight + paddingLeft
        nameRect.top = nameLayoutParams.topMargin + paddingTop
        nameRect.right = nameRect.left + name.measuredWidth
        nameRect.bottom = nameRect.top + name.measuredHeight
        name.layout(nameRect)

        textRect.left = text.marginLeft + avatarRect.right + imageView.marginRight + paddingLeft
        textRect.top = textLayoutParams.topMargin + name.measuredHeight + paddingTop
        textRect.right = textRect.left + text.measuredWidth
        textRect.bottom = textRect.top + text.measuredHeight
        text.layout(textRect)

        flexboxLayoutRect.left = flexboxLayoutParams.leftMargin + paddingLeft
        flexboxLayoutRect.top = maxOf(avatarRect.bottom, textRect.bottom) + flexboxLayoutParams.topMargin + paddingTop
        flexboxLayoutRect.right = flexboxLayoutRect.left + flexboxLayout.measuredWidth
        flexboxLayoutRect.bottom = flexboxLayoutRect.top + flexboxLayout.measuredHeight
        flexboxLayout.layout(flexboxLayoutRect)

    }

    override fun generateDefaultLayoutParams(): LayoutParams =
        MarginLayoutParams(MATCH_PARENT, WRAP_CONTENT)

    override fun generateLayoutParams(attrs: AttributeSet?) = MarginLayoutParams(context, attrs)

    override fun generateLayoutParams(p: LayoutParams?): LayoutParams = MarginLayoutParams(p)

    private fun View.layout(rect: Rect) {
        layout(rect.left, rect.top, rect.right, rect.bottom)
    }
}