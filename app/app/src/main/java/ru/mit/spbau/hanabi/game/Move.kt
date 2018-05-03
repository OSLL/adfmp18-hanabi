package ru.mit.spbau.hanabi.game

abstract class Move {
}

abstract class HintMove(val playerId: Int) : Move() {
}

class ColorHintMove(playerId: Int, val color: Int) : HintMove(playerId) {
}

class ValueHintMove(playerId: Int, val value: Int) : HintMove(playerId) {
}

class SolitaireMove(val cardId: Int) : Move() {
}

class FoldMove(val cardId: Int) : Move() {
}
