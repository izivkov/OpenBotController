package org.openbot.openbotcontroller

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.openbot.openbotcontroller.customComponents.*
import org.openbot.openbotcontroller.utils.EventProcessor
import org.openbot.openbotcontroller.utils.Utils
import kotlinx.android.synthetic.main.activity_fullscreen.*

@Suppress("DEPRECATION")
class OpenbotControllerActivity() : AppCompatActivity() {
    private val TAG = "OpenbotControllerActivity"
    private var buttonsVisible: Boolean = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_fullscreen)

        createAppEventsSubscription()

        leftDriveControl.setDirection(DualDriveSeekBar.LeftOrRight.LEFT)
        rightDriveControl.setDirection(DualDriveSeekBar.LeftOrRight.RIGHT)

        hideControls ()
        // showControls()

        hideSystemUI()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        main_screen.setupDoubleTap(::toggleButtons)
        NearbyConnection.connect(this)
    }

    private fun toggleButtons() {
        if (buttonsVisible) {
            hideButtons()
        } else {
            showButtons()
        }
    }

    private fun hideButtons() {
        // bot_setup_buttons.hide()
        bot_setup_buttons.visibility = INVISIBLE
        showSliders()
        buttonsVisible = false
    }

    private fun hideSliders() {
        // bot_setup_buttons.hide()
        drive_mode_controls.visibility = INVISIBLE
    }

    private fun showSliders() {
        // drive_mode_controls.show()
        drive_mode_controls.visibility = VISIBLE
    }

    private fun showButtons(milliseconds: Long) {
        showButtons()

        Handler().postDelayed({
            hideButtons()
        }, milliseconds)
    }

    private fun showButtons() {
        // bot_setup_buttons.show()
        bot_setup_buttons.visibility = VISIBLE

        hideSliders()
        buttonsVisible = true
    }

    private fun hideControls() {
        main_screen.hide()
        splash_screen.show()
    }

    private fun showControls() {
        splash_screen.hide()
        main_screen.show()

        showButtons(3000)
    }

    private fun createAppEventsSubscription(): Disposable =
        EventProcessor.connectionEventFlowable
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                Log.i(TAG, "Got ${it} event")

                when (it) {
                    EventProcessor.ProgressEvents.ConnectionSuccessful -> {
                        Utils.beep()
                        showControls()
                    }
                    EventProcessor.ProgressEvents.ConnectionStarted -> {
                    }
                    EventProcessor.ProgressEvents.ConnectionFailed -> {
                        hideControls()
                    }
                    EventProcessor.ProgressEvents.StartAdvertising -> {
                        hideControls()
                    }
                    EventProcessor.ProgressEvents.Disconnected -> {
                        hideControls()
                        NearbyConnection.connect(this)
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
