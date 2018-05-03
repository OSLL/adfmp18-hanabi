package ru.mit.spbau.hanabi

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TabHost
import android.widget.TextView
import ru.mit.spbau.hanabi.game.*



class GameActivity : AppCompatActivity(), GameView {
    companion object {
        private val solitaireTabTag = "solitaire"
        private val junkTabTag = "junk"
    }

    private var tabHost: TabHost? = null
    private var mCurTab = 0
    private var mHandsView: ListView? = null
    private var mLifeView: TextView? = null
    private var mHintsView: TextView? = null
    private var mUIPlayer: UIPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        setupTabs()
        setupGameInfoView()
        setupHandsView()

        mUIPlayer = UIPlayer(this)
        val gameThread = Thread(Runnable({
            val game = Game(listOf(mUIPlayer!!, StupidAIPlayer(), StupidAIPlayer()))
            game.run()
        }))
        gameThread.start()
    }

    override fun redraw(gameState: GameState) {
        gameState.printState()
        updateGameInfoView(gameState.getCntLife(), gameState.getCntHints())
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

    private fun setupGameInfoView() {
        // TODO
        mLifeView = findViewById(R.id.life_view)
        mHintsView = findViewById(R.id.hints_view)

        updateGameInfoView(3, 8)
    }

    private fun updateGameInfoView(lifeCnt: Int, hintsCnt: Int) {
        mLifeView!!.setText("$lifeCnt life")
        mHintsView!!.setText("$hintsCnt hints")

    }

    private fun setupHandsView() {
        mHandsView = findViewById(R.id.hands_view)

        val firstHandStub = arrayOf(CardInfo(1, 4), CardInfo(2, 3),
                CardInfo(3, 3), CardInfo(4, 4))
        val secondHandStub = arrayOf(CardInfo(5, 5), CardInfo(1, 5),
                CardInfo(2, 2), CardInfo(3, 3))
        val thirdHandStub = arrayOf(CardInfo(3, 2), CardInfo(4, 4),
                CardInfo(1, 1), CardInfo(2, 2))
        val fourthHandStub = arrayOf(CardInfo(2, 2), CardInfo(5, 1),
                CardInfo(4, 5), CardInfo(3, 1))
        val stubHandsInfo = arrayOf(
                HandInfo(firstHandStub),
                HandInfo(secondHandStub),
                HandInfo(thirdHandStub),
                HandInfo(fourthHandStub)
        )

        mHandsView?.adapter = HandsListAdapter(stubHandsInfo)
    }

    private fun tabChangedListener(tab: String) {
        // TODO
    }

    private data class CardInfo(val value: Int, val color: Int)
    private data class HandInfo(val cards: Array<CardInfo>) // we don't need neither equals, nor hashcode

    private inner class HandsListAdapter(private val handInfosList: Array<HandInfo>) :
            ArrayAdapter<HandInfo>(this@GameActivity, R.layout.player_hand_item_view, handInfosList) {

        private val inflater = this@GameActivity.layoutInflater

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val handItem: View?

            if (convertView == null) {
                handItem = inflater.inflate(R.layout.player_hand_item_view, parent, false)
            } else {
                handItem = convertView
            }

            // TODO performance, need view holder
            (handItem as ViewGroup).removeAllViews()
            for (card in handInfosList[position].cards) {
                val cardView = inflater.inflate(R.layout.game_card, handItem, false) as ViewGroup
                val cardTextView = cardView.getChildAt(0) as TextView
                cardTextView.text = card.value.toString()
                when (card.color) {
                    0 -> cardTextView.setTextColor(ContextCompat.getColor(this@GameActivity, R.color.cardWhite))
                    1 -> cardTextView.setTextColor(ContextCompat.getColor(this@GameActivity, R.color.cardYellow))
                    2 -> cardTextView.setTextColor(ContextCompat.getColor(this@GameActivity, R.color.cardBlue))
                    3 -> cardTextView.setTextColor(ContextCompat.getColor(this@GameActivity, R.color.cardGreen))
                    4 -> cardTextView.setTextColor(ContextCompat.getColor(this@GameActivity, R.color.cardRed))
                }
                (cardView.layoutParams as ViewGroup.MarginLayoutParams).marginEnd = 30
                handItem.addView(cardView)
            }

            return handItem
        }

    }

}
