package ru.mit.spbau.hanabi

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import ru.mit.spbau.hanabi.game.*
import android.content.Intent


class GameActivity : AppCompatActivity(), GameView {
    companion object {
        private const val solitaireTabTag = "solitaire"
        private const val junkTabTag = "junk"
    }

    private var tabHost: TabHost? = null
    private var mCurTab = 0
    private var mHandsView: ListView? = null
    private var mLifeView: TextView? = null
    private var mHintsView: TextView? = null
    private var mSolitaireView: ListView? = null
    private var mJunkView: ListView? = null
    private var mUIPlayer: UIPlayer? = null
    private var mGameState: GameState? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        setupTabs()
        setupGameInfoView()
        setupHandsView()

        setupGame()
    }

    private fun setupGame() {
        mUIPlayer = UIPlayer(this)
        val players: MutableList<Player> = mutableListOf(mUIPlayer!!)
        val playersCnt = intent.getIntExtra("PLAYERS_NUMBER", 2)
        for (i in 2..playersCnt) {
            players.add(NotSoStupidAIPlayer())
        }

        val gameThread = Thread(Runnable({
            val game = Game(players)
            game.run()
        }))
        gameThread.start()
    }

    override fun redraw(gameState: GameState) {
        mGameState = gameState
        gameState.printState()
        updateGameInfoView(gameState.getCntLife(), gameState.getCntHints())
        updateHandsView(gameState.playersHands)
        updateSolitaire(gameState.solitaire)
        updateJunk(gameState.junk)

        val state = gameState.getState()
        when (state) {
            GameState.State.LOOSE -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("End game")
                builder.setMessage("You loose")
                builder.setPositiveButton("OK") { _, _ ->
                    val intent = Intent(this, SinglePlayerActivity::class.java)
                    this.startActivity(intent)
                }

                builder.setCancelable(true)
                builder.setOnCancelListener {
                    val intent = Intent(this, SinglePlayerActivity::class.java)
                    this.startActivity(intent)
                }
                builder.create().show()
            }
            GameState.State.WIN -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("You win")
                builder.setMessage("Your score is ${gameState.getScore()}")
                builder.setPositiveButton("OK") { _, _ ->
                    val intent = Intent(this, SinglePlayerActivity::class.java)
                    this.startActivity(intent)
                }
                builder.setCancelable(true)
                builder.setOnCancelListener {
                    val intent = Intent(this, SinglePlayerActivity::class.java)
                    this.startActivity(intent)
                }
                builder.create().show()
            }
            else -> {
            }
        }
    }

    private fun updateSolitaire(solitaire: Solitaire) {
        val hand = PlayerHand()
        for (color in 1..5) {
            val card = Card(solitaire.maxValue[color - 1], color)
            hand.cards.add(card)
        }
        val hands: List<PlayerHand> = listOf(hand)

        mSolitaireView?.adapter = HandsListAdapter(hands, -1)
    }

    private fun updateJunk(junk: List<Card>) {

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

        mSolitaireView = findViewById(R.id.solitaire_tab_content)
        mJunkView = findViewById(R.id.junk_tab_content)
    }

    private fun setupGameInfoView() {
        mLifeView = findViewById(R.id.life_view)
        mHintsView = findViewById(R.id.hints_view)
    }

    private fun updateGameInfoView(lifeCnt: Int, hintsCnt: Int) {
        mLifeView!!.text = "$lifeCnt life"
        mHintsView!!.text = "$hintsCnt hints"
    }

    private fun updateHandsView(playersHands: List<PlayerHand>) {
        mHandsView?.adapter = HandsListAdapter(playersHands, 0)
    }

    private fun setupHandsView() {
        mHandsView = findViewById(R.id.hands_view)
    }

    private fun tabChangedListener(tab: String) {
        // TODO
    }

    private inner class HandsListAdapter(private val handInfosList: List<PlayerHand>, val currentPlayer: Int) :
            ArrayAdapter<PlayerHand>(this@GameActivity, R.layout.player_hand_item_view, handInfosList) {

        private val inflater = this@GameActivity.layoutInflater

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val handItem: View? = convertView
                    ?: inflater.inflate(R.layout.player_hand_item_view, parent, false)

            // TODO performance, need view holder
            (handItem as ViewGroup).removeAllViews()
            for (cardPos in 0 until handInfosList[position].cards.size) {
                val card = handInfosList[position].cards[cardPos]
                val cardView = inflater.inflate(R.layout.game_card, handItem, false) as ViewGroup
                val cardTextView = cardView.getChildAt(0) as TextView
                if (position != currentPlayer) {
                    cardTextView.text = card.value.toString()
                    setColorCard(cardTextView, card.color)
                    setOnClickListenerToFriendCard(cardView, position, card)
                } else {
                    if (card.ownerKnowsVal) {
                        cardTextView.text = card.value.toString()
                    } else {
                        cardTextView.text = "?"
                    }
                    if (card.ownerKnowsCol) {
                        setColorCard(cardTextView, card.color)
                    } else {
                        cardTextView.setTextColor(ContextCompat.getColor(this@GameActivity, R.color.cardUnknown))
                    }

                    setOnClickListenerToMyCard(cardView, cardPos)
                }
                (cardView.layoutParams as ViewGroup.MarginLayoutParams).marginEnd = 30
                handItem.addView(cardView)
            }

            return handItem
        }

        fun setOnClickListenerToFriendCard(cardView: View, playerId: Int, card: Card) {
            if (mGameState!!.currentPlayer == 0 && mGameState!!.getCntHints() > 0) {
                cardView.setOnClickListener {
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Move")
                    builder.setMessage("Choose move")
                    builder.setPositiveButton("hint color") { _, _ ->
                        System.err.println("Hint color ${card.color}")
                        mUIPlayer!!.notifyMyMove(ColorHintMove(mGameState!!.currentPlayer, playerId, card.color))
                    }
                    builder.setNegativeButton("hint value") { _, _ ->
                        System.err.println("Hint value ${card.value}")
                        mUIPlayer!!.notifyMyMove(ValueHintMove(mGameState!!.currentPlayer, playerId, card.value))
                    }
                    builder.setCancelable(true)
                    builder.create().show()
                }
            }
        }

        fun setOnClickListenerToMyCard(cardView: View, cardPos: Int) {
            if (mGameState!!.currentPlayer == 0) {
                cardView.setOnClickListener {
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Move")
                    builder.setMessage("Choose move")
                    builder.setPositiveButton("fold") { _, _ ->
                        mUIPlayer!!.notifyMyMove(FoldMove(mGameState!!.currentPlayer, cardPos))
                    }
                    builder.setNegativeButton("solitaire") { _, _ ->
                        mUIPlayer!!.notifyMyMove(SolitaireMove(mGameState!!.currentPlayer, cardPos))
                    }
                    builder.setCancelable(true)
                    builder.create().show()
                }
            }
        }

        fun setColorCard(cardTextView: TextView, color: Int) {
            when (color) {
                1 -> cardTextView.setTextColor(ContextCompat.getColor(this@GameActivity, R.color.cardWhite))
                2 -> cardTextView.setTextColor(ContextCompat.getColor(this@GameActivity, R.color.cardYellow))
                3 -> cardTextView.setTextColor(ContextCompat.getColor(this@GameActivity, R.color.cardBlue))
                4 -> cardTextView.setTextColor(ContextCompat.getColor(this@GameActivity, R.color.cardGreen))
                5 -> cardTextView.setTextColor(ContextCompat.getColor(this@GameActivity, R.color.cardRed))
                else -> throw GameException("Draw unknown card")
            }
        }

    }

}
