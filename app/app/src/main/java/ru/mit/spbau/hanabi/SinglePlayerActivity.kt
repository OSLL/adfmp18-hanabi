package ru.mit.spbau.hanabi

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.NumberPicker

class SinglePlayerActivity : AppCompatActivity() {

    var mPicker: NumberPicker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_player)
        setupNumberPicker()
        setupStartButton()
    }

    private fun setupNumberPicker() {
        mPicker = findViewById(R.id.players_number_picker)
        mPicker?.minValue = 2
        mPicker?.maxValue = 5
        mPicker?.wrapSelectorWheel = false
    }

    private fun setupStartButton() {
        val startButton = findViewById<Button>(R.id.start_button)
        startButton?.setOnClickListener { _ ->
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("PLAYERS_NUMBER", mPicker!!.value)
            this.startActivity(intent)
        }
    }

}
