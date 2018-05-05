package ru.mit.spbau.hanabi.game

abstract class Move(val fromPlayerId: Int) {
}

abstract class HintMove(fromPlayerId: Int, val playerId: Int) : Move(fromPlayerId) {
}

class ColorHintMove(fromPlayerId: Int, playerId: Int, val color: Int) : HintMove(fromPlayerId, playerId) {
}

class ValueHintMove(fromPlayerId: Int, playerId: Int, val value: Int) : HintMove(fromPlayerId, playerId) {
}

class SolitaireMove(fromPlayerId: Int, val cardId: Int) : Move(fromPlayerId) {
}

class FoldMove(fromPlayerId: Int, val cardId: Int) : Move(fromPlayerId) {
}
