package org.openbot.openbotcontroller.customComponents

import android.content.Context
import android.util.AttributeSet

class LeftIndicator @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Button(context, attrs, defStyleAttr) {

    init {
        setOnTouchListener(OnTouchListener("{command: INDICATOR_LEFT}"))
    }
}
