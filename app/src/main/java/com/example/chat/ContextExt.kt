package com.example.chat

import android.content.Context
import android.content.res.Resources
import androidx.annotation.Px
import kotlin.math.roundToInt

@Px
fun Context.spToPx(sp: Float): Int {
    return spToPx(sp, resources)
}

@Px
fun Context.dpToPx(dp: Float): Int {
    return dpToPx(dp, resources)
}

@Px
fun spToPx(sp: Float, resources: Resources): Int {
    return (sp * resources.displayMetrics.scaledDensity).roundToInt()
}

@Px
fun dpToPx(dp: Float, resources: Resources): Int {
    return (dp * resources.displayMetrics.density).roundToInt()
}