package org.openbot.openbotcontroller.customComponents

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import org.openbot.openbotcontroller.NearbyConnection

class ConnectButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Button(context, attrs, defStyleAttr) {

    init {
        setOnTouchListener(OnTouchListener())
    }

    inner class OnTouchListener : View.OnTouchListener {
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {

            when (event?.action) {
                MotionEvent.ACTION_UP -> {
                    NearbyConnection.connect(context)
                }
            }
            return true
        }
    }
}
