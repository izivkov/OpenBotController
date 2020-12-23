package org.openbot.openbotcontroller.customComponents

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import org.openbot.openbotcontroller.NearbyConnection

open class Button @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : com.google.android.material.button.MaterialButton(context, attrs, defStyleAttr) {

    fun show() {
        visibility = VISIBLE
    }

    fun hide() {
        visibility = INVISIBLE
    }

    protected fun sendMessage(message: String) {
        NearbyConnection.sendMessage(message)
    }

    inner class OnTouchListener(private val command:String) : View.OnTouchListener {
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            when (event?.action) {
                MotionEvent.ACTION_UP -> {
                    sendMessage(command)
                }
            }
            return false
        }
    }
}
