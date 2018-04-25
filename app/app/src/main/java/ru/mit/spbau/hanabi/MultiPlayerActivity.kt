package ru.mit.spbau.hanabi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TabHost
import ru.mit.spbau.hanabi.network.wifip2p.WiFiDirectBroadcastReceiver

class MultiPlayerActivity : AppCompatActivity() {

    companion object {
        val TAG = "MultiPlayerActivity"
        val p2pTabTag = "wifi_p2p"
        val internetTabTag = "internet"
    }

    private var mManager: WifiP2pManager? = null
    private var mChannel: WifiP2pManager.Channel? = null
    private var mReceiver: BroadcastReceiver? = null
    private var mIntentFilter: IntentFilter? = null
    private var tabHost: TabHost? = null

    private val mP2PIntentFilter = IntentFilter()

    private var mCurTab = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multi_player)
        setupTabs()
        prepareP2PSettings()
        prepareInternetSettings()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "resuming MultiPlayerActivity")
        registerReceiver(mReceiver, mIntentFilter)
    }

    /* unregister the broadcast receiver */
    override fun onPause() {
        super.onPause()
        Log.d(TAG, "pausing MultiPlayerActivity")
        unregisterReceiver(mReceiver)
    }

    private fun prepareP2PSettings() {
        mManager = this.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        mChannel = mManager!!.initialize(this, this.mainLooper, null)

        mP2PIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        mP2PIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        mP2PIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        mP2PIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)

        WiFiDirectBroadcastReceiver.setP2PStateChangeCallback(this::handleP2PStateChange)
    }

    private fun prepareInternetSettings() {
        // TODO
    }

    private fun setupTabs() {
        tabHost = findViewById<TabHost>(R.id.multiplayer_tabhost) as TabHost

        tabHost!!.setup()
        tabHost!!.setOnTabChangedListener(this::tabChangedListener)

        var tabSpec = tabHost!!.newTabSpec(p2pTabTag)
        tabSpec.setContent(R.id.wifi_p2p_tab)
        tabSpec.setIndicator("Wifi p2p")
        tabHost!!.addTab(tabSpec)

        tabSpec = tabHost!!.newTabSpec(internetTabTag)
        tabSpec.setContent(R.id.internet_tab)
        tabSpec.setIndicator("Internet")
        tabHost!!.addTab(tabSpec)

        tabHost!!.currentTab = mCurTab
    }

    private fun tabChangedListener(tab: String) {
        fun useP2P() {
            mCurTab = 0
            mIntentFilter = mP2PIntentFilter
            mReceiver = WiFiDirectBroadcastReceiver
            Log.d(TAG, "using P2P")
        }

        fun useInternet() {
            mCurTab = 1
            Log.d(TAG, "using Internet")
        }

        if (mReceiver != null) {
            unregisterReceiver(mReceiver)
        }
        when (tab) {
            p2pTabTag -> useP2P()
            internetTabTag -> useInternet()
        }
        registerReceiver(mReceiver, mIntentFilter)
    }

    private fun handleP2PStateChange(intent: Intent) {
        val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
        Log.d(TAG, "handling P2P state change: " + state)
        if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
            refreshGameList()
        } else {
            val builder = AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
            builder.setTitle("Wifi p2p is disabled")
                    .setMessage("Please, enable Wifi to be able to use Wifi p2p.")
                    .setPositiveButton(android.R.string.ok, { _, _ ->
                    })
                    .show()
        }
    }

    private fun refreshGameList() {

    }

}
