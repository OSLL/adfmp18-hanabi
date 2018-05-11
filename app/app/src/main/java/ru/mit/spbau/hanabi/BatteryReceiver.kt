package ru.mit.spbau.hanabi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log

object BatteryReceiver : BroadcastReceiver() {

    private const val BATTERY_TAG = "Battery level"
    private var isRegistered = false

    override fun onReceive(context: Context, intent: Intent) {
        val rawlevel = intent.getIntExtra("level", -1)
        val scale = intent.getIntExtra("scale", -1)
        var level = -1
        if (rawlevel >= 0 && scale > 0) {
            level = rawlevel * 100 / scale
        }
        Log.d(BATTERY_TAG, "Battery Level in % is:: $level%, rawlevel is:: $rawlevel")
    }

    fun register(context: Context) {
        if (!isRegistered) {
            val batteryLevelFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            context.registerReceiver(this, batteryLevelFilter)
            Log.d(BATTERY_TAG, "Registered battery receiver")
            isRegistered = true
        }
    }

    fun unregister(context: Context) {
        if (isRegistered) {
            context.unregisterReceiver(this)
            Log.d(BATTERY_TAG, "unregistered battery receiver")
            isRegistered = false
        }
    }

}