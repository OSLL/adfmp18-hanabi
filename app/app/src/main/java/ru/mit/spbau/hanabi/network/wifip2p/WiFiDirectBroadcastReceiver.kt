package ru.mit.spbau.hanabi.network.wifip2p

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pManager


/**
 * A BroadcastReceiver that notifies of important Wi-Fi p2p events.
 */
typealias Callback<T> = (T) -> Unit

object WiFiDirectBroadcastReceiver : BroadcastReceiver() {
    private var handleP2PStateChangeCallback: Callback<Intent>? = null

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION == action) {
            handleP2PStateChangeCallback?.invoke(intent)
        }
    }

    fun setP2PStateChangeCallback(callback: Callback<Intent>) {
        handleP2PStateChangeCallback = callback
    }

}

