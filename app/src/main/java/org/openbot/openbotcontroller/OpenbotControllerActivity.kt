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

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
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

        val controlsContainer: LinearLayout =
            findViewById<LinearLayout>(R.id.fullscreen_content_controls)

        controlsContainer.visibility = View.INVISIBLE;
        createAppEventsSubscription()

        findViewById<Button>(R.id.left).setOnTouchListener(controlButtonListener)
        findViewById<Button>(R.id.right).setOnTouchListener(controlButtonListener)
        findViewById<Button>(R.id.stop).setOnTouchListener(controlButtonListener)
        findViewById<Button>(R.id.go).setOnTouchListener(controlButtonListener)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        NearbyConnection.connect(this)
    }

    private fun createAppEventsSubscription(): Disposable =
        EventProcessor.connectionEventFlowable
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                Log.i(TAG, "Got ${it} event")

                when (it) {
                    EventProcessor.ProgressEvents.ConnectionSuccessful -> {
                        findViewById<LinearLayout>(R.id.splash_screen).visibility = View.INVISIBLE
                        findViewById<LinearLayout>(R.id.fullscreen_content_controls).visibility =
                            View.VISIBLE
                    }
                    EventProcessor.ProgressEvents.ConnectionFailed -> {
                    }
                    EventProcessor.ProgressEvents.ConnectionFailed -> {
                    }
                    EventProcessor.ProgressEvents.StartAdvertising -> {
                    }
                    EventProcessor.ProgressEvents.Disconnecting -> {
                    }
                    EventProcessor.ProgressEvents.StopAdvertising -> {
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