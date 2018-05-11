package ru.mit.spbau.hanabi

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button

class MainMenuActivity : AppCompatActivity() {

    override fun onResume() {
        super.onResume()
        BatteryReceiver.register(this)
    }

    override fun onPause() {
        super.onPause()
        BatteryReceiver.unregister(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        val singlePlayerButton = findViewById<Button>(R.id.single_player_button)
        singlePlayerButton?.setOnClickListener { _ ->
            val intent = Intent(this, SinglePlayerActivity::class.java)
            this.startActivity(intent)
        }

        val multiPlayerButton = findViewById<Button>(R.id.multi_player_button)
        multiPlayerButton?.setOnClickListener { _ ->
            val intent = Intent(this, MultiPlayerActivity::class.java)
            this.startActivity(intent)
        }

        val rulesButton = findViewById<Button>(R.id.rules_button)
        rulesButton?.setOnClickListener { _ ->
            val intent = Intent(this, RulesActivity::class.java)
            this.startActivity(intent)
        }

        val settingsButton = findViewById<Button>(R.id.setting_button)
        settingsButton?.setOnClickListener { _ ->
            val intent = Intent(this, SettingsActivity::class.java)
            this.startActivity(intent)
        }

        BatteryReceiver.register(this)

    }

}
