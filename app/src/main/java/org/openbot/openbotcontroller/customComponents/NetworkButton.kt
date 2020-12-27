package org.openbot.openbotcontroller.customComponents

import android.content.Context
import android.util.AttributeSet

class NetworkButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Button(context, attrs, defStyleAttr) {

    init {
        setOnTouchListener(OnTouchListener("{command: NETWORK}"))
        subscribe("NETWORK", ::onDataReceived)
        setToOffState()
    }

    private fun onDataReceived(data: String) {
        setOnOffStates(data)
    }
}
