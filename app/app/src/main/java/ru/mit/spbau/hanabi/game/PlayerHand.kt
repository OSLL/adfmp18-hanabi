package ru.mit.spbau.hanabi.game

class PlayerHand {
    val cards: MutableList<Card> = ArrayList()

    fun getCard(cardId: Int): Card {
        return cards[cardId]
    }

    fun hintColor(color: Int): Boolean {
        var informed = false
        for (card in cards) {
            if (card.color == color) {
                informed = true
                card.knowColor()
            }
        }
        return informed
    }

    fun hintValue(value: Int): Boolean {
        var informed = false
        for (card in cards) {
            if (card.value == value) {
                informed = true
                card.knowValue()
            }
        }
        return informed
    }

    fun addToSolitaire(cardId: Int, solitaire: Solitaire): Boolean {
        if (cardId < 0 || cardId > cards.size - 1) {
            throw GameException("Incorrect card id to solitaire")
        }

        val cardToSolitaire = cards[cardId]
        return if (solitaire.tryAdd(cardToSolitaire)) {
            cards.remove(cardToSolitaire)
            true
        } else {
            false
        }
    }

    fun foldCard(cardId: Int, junk: MutableList<Card>) {
        if (cardId < 0 || cardId > cards.size - 1) {
            throw GameException("Incorrect card id to fold")
        }
        val cardToRemove = cards[cardId]
        junk.add(cardToRemove)
        cards.remove(cardToRemove)
    }

    fun getCartFromDeck(deck: MutableList<Card>) {
        if (!deck.isEmpty()) {
            cards.add(deck.last())
            deck.removeAt(deck.size - 1)
        }
    }
}
