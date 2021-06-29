package com.example.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.widget.Toast
import com.google.gson.Gson
import okhttp3.OkHttpClient
import java.util.*

class AutoStartReceiver : BroadcastReceiver() {

    val client = OkHttpClient()

    override fun onReceive(context: Context?, intent: Intent?) {


//        context?.startService(
//            Intent(context, AutoStartService::class.java).apply {
//                this.action = intent?.action
//
//            }
//        )
send(context,intent,client)


    }
}

fun send(context: Context?, intent: Intent?, client: OkHttpClient) {
    val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
        context?.registerReceiver(null, ifilter)
    }
    val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
    val isCharging: Boolean = status == BatteryManager.BATTERY_STATUS_CHARGING
            || status == BatteryManager.BATTERY_STATUS_FULL
    val isFull = status == BatteryManager.BATTERY_STATUS_FULL
// How are we charging?
    val chargePlug: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1
    val usbCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_USB
    val acCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_AC
    val wirelessCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_WIRELESS


    val action = intent?.action ?: "<null ?!>"
    val m = mapOf(
        ("usbCharge" to "$usbCharge"),
        ("acCharge" to "$acCharge"),
        ("wirelessCharge" to "$wirelessCharge"),
        ("isCharging" to "$isCharging"),
        ("isFull" to "$isFull"),
        ("data" to "${Date()}"),
        ("source" to "AutoStartReceiver"),
        ("source.action" to action)
    )
    val json = Gson().toJson(m)
    client.putJson(
        "https://testingforelevatorpower-default-rtdb.firebaseio.com/Receiver/main.json",
        json
    ) { r ->
    }
}