package ru.mit.spbau.hanabi.game

import java.util.*

const val CARDS_PER_PLAYER = 5
const val MAX_CNT_HINTS = 8
const val MAX_CNT_LIFE = 3
const val ALLOW_EMPTY_SET_HINTS = false
val NUMBER_OF_CARDS_WITH_VALUE = intArrayOf(0, 3, 2, 2, 2, 1)

class GameState(val playersCnt: Int) {
    enum class State { IN_PROGRESS, LOOSE, WIN }

    private var state: State = State.IN_PROGRESS
    val deck: MutableList<Card> = generateRandomDeck()
    val solitaire: Solitaire = Solitaire()
    val junk: MutableList<Card> = ArrayList()
    val playersHands: List<PlayerHand> = dealCards(deck, playersCnt)
    val movesHistory: MutableList<Move> = ArrayList()
    var cntHints: Int = MAX_CNT_HINTS
    var cntLife: Int = MAX_CNT_LIFE
    var currentPlayer = 0
    var lastRoundMoves = playersCnt + 1

    fun getState(): State {
        return state
    }

    fun applyMove(move: Move) {
        when (move) {
            is HintMove -> {
                if (move.playerId == currentPlayer) {
                    throw GameException("Hint to yourself")
                }
                if (cntHints == 0) {
                    throw GameException("Use hint when there are no hints")
                }

                val informed =
                        when (move) {
                            is ColorHintMove -> playersHands[move.playerId].hintColor(move.color)
                            is ValueHintMove -> playersHands[move.playerId].hintValue(move.value)
                            else -> false
                        }
                if (!informed && !ALLOW_EMPTY_SET_HINTS) {
                    throw GameException("Hint to empty set of cards")
                }

                cntHints--
            }

            is SolitaireMove -> {
                val card = playersHands[currentPlayer].getCard(move.cardId)
                if (!playersHands[currentPlayer].addToSolitaire(move.cardId, solitaire)) {
                    playersHands[currentPlayer].foldCard(move.cardId, junk)
                    cntLife--
                    if (cntLife == 0) {
                        state = State.LOOSE
                    }
                } else {
                    if (card.value == 5) {
                        cntHints = minOf(cntHints + 1, 8)
                    }
                }
                playersHands[currentPlayer].getCartFromDeck(deck)
            }

            is FoldMove -> {
                playersHands[currentPlayer].foldCard(move.cardId, junk)
                playersHands[currentPlayer].getCartFromDeck(deck)
                cntHints = minOf(cntHints + 1, 8)
            }
        }

        if (deck.isEmpty()) {
            lastRoundMoves--
            if (lastRoundMoves == 0) {
                state = State.WIN
            }
        }
        currentPlayer = (currentPlayer + 1) % playersCnt
        movesHistory.add(move)
    }

    fun getScore(): Int {
        return solitaire.getScore()
    }

    fun printState() {
        System.out.println("Hints cnt = " + cntHints)
        System.out.println("Life cnt = " + cntLife)
        System.out.println("Current player = " + currentPlayer)
        System.out.println("Solitaire:")
        for (value in solitaire.maxValue) {
            System.out.print("" + value + " ")
        }
        System.out.println()
        if (!movesHistory.isEmpty()) {
            System.out.println("Last move:")
            val move = movesHistory.last()
            when (move) {
                is ColorHintMove -> System.out.println("Color hint " + move.color + " to player " + move.playerId)
                is ValueHintMove -> System.out.println("Value hint " + move.value + " to player " + move.playerId)
                is SolitaireMove -> System.out.println("Solitaire " + move.cardId)
                is FoldMove -> System.out.println("Fold " + move.cardId)
            }
        }

        for (playerHand in playersHands) {
            System.out.println("Player:")
            for (card in playerHand.cards) {
                System.out.println("Card " + card.color + " " + card.value + " " +
                        card.ownerKnowsCol + " " + card.ownerKnowsVal)
            }
        }
        System.out.println()
    }
}

fun generateRandomDeck(): MutableList<Card> {
    val res = ArrayList<Card>()
    for (value in 1..5) {
        for (color in 1..5) {
            for (i in 1..NUMBER_OF_CARDS_WITH_VALUE[value]) {
                res.add(Card(value, color))
            }
        }
    }

    Collections.shuffle(res)

    return res
}

fun dealCards(deck: MutableList<Card>, playersCnt: Int): MutableList<PlayerHand> {
    val res = ArrayList<PlayerHand>()
    for (i in 0 until playersCnt) {
        val cardsForPlayer = PlayerHand()
        for (j in 0 until CARDS_PER_PLAYER) {
            cardsForPlayer.getCartFromDeck(deck)
        }
        res.add(cardsForPlayer)
    }

    return res
}