package org.openbot.openbotcontroller

import android.util.Log
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import org.json.JSONObject
import java.nio.charset.StandardCharsets

/*
This class listens for status data from the Bot and emits events.
These events are received by various custom components which update their UI accordingly.
For example, a right indicator will start blinking if the status on the bot is set.
 */
object BotDataListener {
    fun init() {
        NearbyConnection.setPayloadCallback(::payloadCallback)
    }

    // Callbacks for receiving payloads. The NearbyConnection will call this upon receiving new data.
    private val payloadCallback: PayloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(
            endpointId: String,
            payload: Payload
        ) {
            val dataJson = JSONObject(
                String(
                    payload.asBytes()!!,
                    StandardCharsets.UTF_8
                )
            )
            val statusValues = dataJson.getJSONObject("status")

            for (key in statusValues.keys()) {
                val value: String = statusValues.getString(key)
                Log.i(null, key)

                // Send an event on a particular subject.
                // The custom controls are listening on their subject.
                StatusEventBus.emitEvent(value, key)
            }
        }

        override fun onPayloadTransferUpdate(
            endpointId: String,
            update: PayloadTransferUpdate
        ) {
        }
    }
}