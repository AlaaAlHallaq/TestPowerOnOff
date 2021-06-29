package com.example.myapplication

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.*


class AutoStartService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val context = this.applicationContext

        return START_STICKY
        //return super.onStartCommand(intent, flags, startId)

    }

    lateinit var receiver: AutoStartReceiver
    override fun onCreate() {
        super.onCreate()
        receiver = AutoStartReceiver()
        val filter = IntentFilter()

        filter.addAction(android.content.Intent.ACTION_POWER_CONNECTED)
        filter.addAction(android.content.Intent.ACTION_POWER_DISCONNECTED)

        this.registerReceiver(receiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        this.unregisterReceiver(receiver)
    }


}

data class ResponseValue<T>(val value: T?, val error: IOException?)
typealias ResponseAction<T> = ((ResponseValue<T>) -> Unit)

fun OkHttpClient.putJson(url: String, json: String, onDone: ResponseAction<Response>) {
    val jsonType: MediaType = "application/json; charset=utf-8".toMediaType()
    val body: RequestBody = json.toRequestBody(jsonType)
    val request: Request = Request.Builder()
        .url(url)
        .put(body)
        .build()

    this.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            onDone(ResponseValue(null, e))
        }

        override fun onResponse(call: Call, response: Response) {
            onDone(ResponseValue(response, null))
        }
    })
}