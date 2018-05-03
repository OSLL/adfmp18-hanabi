package ru.mit.spbau.hanabi

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.NumberPicker

class CreateGameActivity : AppCompatActivity() {

    private var mPicker: NumberPicker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_game)
        setupNumberPicker()
    }

    private fun setupNumberPicker() {
        mPicker = findViewById(R.id.numberPicker)
        mPicker?.minValue = 2
        mPicker?.maxValue = 5
        mPicker?.wrapSelectorWheel = false
    }

    private fun setupCreateBtn() {
//        val createButton = findViewById<Button>(R.id.start_button)
//        createButton?.setOnClickListener { _ ->
//            val intent = Intent(this, GameRoomActivity::class.java)
//            this.startActivity(intent)
//        }
    }


}
