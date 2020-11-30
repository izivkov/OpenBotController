package org.openbot.openbotcontroller.utils

import android.util.Log
import io.reactivex.Flowable
import io.reactivex.processors.PublishProcessor

object EventProcessor {
    private val eventProcessor: PublishProcessor<ProgressEvents> =
        PublishProcessor.create()

    val connectionEventFlowable = eventProcessor as Flowable<ProgressEvents>

    fun onNext(e: ProgressEvents) {
        if (eventProcessor.hasSubscribers()) {
            return eventProcessor.onNext(e)
        } else {
            Log.d("EventProcessor:onNext", "----------- No subscribers")
        }
    }

    open class ProgressEvents(var payload: String = "") {

        object Init : ProgressEvents()

        object ConnectionSuccessful:  ProgressEvents()
        object ConnectionFailed:  ProgressEvents()
        object StartAdvertising:  ProgressEvents()
        object Disconnecting:  ProgressEvents()
        object StopAdvertising: ProgressEvents()
        object AdvertisingFailed: ProgressEvents()
    }
}