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
        sleep(2000)
        val cards = gameState.playersHands[gameState.currentPlayer].cards
        for (cardId in 0 until cards.size) {
            val card = cards[cardId]
            if (card.ownerKnowsCol && card.ownerKnowsVal && gameState.solitaire.canAdd(card)) {
                return SolitaireMove(cardId)
            }
        }

        if (gameState.getCntHints() == 0) {
            return FoldMove(0)
        }

        for (otherPlayerId in 0 until gameState.playersCnt) {
            if (otherPlayerId != gameState.currentPlayer) {
                val otherPlayerCards = gameState.playersHands[otherPlayerId].cards
                for (cardId in 0 until otherPlayerCards.size) {
                    val card = otherPlayerCards[cardId]
                    if ((!card.ownerKnowsCol || !card.ownerKnowsVal) && gameState.solitaire.canAdd(card)) {
                        return if (!card.ownerKnowsCol) {
                            ColorHintMove(otherPlayerId, card.color)
                        } else {
                            ValueHintMove(otherPlayerId, card.value)
                        }
                    }
                }
            }
        }
        return FoldMove(0)
    }

    override fun sendMoveNotification(gameState: GameState) {

    }

    override fun notifyMyMove(move: Move) {

    }

    override fun gameEnd(gameState: GameState) {
        if (gameState.getState() == GameState.State.LOOSE) {
            println("I loose :(")
        } else {
            println("I win :)")
            println("Score = " + gameState.getScore())
        }
    }
}

class UIPlayer(private val gameView: GameActivity) : Player {
    private val queue: BlockingQueue<Move> = ArrayBlockingQueue<Move>(1)

    override fun makeMove(gameState: GameState): Move {
        sleep(2000)
        return FoldMove(0)

//        while (queue.isEmpty()) {
//        }
//        return queue.poll()
    }

    override fun sendMoveNotification(gameState: GameState) {
        gameView.runOnUiThread {
            gameView.redraw(gameState)
        }
    }

    override fun notifyMyMove(move: Move) {
        queue.put(move)
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
                ColorHintMove(playerId, scanner.nextInt())
            }
            "val" -> {
                val playerId = scanner.nextInt()
                ValueHintMove(playerId, scanner.nextInt())
            }
            "sol" -> {
                SolitaireMove(scanner.nextInt())
            }
            "fold" -> {
                FoldMove(scanner.nextInt())
            }
            else -> {
                FoldMove(0)
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