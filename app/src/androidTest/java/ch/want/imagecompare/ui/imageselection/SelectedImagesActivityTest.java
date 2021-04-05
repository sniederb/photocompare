package ch.want.imagecompare.ui.imageselection;

import android.content.Intent;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import ch.want.imagecompare.BundleKeys;
import ch.want.imagecompare.R;
import ch.want.imagecompare.TestSettings;
import ch.want.imagecompare.data.ImageBean;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class SelectedImagesActivityTest {

    @Before
    public void stubAllExternalIntents() {
        // By default Espresso Intents does not stub any Intents. Stubbing needs to be setup before
        // every test run. In this case all external Intents will be blocked.
        Intents.init();
    }

    @After
    public void releaseEspressoIntents() {
        Intents.release();
    }

    @Test
    public void onCreate() {
        final ActivityScenario<SelectedImagesActivity> scenario = launchActivity();
        Assert.assertEquals(Lifecycle.State.RESUMED, scenario.getState());
    }

    @Test
    public void clickShareActionButton() {
        launchActivity();
        // act
        onView(withId(R.id.shareImagesButton)).perform(click());
    }

    @Test
    public void invertSelectionAction() {
        launchActivity();
        // act
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getTargetContext());
        // see https://stackoverflow.com/questions/24738028/espresso-nomatchingviewexception-when-using-withid-matcher/24743493#24743493
        // Android renders the menu view WITHOUT IDs, so Espresso will not find a view withId()
        onView(withText("Invert selection")).perform(click());
    }

    @Test
    public void removeUnselectedAction() {
        launchActivity();
        //Espresso.openContextualActionModeOverflowMenu();
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getTargetContext());
        onView(withText("Delete all others")).perform(click());
    }

    @Test
    public void removeSelectedAction() {
        launchActivity();
        // act
        //Espresso.openContextualActionModeOverflowMenu();
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getTargetContext());
        onView(withText("Delete selected images")).perform(click());
    }

    private static ActivityScenario<SelectedImagesActivity> launchActivity() {
        final Intent intent = new Intent(ApplicationProvider.getApplicationContext(), SelectedImagesActivity.class);
        final ArrayList<ImageBean> selectedBeans = buildSelectedImageBeanList();
        intent.putExtra(BundleKeys.KEY_IMAGE_FOLDER, TestSettings.DOWNLOAD_FOLDER);
        intent.putParcelableArrayListExtra(BundleKeys.KEY_SELECTION_COLLECTION, selectedBeans);
        return ActivityScenario.launch(intent);
    }

    private static ArrayList<ImageBean> buildSelectedImageBeanList() {
        final ArrayList<ImageBean> beans = new ArrayList<>();
        beans.add(new ImageBean("J0091157.jpg", TestSettings.getUri("J0091157.JPG", "EOS77D")));
        return beans;
    }
}