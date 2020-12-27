package org.openbot.openbotcontroller.customComponents

import android.content.Context
import android.util.AttributeSet

class LogsButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Button(context, attrs, defStyleAttr) {

    init {
        setOnTouchListener(OnTouchListener("{command: LOGS}"))
        subscribe("LOGS", ::onDataReceived)
    }

    private fun onDataReceived(data: String) {
        setOnOffStates(data)
    }
}
