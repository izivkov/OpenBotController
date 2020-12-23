package org.openbot.openbotcontroller.customComponents

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import org.openbot.openbotcontroller.R
import android.view.GestureDetector
import android.widget.RelativeLayout

class DriveModeControlsLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    public fun show() {
        visibility = VISIBLE
    }

    public fun hide() {
        visibility = INVISIBLE
    }
}
