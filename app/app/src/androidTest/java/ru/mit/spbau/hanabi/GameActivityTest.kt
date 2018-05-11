package ru.mit.spbau.hanabi

import android.support.test.espresso.Espresso
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.rule.ActivityTestRule
import org.hamcrest.CoreMatchers.not
import org.junit.Rule
import org.junit.Test


class GameActivityTest {
    @get:Rule
    val activityRule = ActivityTestRule(GameActivity::class.java)

    @Test
    fun viewsTest() {
        val gameTable = Espresso.onView(ViewMatchers.withId(R.id.game_table))
        gameTable.check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        val handsView = Espresso.onView(ViewMatchers.withId(R.id.hands_view))
        handsView.check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        val infoPanel = Espresso.onView(ViewMatchers.withId(R.id.info_panel))
        infoPanel.check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        val solitaireContent = Espresso.onView(ViewMatchers.withId(R.id.solitaire_tab_content))
        solitaireContent.check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        val junkContent = Espresso.onView(ViewMatchers.withId(R.id.junk_tab_content))
        junkContent.check(ViewAssertions.matches(not(ViewMatchers.isDisplayed())))
    }

}
