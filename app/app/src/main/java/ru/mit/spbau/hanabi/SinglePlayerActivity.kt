package ru.mit.spbau.hanabi

import android.os.Bundle
import android.widget.NumberPicker

class SinglePlayerActivity : ActivityWithMenu() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_player)
        setupNumberPicker()
    }

    private fun setupNumberPicker() {
        val nPicker = findViewById<NumberPicker>(R.id.players_number_picker)
        nPicker.minValue = 2
        nPicker.maxValue = 5
        nPicker.wrapSelectorWheel = false
    }

}
