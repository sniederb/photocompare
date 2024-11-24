package ch.want.imagecompare.ui.compareimages;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.core.IsNot.not;

import android.content.Intent;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import ch.want.imagecompare.BundleKeys;
import ch.want.imagecompare.R;
import ch.want.imagecompare.TestSettings;
import ch.want.imagecompare.data.ImageBean;

@RunWith(AndroidJUnit4.class)
public class CompareImagesActivityTest {

    @Rule
    public GrantPermissionRule permissionRule = TestSettings.permissionRule();

    @Test
    public void onCreate() {
        try (final ActivityScenario<CompareImagesActivity> scenario = launchActivity()) {
            Assert.assertEquals(Lifecycle.State.RESUMED, scenario.getState());
        }
    }

    @Test
    public void clickImageSelection() {
        try (final ActivityScenario<CompareImagesActivity> scenario = launchActivity()) {
            onView(allOf(withId(R.id.selectImageCheckbox), withParent(withId(R.id.upperImage)))).perform(click());
        }
    }

    @Test
    public void clickMenuDarkCheckboxes() {
        try (final ActivityScenario<CompareImagesActivity> scenario = launchActivity()) {
            openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getTargetContext());
            // see https://stackoverflow.com/questions/24738028/espresso-nomatchingviewexception-when-using-withid-matcher/24743493#24743493
            // Android renders the menu view WITHOUT IDs, so Espresso will not find a view withId()
            onView(withText("Dark checkboxes")).perform(click());
        }
    }

    @Test
    public void clickMenuShowExif() {
        try (final ActivityScenario<CompareImagesActivity> scenario = launchActivity()) {
            openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getTargetContext());
            // see https://stackoverflow.com/questions/24738028/espresso-nomatchingviewexception-when-using-withid-matcher/24743493#24743493
            // Android renders the menu view WITHOUT IDs, so Espresso will not find a view withId()
            onView(withText("Show EXIF")).perform(click());
        }
    }

    @Test
    public void onToggleZoomPanSync() {
        try (final ActivityScenario<CompareImagesActivity> scenario = launchActivity()) {
            onView(withId(R.id.toggleZoomPanSync)).check(matches(isChecked()));
            onView(withId(R.id.toggleZoomPanSync)).perform(click());
            onView(withId(R.id.toggleZoomPanSync)).check(matches(not(isChecked())));
        }
    }

    @Test
    public void onResetMatrixClick() {
        try (final ActivityScenario<CompareImagesActivity> scenario = launchActivity()) {
            onView(withId(R.id.resetMatrix)).perform(click());
        }
    }

    private static ActivityScenario<CompareImagesActivity> launchActivity() {
        final Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CompareImagesActivity.class);
        intent.putExtra(BundleKeys.KEY_IMAGE_FOLDER, TestSettings.DOWNLOAD_FOLDER);
        intent.putParcelableArrayListExtra(BundleKeys.KEY_SELECTION_COLLECTION, buildSelectedImageBeanList());
        intent.putExtra(BundleKeys.KEY_TOPIMAGE_INDEX, 0);
        return ActivityScenario.launch(intent);
    }

    private static ArrayList<ImageBean> buildSelectedImageBeanList() {
        final ArrayList<ImageBean> beans = new ArrayList<>();
        beans.add(new ImageBean(TestSettings.SELECTED_IMAGE_1, TestSettings.getUriSelectedImage1()));
        return beans;
    }
}