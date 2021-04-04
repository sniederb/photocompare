package ch.want.imagecompare.ui.imageselection;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;

import org.junit.Assert;
import org.junit.Before;
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
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsNot.not;

@RunWith(AndroidJUnit4.class)
public class SelectedImagesActivityTest {

    @Before
    public void stubAllExternalIntents() {
        // By default Espresso Intents does not stub any Intents. Stubbing needs to be setup before
        // every test run. In this case all external Intents will be blocked.
        intending(not(isInternal())).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
    }

    @Test
    public void onCreate() {
        actAndAssert(Assert::assertNotNull);
    }

    @Test
    public void clickShareActionButton() {
        actAndAssert(activity ->
                // act
                onView(withId(R.id.shareImagesButton)).perform(click()));
    }

    @Test
    public void invertSelectionAction() {
        actAndAssert(activity -> {
            // act
            openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getTargetContext());
            // see https://stackoverflow.com/questions/24738028/espresso-nomatchingviewexception-when-using-withid-matcher/24743493#24743493
            // Android renders the menu view WITHOUT IDs, so Espresso will not find a view withId()
            onView(withText("Invert selection")).perform(click());
        });
    }

    @Test
    public void removeUnselectedAction() {
        actAndAssert(activity -> {
            //Espresso.openContextualActionModeOverflowMenu();
            openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getTargetContext());
            onView(withText("Delete all others")).perform(click());
        });
    }

    @Test
    public void removeSelectedAction() {
        actAndAssert(activity -> {
            // act
            //Espresso.openContextualActionModeOverflowMenu();
            openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getTargetContext());
            onView(withText("Delete selected images")).perform(click());
        });
    }

    private static void actAndAssert(final ActivityScenario.ActivityAction<SelectedImagesActivity> action) {
        final Intent intent = new Intent(ApplicationProvider.getApplicationContext(), SelectedImagesActivity.class);
        final ArrayList<ImageBean> selectedBeans = buildSelectedImageBeanList();
        intent.putExtra(BundleKeys.KEY_IMAGE_FOLDER, "/storage/emulated/0/Download");
        intent.putParcelableArrayListExtra(BundleKeys.KEY_SELECTION_COLLECTION, selectedBeans);
        try (final ActivityScenario<SelectedImagesActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(action);
        }
    }

    private static ArrayList<ImageBean> buildSelectedImageBeanList() {
        final ArrayList<ImageBean> beans = new ArrayList<>();
        beans.add(new ImageBean("J0091157.jpg", Uri.parse("file:///storage/emulated/0/Download/EOS77D/J0091157.JPG")));
        return beans;
    }
}