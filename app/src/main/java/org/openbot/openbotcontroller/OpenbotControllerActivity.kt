package org.openbot.openbotcontroller

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.openbot.openbotcontroller.customComponents.DualDriveSlider
import org.openbot.openbotcontroller.customComponents.IDriveValue
import org.openbot.openbotcontroller.utils.EventProcessor

import kotlin.Float as DrivePositionAsFloatBetweenMinusOneAndOne

class OpenbotControllerActivity : AppCompatActivity() {
    private val TAG = "OpenbotControllerActivity"

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

    class DriveValue (private val direction: String): IDriveValue {
        override operator fun invoke(x: DrivePositionAsFloatBetweenMinusOneAndOne): DrivePositionAsFloatBetweenMinusOneAndOne {
            val msg:String
            if (direction == "RIGHT") msg = "{rightDrive:${x}}" else msg = "{leftDrive:${x}}"
            NearbyConnection.sendMessage(msg)
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

        // showControlls()
        createAppEventsSubscription()

        findViewById<Button>(R.id.logs).setOnTouchListener(controlButtonListener)
        findViewById<Button>(R.id.indicator_right).setOnTouchListener(controlButtonListener)
        findViewById<Button>(R.id.indicator_left).setOnTouchListener(controlButtonListener)
        findViewById<Button>(R.id.indicator_stop).setOnTouchListener(controlButtonListener)
        findViewById<Button>(R.id.noise).setOnTouchListener(controlButtonListener)
        findViewById<Button>(R.id.drive_by_network).setOnTouchListener(controlButtonListener)
        findViewById<Button>(R.id.reconnect).setOnTouchListener(controlButtonListener)

        findViewById<DualDriveSlider>(R.id.leftDriveControl).setOnValueChangedListener(DriveValue ("LEFT"))
        findViewById<DualDriveSlider>(R.id.rightDriveControl).setOnValueChangedListener(DriveValue ("RIGHT"))

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        NearbyConnection.connect(this)
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
