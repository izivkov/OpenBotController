package org.openbot.openbotcontroller.customComponents

import android.content.Context
import android.util.AttributeSet
import android.util.Log

class DriveModeButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Button(context, attrs, defStyleAttr) {

    init {
        setOnTouchListener(OnTouchListener("{command: DRIVE_MODE}"))
        subscribe("DRIVE_MODE", ::onDataReceived)
        setToOffState()
    }

    private fun onDataReceived (data:String) {
        Log.i(null, "DriveModeButton received: $data")
        text = data
    }
}
