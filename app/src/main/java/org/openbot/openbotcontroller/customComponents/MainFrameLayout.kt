package org.openbot.openbotcontroller.customComponents

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import org.openbot.openbotcontroller.R
import android.view.GestureDetector

class MainFrameLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    fun setupDoubleTap(functionToCallOnDoubleTap: () -> Unit) {
        class GestureListener : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                when (e?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        functionToCallOnDoubleTap()
                    }
                }

                return true
            }
        }

        val gestureDetector = GestureDetector(context, GestureListener())

        setOnTouchListener { v: View, m: MotionEvent ->
            gestureDetector.onTouchEvent(m)
            true
        }
    }

    public fun show() {
        visibility = VISIBLE
    }

    public fun hide() {
        visibility = INVISIBLE
    }

}
