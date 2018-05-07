package ru.mit.spbau.hanabi.game

data class Card(val value: Int, val color: Int, var ownerKnowsVal: Boolean = false,
           var ownerKnowsCol: Boolean = false) {

    fun knowValue() {
        ownerKnowsVal = true
    }

    fun knowColor() {
        ownerKnowsCol = true
    }
}
