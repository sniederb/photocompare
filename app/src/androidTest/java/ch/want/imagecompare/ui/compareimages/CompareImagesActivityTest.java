package ch.want.imagecompare.ui.compareimages;

import android.content.Intent;
import android.net.Uri;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
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
    private static final boolean START_IN_TOUCH_MODE = false;
    private static final boolean START_ACTIVITY_AUTOMATICALLY = false;
    @Rule
    public final ActivityTestRule<CompareImagesActivity> mActivityRule = new ActivityTestRule<>(CompareImagesActivity.class, START_IN_TOUCH_MODE, START_ACTIVITY_AUTOMATICALLY);

    @Test
    public void onCreate() {
        final Intent i = new Intent();
        i.putParcelableArrayListExtra(BundleKeys.KEY_IMAGE_COLLECTION, buildImageBeanList());
        i.putExtra(BundleKeys.KEY_TOPIMAGE_INDEX, 0);
        mActivityRule.launchActivity(i);
    }

    @Test
    public void clickImageSelection() {
        onCreate();
        onView(allOf(withId(R.id.selectImageCheckbox), withParent(withId(R.id.upperImage)))).perform(click());
    }

    @Test
    public void clickMenuDarkCheckboxes() {
        onCreate();
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getTargetContext());
        // see https://stackoverflow.com/questions/24738028/espresso-nomatchingviewexception-when-using-withid-matcher/24743493#24743493
        // Android renders the menu view WITHOUT IDs, so Espresso will not find a view withId()
        onView(withText("Dark checkboxes")).perform(click());
    }

    @Test
    public void onToggleZoomPanSync() {
        onCreate();
        onView(withId(R.id.toggleZoomPanSync)).check(matches(isChecked()));
        onView(withId(R.id.toggleZoomPanSync)).perform(click());
        onView(withId(R.id.toggleZoomPanSync)).check(matches(not(isChecked())));
    }

    @Test
    public void onResetMatrixClick() {
        onCreate();
        onView(withId(R.id.resetMatrix)).perform(click());
    }

    private static ArrayList<ImageBean> buildImageBeanList() {
        final ArrayList<ImageBean> beans = new ArrayList<>();
        beans.add(new ImageBean("J0091157.jpg", Uri.parse("file:///storage/emulated/0/Download/EOS77D/J0091157.JPG")));
        beans.add(new ImageBean("J0091158.jpg", Uri.parse("file:///storage/emulated/0/Download/EOS77D/J0091158.JPG")));
        beans.add(new ImageBean("J0091159.jpg", Uri.parse("file:///storage/emulated/0/Download/EOS77D/J0091159.JPG")));
        return beans;
    }
}