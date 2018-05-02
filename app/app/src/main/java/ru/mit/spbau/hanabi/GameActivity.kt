package ru.mit.spbau.hanabi

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TabHost

class GameActivity : AppCompatActivity() {

    companion object {
        private val solitaireTabTag = "solitaire"
        private val junkTabTag = "junk"
    }

    private var tabHost: TabHost? = null
    private var mCurTab = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        setupTabs()
    }

    private fun setupTabs() {
        tabHost = findViewById<TabHost>(R.id.multiplayer_tabhost) as TabHost

        tabHost!!.setup()
        tabHost!!.setOnTabChangedListener(this::tabChangedListener)

        var tabSpec = tabHost!!.newTabSpec(solitaireTabTag)
        tabSpec.setContent(R.id.solitaire_tab_content)
        tabSpec.setIndicator("solitaire")
        tabHost!!.addTab(tabSpec)

        tabSpec = tabHost!!.newTabSpec(junkTabTag)
        tabSpec.setContent(R.id.junk_tab_content)
        tabSpec.setIndicator("junk")
        tabHost!!.addTab(tabSpec)

        tabHost!!.currentTab = mCurTab
    }

    private fun tabChangedListener(tab: String) {
        // TODO
    }

}
