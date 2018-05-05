package ru.mit.spbau.hanabi.game

import ru.mit.spbau.hanabi.GameActivity
import java.lang.Thread.sleep
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue

interface Player {
    fun makeMove(gameState: GameState): Move
    fun sendMoveNotification(/*info: PlayerInfo, */ gameState: GameState)
    fun notifyMyMove(move: Move)
    fun gameEnd(gameState: GameState)
}

class StupidAIPlayer : Player {
    override fun makeMove(gameState: GameState): Move {
        sleep(500)
        val cards = gameState.playersHands[gameState.currentPlayer].cards
        for (cardId in 0 until cards.size) {
            val card = cards[cardId]
            if (card.ownerKnowsCol && card.ownerKnowsVal && gameState.solitaire.canAdd(card)) {
                return SolitaireMove(gameState.currentPlayer, cardId)
            }
        }

        if (gameState.getCntHints() == 0) {
            return FoldMove(gameState.currentPlayer, 0)
        }

        for (otherPlayerId in 0 until gameState.playersCnt) {
            if (otherPlayerId != gameState.currentPlayer) {
                val otherPlayerCards = gameState.playersHands[otherPlayerId].cards
                for (cardId in 0 until otherPlayerCards.size) {
                    val card = otherPlayerCards[cardId]
                    if ((!card.ownerKnowsCol || !card.ownerKnowsVal) && gameState.solitaire.canAdd(card)) {
                        return if (!card.ownerKnowsCol) {
                            ColorHintMove(gameState.currentPlayer, otherPlayerId, card.color)
                        } else {
                            ValueHintMove(gameState.currentPlayer, otherPlayerId, card.value)
                        }
                    }
                }
            }
        }
        return FoldMove(gameState.currentPlayer, 0)
    }

    override fun sendMoveNotification(gameState: GameState) {
    }

    override fun notifyMyMove(move: Move) {
    }

    override fun gameEnd(gameState: GameState) {
    }
}

class NotSoStupidAIPlayer : Player {
    override fun makeMove(gameState: GameState): Move {
        sleep(500)
        val cards = gameState.playersHands[gameState.currentPlayer].cards
        for (cardId in 0 until cards.size) {
            val card = cards[cardId]
            if ((card.ownerKnowsCol && card.ownerKnowsVal && gameState.solitaire.canAdd(card)) ||
                    (wasHintToOnlyThisCard(card, cards) && isLastHintAboutCard(card, gameState))) {
                return SolitaireMove(gameState.currentPlayer, cardId)
            }
        }

        if (gameState.getCntHints() == 0) {
            return FoldMove(gameState.currentPlayer, findLeastUsefulCard(gameState))
        }

        for (i in 1 until gameState.playersCnt) {
            val otherPlayerId = (gameState.currentPlayer + i) % gameState.playersCnt
            val otherPlayerCards = gameState.playersHands[otherPlayerId].cards
            for (cardId in 0 until otherPlayerCards.size) {
                val card = otherPlayerCards[cardId]
                if ((!card.ownerKnowsCol || !card.ownerKnowsVal) && gameState.solitaire.canAdd(card)) {
                    return if (!card.ownerKnowsCol &&
                            (card.ownerKnowsVal || numCardsWithCol(card.color, otherPlayerCards) == 1)) {
                        ColorHintMove(gameState.currentPlayer, otherPlayerId, card.color)
                    } else {
                        ValueHintMove(gameState.currentPlayer, otherPlayerId, card.value)
                    }
                }
            }
        }
        return FoldMove(gameState.currentPlayer, findLeastUsefulCard(gameState))
    }

    private fun numCardsWithCol(col: Int, cards: List<Card>): Int {
        var res = 0
        for (card in cards) {
            if (card.color == col) {
                res++
            }
        }
        return res
    }

    private fun isLastHintAboutCard(card: Card, gameState: GameState): Boolean {
        val movesHistory = gameState.movesHistory
        for (i in movesHistory.size - 1 downTo 0) {
            val move = movesHistory[i]
            if (move is HintMove && move.playerId == gameState.currentPlayer) {
                return when (move) {
                    is ValueHintMove -> {
                        move.value == card.value
                    }
                    is ColorHintMove -> {
                        move.color == card.color
                    }
                    else -> false
                }
            }
            if (move !is HintMove && move.fromPlayerId == gameState.currentPlayer) {
                return false
            }
        }
        return false
    }

    private fun wasHintToOnlyThisCard(card: Card, cards: List<Card>): Boolean {
        if (card.ownerKnowsCol && card.ownerKnowsVal) {
            return false
        }
        var cntColHints = 0
        var cntValHints = 0
        for (otherCard in cards) {
            if (card.ownerKnowsVal && otherCard.ownerKnowsVal && card.value == otherCard.value) {
                cntValHints++
            }
            if (card.ownerKnowsCol && otherCard.ownerKnowsCol && card.color == otherCard.color) {
                cntColHints++
            }
        }
        return cntValHints == 1 || cntColHints == 1
    }

    private fun findLeastUsefulCard(gameState: GameState): Int {
        val myCards = gameState.playersHands[gameState.currentPlayer]
        for (i in 0 until myCards.cards.size) {
            if (isUselessCard(myCards.cards[i], gameState)) {
                return i
            }
        }
        for (i in 0 until myCards.cards.size) {
            if (!myCards.cards[i].ownerKnowsVal && !myCards.cards[i].ownerKnowsCol) {
                return i
            }
        }
        for (i in 0 until myCards.cards.size) {
            if (!myCards.cards[i].ownerKnowsVal || !myCards.cards[i].ownerKnowsCol) {
                return i
            }
        }
        return 0
    }

    private fun isUselessCard(card: Card, gameState: GameState): Boolean {
        val maxValues = gameState.solitaire.maxValue
        if (card.ownerKnowsVal && card.ownerKnowsCol) {
            return maxValues[card.color - 1] >= card.value
        } else {
            if (card.ownerKnowsVal) {
                for (maxVal in maxValues) {
                    if (maxVal < card.value) {
                        return false
                    }
                }
                return true
            }
            if (card.ownerKnowsCol) {
                return maxValues[card.color - 1] == 5
            }
        }
        return false
    }


    override fun sendMoveNotification(gameState: GameState) {
    }

    override fun notifyMyMove(move: Move) {
    }

    override fun gameEnd(gameState: GameState) {
    }

}

class UIPlayer(private val gameView: GameActivity) : Player {
    private val queue: BlockingQueue<Move> = ArrayBlockingQueue<Move>(1)

    override fun makeMove(gameState: GameState): Move {
        return queue.take()
    }

    override fun sendMoveNotification(gameState: GameState) {
        gameView.runOnUiThread {
            gameView.redraw(gameState)
        }
    }

    override fun notifyMyMove(move: Move) {
        if (queue.isEmpty()) {
            queue.put(move)
        }
    }

    override fun gameEnd(gameState: GameState) {
    }
}

class ConsolePlayer : Player {
    private val scanner: Scanner = Scanner(System.`in`)

    override fun makeMove(gameState: GameState): Move {
        var t: String = scanner.nextLine()
        while (t == "") {
            t = scanner.nextLine()
        }
        return when (t) {
            "col" -> {
                val playerId = scanner.nextInt()
                ColorHintMove(gameState.currentPlayer, playerId, scanner.nextInt())
            }
            "val" -> {
                val playerId = scanner.nextInt()
                ValueHintMove(gameState.currentPlayer, playerId, scanner.nextInt())
            }
            "sol" -> {
                SolitaireMove(gameState.currentPlayer, scanner.nextInt())
            }
            "fold" -> {
                FoldMove(gameState.currentPlayer, scanner.nextInt())
            }
            else -> {
                FoldMove(gameState.currentPlayer, 0)
            }
        }
    }

    override fun sendMoveNotification(gameState: GameState) {

    }

    override fun notifyMyMove(move: Move) {

    }

    override fun gameEnd(gameState: GameState) {
        if (gameState.getState() == GameState.State.LOOSE) {
            println("You loose :(")
        } else {
            println("You win :)")
            println("Score = " + gameState.getScore())
        }
    }

}