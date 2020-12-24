package org.openbot.openbotcontroller.customComponents

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import org.openbot.openbotcontroller.NearbyConnection

class ExitButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Button(context, attrs, defStyleAttr) {

    init {
        setOnTouchListener(OnTouchListener())
    }

    inner class OnTouchListener : View.OnTouchListener {
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            NearbyConnection.disconnect()
            (context as Activity).finish()
            System.exit(0)

            return true
        }
    }
}