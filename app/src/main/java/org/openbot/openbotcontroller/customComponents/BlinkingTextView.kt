package org.openbot.openbotcontroller.customComponents

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import org.openbot.openbotcontroller.NearbyConnection

open class BlinkingTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : com.google.android.material.textview.MaterialTextView(context, attrs, defStyleAttr) {

    init {
        val anim: Animation = AlphaAnimation(0.6f, 1.0f)
        anim.duration = 500 //You can manage the blinking time with this parameter

        anim.startOffset = 20
        anim.repeatMode = Animation.REVERSE
        anim.repeatCount = Animation.INFINITE

        startAnimation(anim)
    }
    fun show() {
        visibility = VISIBLE
    }

    fun hide() {
        visibility = INVISIBLE
    }
}
