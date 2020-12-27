package org.openbot.openbotcontroller.customComponents

import android.content.Context
import android.util.AttributeSet
import android.util.Log

class NoiseButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Button(context, attrs, defStyleAttr) {

    init {
        setOnTouchListener(OnTouchListener("{command: NOISE}"))
        subscribe("NOISE", ::onDataReceived)
        setToOffState()
    }
    private fun onDataReceived (data:String) {
        Log.i(null, "NoiseButton received: $data")
        setOnOffStates (data)
    }
}
