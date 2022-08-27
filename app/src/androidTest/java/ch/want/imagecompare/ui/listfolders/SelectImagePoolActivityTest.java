package ch.want.imagecompare.ui.listfolders;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;

import android.Manifest;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.want.imagecompare.R;

@RunWith(AndroidJUnit4.class)
public class SelectImagePoolActivityTest {

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.READ_EXTERNAL_STORAGE);

    @Test
    public void onCreate() {
        final ActivityScenario<SelectImagePoolActivity> scenario = launchActivity();
        Assert.assertEquals(Lifecycle.State.RESUMED, scenario.getState());
    }

    @Test
    public void onClickFolder() {
        // arrange
        final ActivityScenario<SelectImagePoolActivity> scenario = launchActivity();
        // act
        Espresso.onView(ViewMatchers.withId(R.id.folderThumbnails)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        // assert
        assertImageListActivityLoaded();
        scenario.close();
    }

    @Test
    public void onApplyDateSelection() {
        // arrange
        final ActivityScenario<SelectImagePoolActivity> scenario = launchActivity();
//        Espresso.onView(ViewMatchers.withId(R.id.selectByDateValue)).perform(typeText("4/19/19"));
        // act
        Espresso.onView(ViewMatchers.withId(R.id.applyDateSelection)).perform(click());
        // assert
        assertImageListActivityLoaded();
        scenario.close();
    }

    private static void assertImageListActivityLoaded() {
        Espresso.onView(allOf(instanceOf(TextView.class), withParent(ViewMatchers.withId(R.id.my_toolbar)))).check(matches(withSubstring("Select image(s) to start")));
    }

    private static ActivityScenario<SelectImagePoolActivity> launchActivity() {
        return ActivityScenario.launch(SelectImagePoolActivity.class);
    }
}