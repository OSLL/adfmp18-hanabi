package ru.mit.spbau.hanabi.game

import java.util.*

interface Player {
    fun makeMove(gameState: GameState): Move
    fun sendMoveNotification(/*info: PlayerInfo, */ move: Move)
    fun notifyMyMove(move: Move)
    fun gameEnd(gameState: GameState)
}

class StupidAIPlayer : Player {
    override fun makeMove(gameState: GameState): Move {
        val cards = gameState.playersHands[gameState.currentPlayer].cards
        for (cardId in 0 until cards.size) {
            val card = cards[cardId]
            if (card.ownerKnowsCol && card.ownerKnowsVal && gameState.solitaire.canAdd(card)) {
                return SolitaireMove(cardId)
            }
        }

        if (gameState.cntHints == 0) {
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

    override fun sendMoveNotification(move: Move) {

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

    override fun sendMoveNotification(move: Move) {

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