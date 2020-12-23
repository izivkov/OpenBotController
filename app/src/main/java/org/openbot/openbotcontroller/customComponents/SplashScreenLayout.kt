package org.openbot.openbotcontroller.customComponents

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout

class SplashScreenLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    init {
    }

    fun show() {
        visibility = VISIBLE
    }

    fun hide() {
        visibility = INVISIBLE
    }
}
