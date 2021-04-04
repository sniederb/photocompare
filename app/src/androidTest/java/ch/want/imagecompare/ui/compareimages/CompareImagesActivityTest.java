package ch.want.imagecompare.ui.compareimages;

import android.content.Intent;
import android.net.Uri;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import ch.want.imagecompare.BundleKeys;
import ch.want.imagecompare.R;
import ch.want.imagecompare.data.ImageBean;

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

@RunWith(AndroidJUnit4.class)
public class CompareImagesActivityTest {

    @Test
    public void onCreate() {
        actAndAssert(Assert::assertNotNull);
    }

    @Test
    public void clickImageSelection() {
        actAndAssert(activity -> onView(allOf(withId(R.id.selectImageCheckbox), withParent(withId(R.id.upperImage)))).perform(click()));
    }

    @Test
    public void clickMenuDarkCheckboxes() {
        actAndAssert(activity -> {
            openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getTargetContext());
            // see https://stackoverflow.com/questions/24738028/espresso-nomatchingviewexception-when-using-withid-matcher/24743493#24743493
            // Android renders the menu view WITHOUT IDs, so Espresso will not find a view withId()
            onView(withText("Dark checkboxes")).perform(click());
        });
    }

    @Test
    public void clickMenuShowExif() {
        actAndAssert(activity -> {
            openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getTargetContext());
            // see https://stackoverflow.com/questions/24738028/espresso-nomatchingviewexception-when-using-withid-matcher/24743493#24743493
            // Android renders the menu view WITHOUT IDs, so Espresso will not find a view withId()
            onView(withText("Show EXIF")).perform(click());
        });
    }

    @Test
    public void onToggleZoomPanSync() {
        actAndAssert(activity -> {
            onView(withId(R.id.toggleZoomPanSync)).check(matches(isChecked()));
            onView(withId(R.id.toggleZoomPanSync)).perform(click());
            onView(withId(R.id.toggleZoomPanSync)).check(matches(not(isChecked())));
        });
    }

    @Test
    public void onResetMatrixClick() {
        actAndAssert(activity -> onView(withId(R.id.resetMatrix)).perform(click()));
    }

    private static void actAndAssert(final ActivityScenario.ActivityAction<CompareImagesActivity> action) {
        final Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CompareImagesActivity.class);
        intent.putExtra(BundleKeys.KEY_IMAGE_FOLDER, "/storage/emulated/0/Download");
        intent.putParcelableArrayListExtra(BundleKeys.KEY_SELECTION_COLLECTION, buildSelectedImageBeanList());
        intent.putExtra(BundleKeys.KEY_TOPIMAGE_INDEX, 0);
        try (final ActivityScenario<CompareImagesActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(action);
        }
    }

    private static ArrayList<ImageBean> buildSelectedImageBeanList() {
        final ArrayList<ImageBean> beans = new ArrayList<>();
        beans.add(new ImageBean("J0091157.jpg", Uri.parse("file:///storage/emulated/0/Download/EOS77D/J0091157.JPG")));
        return beans;
    }
}