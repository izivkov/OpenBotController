/*
 * Developed for the OpenBot project (https://openbot.org) by:
 *
 * Ivo Zivkov
 * izivkov@gmail.com
 *
 * Date: 2020-12-27, 10:57 p.m.
 */

package org.openbot.openbotcontroller.customComponents

import android.content.Context
import android.util.AttributeSet
import org.openbot.openbotcontroller.R

class LeftIndicator @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Button(context, attrs, defStyleAttr) {

    init {
        setOnTouchListener(OnTouchListener("{command: INDICATOR_LEFT}"))
        subscribe("INDICATOR_LEFT", ::onDataReceived)
    }

    private fun onDataReceived(data: String) {
        setOnOffStates(data)
    }

    override fun setToOffState() {
        clearAnimation()
        setIconTintResource(R.color.colorPrimary)
    }

    override fun setToOnState() {
        setIconTintResource(R.color.green)
        startAnimation(BlinkerAnimation().animation)
    }
}
