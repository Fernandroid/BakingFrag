package com.example.asus.bakingFrag;


import android.support.annotation.NonNull;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.rule.ActivityTestRule;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static android.support.test.internal.util.Checks.checkNotNull;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.hamcrest.Matchers.allOf;
import static android.support.test.espresso.matcher.ViewMatchers.withResourceName;
import static org.hamcrest.Matchers.instanceOf;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import  android.support.v7.widget.Toolbar;

@RunWith(AndroidJUnit4.class)
public class MainActivityListTest {
    public static final String RECIPE_NAME = "Brownies";
    private final int RECYCLER_POSITION = 1;
/**
 * The ActivityTestRule is a rule provided by Android used for functional testing of a single
 * activity. The activity that will be tested will be launched before each test that's annotated
 * with @Test and before methods annotated with @Before. The activity will be terminated after
 * the test and methods annotated with @After are complete. This rule allows you to directly
 * access the activity during the test.
 */

@Rule
    public ActivityTestRule<MainActivity>mActivityTestRule = new ActivityTestRule<>(MainActivity.class);
    /**
     * Clicks on a GridView item and checks it opens up the OrderActivity with the correct details.
     */

    @Test
    public void clickRecyclerViewOpenRecipe(){

        onView(withId(R.id.recycler)).perform(RecyclerViewActions.scrollToPosition(RECYCLER_POSITION))
                .check(matches(atPosition(RECYCLER_POSITION, hasDescendant(withText(RECIPE_NAME)))));

        onView(withId(R.id.recycler)).perform(RecyclerViewActions.actionOnItemAtPosition(RECYCLER_POSITION, click()));
        onView(allOf(isDescendantOfA(withResourceName("action_bar_container")), withText(RECIPE_NAME))).check(matches(isDisplayed()));
    }
    // Ccustom matcher which look in a specific position of a recycler view.
    //  source: https://stackoverflow.com/a/34795431/1857023
    public static Matcher<View> atPosition(final int position, @NonNull final Matcher<View> itemMatcher) {
        checkNotNull(itemMatcher);
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    // has no item on such position
                    return false;
                }
                return itemMatcher.matches(viewHolder.itemView);
            }
        };
    }
}
