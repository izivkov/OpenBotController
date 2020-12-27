package org.openbot.openbotcontroller.customComponents

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import org.openbot.openbotcontroller.R

class RightIndicator @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Button(context, attrs, defStyleAttr) {

    init {
        setOnTouchListener(OnTouchListener("{command: INDICATOR_RIGHT}"))
        subscribe("INDICATOR_RIGHT", ::onDataReceived)
    }

    private fun onDataReceived (data:String) {
        Log.i(null, "RightIndicator received: $data")
        setOnOffStates (data)
    }

     override fun setToOffState () {
        clearAnimation()
        setIconTintResource(R.color.colorPrimary)
    }

    override fun setToOnState () {
        setIconTintResource(R.color.green)
        startAnimation(BlinkerAnimation().animation)
    }
}
