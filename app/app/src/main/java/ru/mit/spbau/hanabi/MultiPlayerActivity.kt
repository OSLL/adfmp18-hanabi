package ru.mit.spbau.hanabi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import ru.mit.spbau.hanabi.network.wifip2p.WiFiDirectBroadcastReceiver

class MultiPlayerActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MultiPlayerActivity"
        private const val p2pTabTag = "wifi_p2p"
        private const val internetTabTag = "internet"
    }

    private var mManager: WifiP2pManager? = null
    private var mChannel: WifiP2pManager.Channel? = null
    private var mReceiver: BroadcastReceiver? = null
    private var mIntentFilter: IntentFilter? = null
    private var tabHost: TabHost? = null
    private var mGamesList: ListView? = null
    private var mCurrentGameList: Array<GameInfo>? = null

    private val mP2PIntentFilter = IntentFilter()
    private var mCurTab = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multi_player)
        setupTabs()
        setupRefreshLayouts()
        setupCreateGameBtn()
        setupListViews()
        prepareP2PSettings()
        prepareInternetSettings()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "resuming MultiPlayerActivity")
        tabHost?.currentTab = mCurTab
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

    private fun setupRefreshLayouts() {
        val swipeRefreshLayoutP2P = findViewById<SwipeRefreshLayout>(R.id.swiperefresh_wifi_p2p)
        swipeRefreshLayoutP2P?.setOnRefreshListener {
            refreshGameList()
            swipeRefreshLayoutP2P.isRefreshing = false
        }

        val swipeRefreshLayoutInternet = findViewById<SwipeRefreshLayout>(R.id.swiperefresh_internet)
        swipeRefreshLayoutInternet?.setOnRefreshListener {
            refreshGameList()
            swipeRefreshLayoutInternet.isRefreshing = false
        }
    }

    private fun setupTabs() {
        tabHost = findViewById(R.id.multiplayer_tabhost)

        tabHost!!.setup()
        tabHost!!.setOnTabChangedListener(this::tabChangedListener)

        var tabSpec = tabHost!!.newTabSpec(p2pTabTag)
        tabSpec.setContent(R.id.wifi_p2p_tab_content)
        tabSpec.setIndicator("Wifi p2p")
        tabHost!!.addTab(tabSpec)

        tabSpec = tabHost!!.newTabSpec(internetTabTag)
        tabSpec.setContent(R.id.internet_tab_content)
        tabSpec.setIndicator("Internet")
        tabHost!!.addTab(tabSpec)

        tabHost!!.currentTab = mCurTab
    }

    private fun setupListViews() {
        mGamesList = findViewById(R.id.wifi_p2p_game_list)
        mGamesList?.isClickable = true
        mGamesList?.setOnItemClickListener { _, view: View , pos: Int, id: Long ->
            val gameInfo = mCurrentGameList?.get(pos)
            val builder = AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
            builder.setTitle("Join " + gameInfo?.name)
                    .setMessage("game description: " + gameInfo?.description)
                    .setPositiveButton(android.R.string.ok, { _, _ ->
                    })
                    .setNegativeButton(android.R.string.cancel, { _, _ ->
                    })
                    .show()
        }
    }

    private fun setupCreateGameBtn() {
        var createGameBtn = findViewById<Button>(R.id.create_new_game_p2p_btn)
        createGameBtn?.setOnClickListener { _ ->
            val intent = Intent(this, CreateGameActivity::class.java)
            this.startActivity(intent)
        }

        createGameBtn = findViewById<Button>(R.id.create_new_game_internet)
        createGameBtn?.setOnClickListener { _ ->
            val intent = Intent(this, CreateGameActivity::class.java)
            this.startActivity(intent)
        }
    }

    private fun tabChangedListener(tab: String) {
        fun useP2P() {
            mCurTab = 0
            mIntentFilter = mP2PIntentFilter
            mReceiver = WiFiDirectBroadcastReceiver
            mGamesList = findViewById(R.id.wifi_p2p_game_list)
            Log.d(TAG, "using P2P")
        }

        fun useInternet() {
            mCurTab = 1
            mGamesList = findViewById(R.id.internet_game_list)
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
        Log.d(TAG, "refreshing game list")
        val stubGameInfos = arrayOf(
            GameInfo("Game #1", "info", false),
            GameInfo("Game #2", "info", true),
            GameInfo("Game #3", "info", false),
            GameInfo("Game #4", "info", true),
            GameInfo("Game #5", "info", true),
            GameInfo("Game #6", "info", false),
            GameInfo("Game #7", "info", false),
            GameInfo("Game #8", "info", true),
            GameInfo("Game #9", "info", false)
        )

        mGamesList?.adapter = GameListAdapter(stubGameInfos)
    }

    private data class GameInfo(val name: String, val description: String, val isLocked: Boolean)

    private inner class GameListAdapter(private val gameInfoList: Array<GameInfo>):
            ArrayAdapter<GameInfo>(this@MultiPlayerActivity, R.layout.game_list_item, gameInfoList) {

        private val inflater = this@MultiPlayerActivity.layoutInflater

        init {
            this@MultiPlayerActivity.mCurrentGameList = gameInfoList
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val viewHolder: ViewHolder
            val gameItem: View?

            if (convertView == null) {
                gameItem = inflater.inflate(R.layout.game_list_item, parent, false)

                val gameNameTextView = gameItem.findViewById<TextView>(R.id.game_name)
                val gameInfoTextView = gameItem.findViewById<TextView>(R.id.game_info)
                val isGameLockedImageView = gameItem.findViewById<ImageView>(R.id.lock_image)
                viewHolder = ViewHolder(gameNameTextView, gameInfoTextView, isGameLockedImageView)
                gameItem.tag = viewHolder
            } else {
                viewHolder = convertView.tag as ViewHolder
                gameItem = convertView
            }

            if (!gameInfoList[position].isLocked) {
                viewHolder.isGameLockedImageView.visibility = View.GONE
            }

            viewHolder.gameNameTextView.text = gameInfoList[position].name
            viewHolder.gameInfoTextView.text = gameInfoList[position].description

            return gameItem!!
        }

        private inner class ViewHolder(val gameNameTextView: TextView,
                                       val gameInfoTextView: TextView,
                                       val isGameLockedImageView: ImageView)

    }

}
