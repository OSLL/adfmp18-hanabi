package ru.mit.spbau.hanabi

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.Espresso.pressBack
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import org.hamcrest.CoreMatchers.containsString
import org.junit.Rule
import org.junit.Test

class MainMenuActivityTest {
    @get:Rule
    val activityRule = ActivityTestRule(MainMenuActivity::class.java)

    @Test
    fun titleTest() {
        val title = onView(withId(R.id.app_title))
        title.check(matches(withText(containsString("Hanabi"))))
    }

    @Test
    fun navigateToSinglePlayerTest() {
        val singlePlayerButton = onView(withId(R.id.single_player_button))
        singlePlayerButton.check(matches(isDisplayed()))
        singlePlayerButton.perform(click())
        pressBack()
    }

    @Test
    fun navigateToMultiPlayerTest() {
        val multiPlayerButton = onView(withId(R.id.multi_player_button))
        multiPlayerButton.check(matches(isDisplayed()))
        multiPlayerButton.perform(click())
        pressBack()
    }

    @Test
    fun navigateToSettingsTest() {
        val settingsButton = onView(withId(R.id.setting_button))
        settingsButton.check(matches(isDisplayed()))
        settingsButton.perform(click())
        pressBack()
    }
}
