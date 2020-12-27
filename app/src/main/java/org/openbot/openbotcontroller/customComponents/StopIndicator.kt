package org.openbot.openbotcontroller.customComponents

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import org.openbot.openbotcontroller.R

class StopIndicator @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Button(context, attrs, defStyleAttr) {

    init {
        setOnTouchListener(OnTouchListener("{command: INDICATOR_STOP}"))
        subscribe("INDICATOR_STOP", ::onDataReceived)
    }

    private fun onDataReceived(data: String) {
        Log.i(null, "StopIndicator received: $data")
        setOnOffStates(data)
    }

    override fun setToOffState() {
        setIconTintResource(R.color.red)
    }

    override fun setToOnState() {
        setIconTintResource(R.color.colorPrimary)
    }
}
