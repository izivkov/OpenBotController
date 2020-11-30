package org.openbot.openbotcontroller.utils

import android.media.AudioManager
import android.media.ToneGenerator

object Utils {

    enum class TONE {
        PIP,
        ALERT,
        INTERCEPT
    }

    fun beep(tone: TONE = TONE.PIP, duration: Int = 150) {
        val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        val tgTone = when (tone) {
            TONE.INTERCEPT -> {
                ToneGenerator.TONE_CDMA_ABBR_INTERCEPT
            }
            TONE.ALERT -> {
                ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD
            }
            else -> {
                ToneGenerator.TONE_CDMA_PIP
            }
        }
        toneGen.startTone(tgTone, duration)
    }
}