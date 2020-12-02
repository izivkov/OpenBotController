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
import org.openbot.openbotcontroller.utils.EventProcessor

class OpenbotControllerActivity : AppCompatActivity() {
    private val TAG = "OpenbotControllerActivity"

    private val controlButtonListener = View.OnTouchListener { view, motionEvent ->
        val any = when (motionEvent.action) {
            MotionEvent.ACTION_UP -> {
                when (view.id) {
                    R.id.left -> {
                        Log.i(TAG, "Left")
                        NearbyConnection.sendMessage("LEFT")
                    }
                    R.id.right -> {
                        Log.i(TAG, "Right")
                        NearbyConnection.sendMessage("RIGHT")
                    }
                    R.id.stop -> {
                        NearbyConnection.sendMessage("STOP")
                        Log.i(TAG, "Stop")
                    }
                    R.id.go -> {
                        NearbyConnection.sendMessage("GO")
                        Log.i(TAG, "Go")
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_fullscreen)

        val controlsContainer: RelativeLayout =
            findViewById<RelativeLayout>(R.id.fullscreen_content_controls)

        controlsContainer.visibility = View.GONE;
        createAppEventsSubscription()

        findViewById<Button>(R.id.left).setOnTouchListener(controlButtonListener)
        findViewById<Button>(R.id.right).setOnTouchListener(controlButtonListener)
        findViewById<Button>(R.id.stop).setOnTouchListener(controlButtonListener)
        findViewById<Button>(R.id.go).setOnTouchListener(controlButtonListener)

        findViewById<Button>(R.id.reconnect).setOnTouchListener(controlButtonListener)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        NearbyConnection.connect(this)
    }

    private fun hideControlls () {
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
                        hideControlls()
                    }
                    EventProcessor.ProgressEvents.StartAdvertising -> {
                        hideControlls()
                    }
                    EventProcessor.ProgressEvents.Disconnecting -> {
                    }
                    EventProcessor.ProgressEvents.StopAdvertising -> {
                    }
                    EventProcessor.ProgressEvents.AdvertisingFailed -> {
                        hideControlls()
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