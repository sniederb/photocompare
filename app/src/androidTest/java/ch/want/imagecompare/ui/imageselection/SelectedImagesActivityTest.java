package ch.want.imagecompare.ui.imageselection;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import ch.want.imagecompare.BundleKeys;
import ch.want.imagecompare.R;
import ch.want.imagecompare.data.ImageBean;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsNot.not;

@RunWith(AndroidJUnit4.class)
public class SelectedImagesActivityTest {
    private static final boolean START_IN_TOUCH_MODE = false;
    private static final boolean START_ACTIVITY_AUTOMATICALLY = false;
    @Rule
    public final ActivityTestRule<SelectedImagesActivity> mActivityRule = new ActivityTestRule<>(SelectedImagesActivity.class, START_IN_TOUCH_MODE, START_ACTIVITY_AUTOMATICALLY);
    @Rule
    public IntentsTestRule<SelectedImagesActivity> intentsTestRule = new IntentsTestRule<>(SelectedImagesActivity.class);

    @Before
    public void stubAllExternalIntents() {
        // By default Espresso Intents does not stub any Intents. Stubbing needs to be setup before
        // every test run. In this case all external Intents will be blocked.
        intending(not(isInternal())).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
    }

    @Test
    public void onCreate() {
        final Intent i = new Intent();
        final ArrayList<ImageBean> beans = buildImageBeanList();
        beans.get(0).setSelected(true);
        i.putParcelableArrayListExtra(BundleKeys.KEY_IMAGE_COLLECTION, beans);
        mActivityRule.launchActivity(i);
    }

    @Test
    public void clickShareActionButton() {
        // arrange
        onCreate();
        // act
        onView(withId(R.id.shareImagesButton)).perform(click());
    }

    @Test
    public void invertSelectionAction() {
        // arrange
        onCreate();
        // act
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getTargetContext());
        // see https://stackoverflow.com/questions/24738028/espresso-nomatchingviewexception-when-using-withid-matcher/24743493#24743493
        // Android renders the menu view WITHOUT IDs, so Espresso will not find a view withId()
        onView(withText("Invert selection")).perform(click());
    }

    @Test
    public void removeUnselectedAction() {
        // arrange
        onCreate();
        // act
        //Espresso.openContextualActionModeOverflowMenu();
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getTargetContext());
        onView(withText("Delete all others")).perform(click());
    }

    private static ArrayList<ImageBean> buildImageBeanList() {
        final ArrayList<ImageBean> beans = new ArrayList<>();
        beans.add(new ImageBean("J0091157.jpg", Uri.parse("file:///storage/emulated/0/Download/EOS77D/J0091157.JPG")));
        beans.add(new ImageBean("J0091158.jpg", Uri.parse("file:///storage/emulated/0/Download/EOS77D/J0091158.JPG")));
        beans.add(new ImageBean("J0091159.jpg", Uri.parse("file:///storage/emulated/0/Download/EOS77D/J0091159.JPG")));
        return beans;
    }
}