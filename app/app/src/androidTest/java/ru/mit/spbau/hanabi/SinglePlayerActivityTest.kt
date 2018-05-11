package ru.mit.spbau.hanabi

import android.support.test.espresso.Espresso
import android.support.test.espresso.ViewAction
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.rule.ActivityTestRule
import org.hamcrest.CoreMatchers
import org.junit.Rule
import org.junit.Test
import android.widget.NumberPicker
import android.support.test.espresso.UiController
import android.view.View
import org.hamcrest.Matcher


class SingleGameActivityTest {
    @get:Rule
    val activityRule = ActivityTestRule(SinglePlayerActivity::class.java)

    @Test
    fun pickerAndTitleTest() {
        val title = Espresso.onView(ViewMatchers.withId(R.id.single_player_title))
        title.check(ViewAssertions.matches(ViewMatchers.withText(
                CoreMatchers.containsString("single player"))))
        val pickerText = Espresso.onView(ViewMatchers.withId(R.id.choose_players_num_text))
        pickerText.check(ViewAssertions.matches(ViewMatchers.withText(
                CoreMatchers.containsString("Choose the number of players"))))
        val picker = Espresso.onView(ViewMatchers.withId(R.id.players_number_picker))
        picker.check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    private fun startGameTest(expectedPlayersCnt: Int) {
        val startButton = Espresso.onView(ViewMatchers.withId(R.id.start_button))
        startButton.check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        startButton.perform(ViewActions.click())

        val handsView = Espresso.onView(ViewMatchers.withId(R.id.hands_view))
        handsView.check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        handsView.check(ViewAssertions.matches(ViewMatchers.hasChildCount(expectedPlayersCnt)))

        Espresso.pressBack()
    }

    private fun setNumber(num: Int): ViewAction {
        return object : ViewAction {
            override fun perform(uiController: UiController, view: View) {
                val np = view as NumberPicker
                np.value = num
            }

            override fun getDescription(): String {
                return "Set the passed number into the NumberPicker"
            }

            override fun getConstraints(): Matcher<View> {
                return ViewMatchers.isAssignableFrom(NumberPicker::class.java)
            }
        }
    }

    @Test
    fun startGameWithDifferentNumberOfPlayersTest() {
        val picker = Espresso.onView(ViewMatchers.withId(R.id.players_number_picker))
        for (i in 2..5) {
            picker.perform(setNumber(i))
            startGameTest(i)
        }
    }
}
