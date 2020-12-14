package org.openbot.openbotcontroller

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_UP
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
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
    private var buttinsVisible: Boolean = false;
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
//                    R.id.reconnect -> {
//                        NearbyConnection.disconnect()
//                        NearbyConnection.connect(this)
//                        Log.i(TAG, "Reconnect")
//                    }
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

        createAppEventsSubscription()

        findViewById<Button>(R.id.logs).setOnTouchListener(controlButtonListener)
        findViewById<Button>(R.id.indicator_right).setOnTouchListener(controlButtonListener)
        findViewById<Button>(R.id.indicator_left).setOnTouchListener(controlButtonListener)
        findViewById<Button>(R.id.indicator_stop).setOnTouchListener(controlButtonListener)
        findViewById<Button>(R.id.noise).setOnTouchListener(controlButtonListener)
        findViewById<Button>(R.id.drive_by_network).setOnTouchListener(controlButtonListener)
        // findViewById<Button>(R.id.reconnect).setOnTouchListener(controlButtonListener)

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

        findViewById<RelativeLayout>(R.id.fullscreen_content_controls).setOnTouchListener {v: View, m: MotionEvent ->
            if (m.action == ACTION_UP) {
                toggleButtons()
            }
            true
        }

        hideSystemUI()
        hideControls ()

        NearbyConnection.connect(this)
    }

    private fun toggleButtons () {
        if (buttinsVisible) {
            hideButtons()
        } else {
            showButtons()
        }
    }

    private fun hideButtons () {
        findViewById<Button>(R.id.logs).visibility = INVISIBLE
        findViewById<Button>(R.id.indicator_right).visibility = INVISIBLE
        findViewById<Button>(R.id.indicator_left).visibility = INVISIBLE
        findViewById<Button>(R.id.indicator_stop).visibility = INVISIBLE
        findViewById<Button>(R.id.noise).visibility = INVISIBLE
        findViewById<Button>(R.id.drive_by_network).visibility = INVISIBLE

        showSliders()
        buttinsVisible = false;
    }

    private fun hideSliders () {
        findViewById<DualDriveSlider>(R.id.leftDriveControl).visibility = INVISIBLE
        findViewById<DualDriveSlider>(R.id.rightDriveControl).visibility = INVISIBLE
    }

    private fun showSliders () {
        findViewById<DualDriveSlider>(R.id.leftDriveControl).visibility = VISIBLE
        findViewById<DualDriveSlider>(R.id.rightDriveControl).visibility = VISIBLE
    }

    private fun showButtons (milliseconds: Long) {
        showButtons ()

        Handler().postDelayed({
            hideButtons()
        }, milliseconds)
    }

    private fun showButtons () {
        findViewById<Button>(R.id.logs).visibility = VISIBLE
        findViewById<Button>(R.id.indicator_right).visibility = VISIBLE
        findViewById<Button>(R.id.indicator_left).visibility = VISIBLE
        findViewById<Button>(R.id.indicator_stop).visibility = VISIBLE
        findViewById<Button>(R.id.noise).visibility = VISIBLE
        findViewById<Button>(R.id.drive_by_network).visibility = VISIBLE

        hideSliders()
        buttinsVisible = true;
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

        showButtons(3000)
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
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
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

    @Override
    override fun onResume() {
        super.onResume()
        hideSystemUI()
    }

    companion object
}
