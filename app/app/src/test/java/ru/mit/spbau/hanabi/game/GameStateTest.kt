package ru.mit.spbau.hanabi.game

import org.junit.Assert.*
import org.junit.Test

class GameStateTest {
    @Test
    fun applyFoldMoveTest() {
        val gameState = GameState(3)

        val oldHand0 = gameState.playersHands[0].cards.toMutableList()
        val oldHand1 = gameState.playersHands[1].cards.toMutableList()
        val oldHand2 = gameState.playersHands[2].cards.toMutableList()

        gameState.applyMove(FoldMove(0, 0))

        assertEquals(oldHand1, gameState.playersHands[1].cards)
        assertEquals(oldHand2, gameState.playersHands[2].cards)

        val newHand0 = gameState.playersHands[0].cards

        for (i in 1 until oldHand0.size) {
            assertEquals(oldHand0[i], newHand0[i - 1])
        }
    }

    @Test
    fun applyHintMoveTest() {
        val gameState = GameState(3)

        val valueToHint = gameState.playersHands[1].cards[0].value

        gameState.applyMove(ValueHintMove(0, 1, valueToHint))

        val hand1 = gameState.playersHands[1].cards
        for (card in hand1) {
            assertSame(card.value == valueToHint, card.ownerKnowsVal)
        }
    }

    @Test
    fun foldAlwaysTest() {
        val gameState = GameState(3)

        while (gameState.getState() == GameState.State.IN_PROGRESS) {
            gameState.applyMove(FoldMove(gameState.currentPlayer, 0))
        }

        assertSame(GameState.State.WIN, gameState.getState())
        assertSame(0, gameState.getScore())
    }

    @Test
    fun looseTest() {
        val gameState = GameState(3)

        while (gameState.getState() == GameState.State.IN_PROGRESS) {
            gameState.applyMove(SolitaireMove(gameState.currentPlayer, 0))
        }

        assertSame(GameState.State.LOOSE, gameState.getState())
    }
}
