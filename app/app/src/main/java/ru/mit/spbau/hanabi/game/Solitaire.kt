package ru.mit.spbau.hanabi.game

class Solitaire {
    val maxValue = IntArray(5)

    fun tryAdd(card: Card): Boolean {
        return if (maxValue[card.color - 1] == card.value - 1) {
            maxValue[card.color - 1]++
            true
        } else {
            false
        }
    }

    fun canAdd(card: Card): Boolean {
        return maxValue[card.color - 1] == card.value - 1
    }

    fun getScore(): Int {
        var sum = 0
        for (x in maxValue) {
            sum += x
        }
        return sum
    }
}
