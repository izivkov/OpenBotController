package org.openbot.openbotcontroller.customComponents

import android.content.Context
import android.util.AttributeSet
import android.util.Log

class LogsButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Button(context, attrs, defStyleAttr) {

    init {
        setOnTouchListener(OnTouchListener("{command: LOGS}"))
        subscribe("LOGS", ::onDataReceived)
    }

    private fun onDataReceived (data:String) {
        Log.i(null, "LogsButton received: $data")
        setOnOffStates (data)
    }
}
