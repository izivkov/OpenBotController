package org.openbot.openbotcontroller

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.openbot.openbotcontroller.customComponents.DualDriveSlider
import org.openbot.openbotcontroller.customComponents.IDriveValue
import org.openbot.openbotcontroller.utils.EventProcessor
import kotlin.Float as DrivePositionAsFloatBetweenMinusOneAndOne


@Suppress("DEPRECATION")
class OpenbotControllerActivity : AppCompatActivity() {
    private val TAG = "OpenbotControllerActivity"
    enum class LeftOrRight { LEFT, RIGHT }

    private val controlButtonListener = View.OnTouchListener { view, motionEvent ->
        val any = when (motionEvent.action) {
            MotionEvent.ACTION_UP -> {
                when (view.id) {
                    R.id.logs -> {
                        NearbyConnection.sendMessage("{buttonValue: LOGS}")
                    }
                    R.id.indicator_right -> {
                        NearbyConnection.sendMessage("{buttonValue: INDICATOR_RIGHT}")
                    }
                    R.id.indicator_left -> {
                        NearbyConnection.sendMessage("{buttonValue: INDICATOR_LEFT}")
                    }
                    R.id.indicator_stop -> {
                        NearbyConnection.sendMessage("{buttonValue: INDICATOR_STOP}")
                    }
                    R.id.noise -> {
                        NearbyConnection.sendMessage("{buttonValue: NOISE}")
                    }
                    R.id.drive_by_network -> {
                        NearbyConnection.sendMessage("{buttonValue: DRIVE_BY_NETWORK}")
                        Log.i(TAG, "Stop")
                    }
                    R.id.reconnect -> {
                        NearbyConnection.disconnect()
                        NearbyConnection.connect(this)
                        Log.i(TAG, "Reconnect")
                    }
                }
                view.performClick()
            }

            else -> {
            }
        }
        false
    }

    // This class combines, coalesces and throttles signals from the left and right drive DualDriveSlider and sends them to the bot.
    object DriveCommandEmitter {
        private var lastTransmitted: Long = System.currentTimeMillis()
        private const val MIN_TIME_BETWEEN_TRANSMISSIONS = 50 // ms
        private var lastRightValue = 0f
        private var lastLeftValue = 0f

        fun controlInput(value: kotlin.Float, leftOrRight: LeftOrRight) {
            if (leftOrRight == LeftOrRight.LEFT) lastLeftValue = value else lastRightValue = value

            if ((System.currentTimeMillis() - lastTransmitted) >= MIN_TIME_BETWEEN_TRANSMISSIONS) {
                val msg = "{r:${lastRightValue}, l:${lastLeftValue}}"
                NearbyConnection.sendMessage(msg)
                lastTransmitted = System.currentTimeMillis()

                Log.i("", "Sending ${msg}")
            }
        }
    }

    class DriveValue(private val direction: LeftOrRight): IDriveValue {
        override operator fun invoke(x: DrivePositionAsFloatBetweenMinusOneAndOne): DrivePositionAsFloatBetweenMinusOneAndOne {

            DriveCommandEmitter.controlInput(x, direction)
            return x;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_fullscreen)

        val controlsContainer: RelativeLayout =
            findViewById<RelativeLayout>(R.id.fullscreen_content_controls)

        controlsContainer.visibility = View.GONE;
        createAppEventsSubscription()

        findViewById<Button>(R.id.logs).setOnTouchListener(controlButtonListener)
        findViewById<Button>(R.id.indicator_right).setOnTouchListener(controlButtonListener)
        findViewById<Button>(R.id.indicator_left).setOnTouchListener(controlButtonListener)
        findViewById<Button>(R.id.indicator_stop).setOnTouchListener(controlButtonListener)
        findViewById<Button>(R.id.noise).setOnTouchListener(controlButtonListener)
        findViewById<Button>(R.id.drive_by_network).setOnTouchListener(controlButtonListener)
        findViewById<Button>(R.id.reconnect).setOnTouchListener(controlButtonListener)

        findViewById<DualDriveSlider>(R.id.leftDriveControl).setOnValueChangedListener(
            DriveValue(
                LeftOrRight.LEFT
            )
        )
        findViewById<DualDriveSlider>(R.id.rightDriveControl).setOnValueChangedListener(
            DriveValue(
                LeftOrRight.RIGHT
            )
        )

        hideSystemUI()
        NearbyConnection.connect(this)
    }

    private fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let {
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                window.navigationBarColor = getColor(R.color.colorPrimaryDark)
                it.hide(WindowInsets.Type.systemBars())
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            @Suppress("DEPRECATION")
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        }
    }

    @Override
    override fun onPause() {
        super.onPause()
        NearbyConnection.disconnect()
    }

    private fun hideControls () {
        findViewById<RelativeLayout>(R.id.fullscreen_content_controls).visibility =
            View.GONE
        findViewById<LinearLayout>(R.id.splash_screen).visibility = View.VISIBLE
    }
    private fun showControlls () {
        findViewById<LinearLayout>(R.id.splash_screen).visibility = View.GONE
        findViewById<RelativeLayout>(R.id.fullscreen_content_controls).visibility =
            View.VISIBLE
    }

    private fun createAppEventsSubscription(): Disposable =
        EventProcessor.connectionEventFlowable
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                Log.i(TAG, "Got ${it} event")

                when (it) {
                    EventProcessor.ProgressEvents.ConnectionSuccessful -> {
                        showControlls()
                    }
                    EventProcessor.ProgressEvents.ConnectionStarted -> {
                    }
                    EventProcessor.ProgressEvents.ConnectionFailed -> {
                        hideControls()
                    }
                    EventProcessor.ProgressEvents.StartAdvertising -> {
                        hideControls()
                    }
                    EventProcessor.ProgressEvents.Disconnecting -> {
                    }
                    EventProcessor.ProgressEvents.StopAdvertising -> {
                    }
                    EventProcessor.ProgressEvents.AdvertisingFailed -> {
                        hideControls()
                    }
                }
            }
            .subscribe(
                { },
                { throwable ->
                    Log.d(
                        "createAppEventsSubscription",
                        "Got error on subscribe: $throwable"
                    )
                })

    companion object
}
