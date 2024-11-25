package ch.want.imagecompare.ui.imageselection;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;
import android.net.Uri;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.stream.Stream;

import ch.want.imagecompare.BundleKeys;
import ch.want.imagecompare.R;
import ch.want.imagecompare.TestSettings;
import ch.want.imagecompare.data.ImageBean;

@RunWith(AndroidJUnit4.class)
public class SelectedImagesActivityTest {

    @Rule
    public GrantPermissionRule permissionRule = TestSettings.permissionRule();

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
        try (final ActivityScenario<SelectedImagesActivity> scenario = launchActivity()) {
            Assert.assertEquals(Lifecycle.State.RESUMED, scenario.getState());
        }
    }

    @Test
    public void clickShareActionButton() {
        try (final ActivityScenario<SelectedImagesActivity> scenario = launchActivity()) {
            // act
            onView(withId(R.id.shareImagesButton)).perform(click());
        }
    }

    @Test
    public void invertSelectionAction() {
        try (final ActivityScenario<SelectedImagesActivity> scenario = launchActivity()) {
            // act
            openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getTargetContext());
            // see https://stackoverflow.com/questions/24738028/espresso-nomatchingviewexception-when-using-withid-matcher/24743493#24743493
            // Android renders the menu view WITHOUT IDs, so Espresso will not find a view withId()
            onView(withText("Invert selection")).perform(click());
        }
    }

    @Test
    public void removeUnselectedAction() {
        try (final ActivityScenario<SelectedImagesActivity> scenario = launchActivity()) {
            openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getTargetContext());
            onView(withText("Delete all others")).perform(click());
        }
    }

    @Test
    public void removeUnselectedFromCameraAction() {
        try (final ActivityScenario<SelectedImagesActivity> scenario = launchActivity(imagesFromCamera(), TestSettings.CAMERA_FOLDER)) {
            openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getTargetContext());
            onView(withText("Delete all others")).perform(click());
            // This folder seems to be the main camera folder, for which "delete others" is not allowed.
            onView(withText(CoreMatchers.containsString("not allowed"))).check(matches(ViewMatchers.isDisplayed()));
        }
    }

    @Test
    public void removeSelectedAction() {
        try (final ActivityScenario<SelectedImagesActivity> scenario = launchActivity()) {
            // act
            openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getTargetContext());
            onView(withText("Delete selected images")).perform(click());
        }
    }

    private static ActivityScenario<SelectedImagesActivity> launchActivity() {
        return launchActivity(imagesFromDownload(), TestSettings.DOWNLOAD_FOLDER);
    }

    private static ActivityScenario<SelectedImagesActivity> launchActivity(ArrayList<ImageBean> imageSelection, String imageFolder) {
        final Intent intent = new Intent(ApplicationProvider.getApplicationContext(), SelectedImagesActivity.class);
        intent.putExtra(BundleKeys.KEY_IMAGE_FOLDER, imageFolder);
        intent.putParcelableArrayListExtra(BundleKeys.KEY_SELECTION_COLLECTION, imageSelection);
        return ActivityScenario.launch(intent);
    }

    private static ArrayList<ImageBean> imagesFromCamera() {
        final ArrayList<ImageBean> beans = new ArrayList<>();
        Stream.of("IMG_20241124_144834.jpg")
                .map(imgName -> {
                    Uri uri = Uri.parse(String.format("file://%s/%s", TestSettings.CAMERA_FOLDER, imgName));
                    return new ImageBean(imgName, uri);
                })
                .forEach(beans::add);
        return beans;
    }

    private static ArrayList<ImageBean> imagesFromDownload() {
        final ArrayList<ImageBean> beans = new ArrayList<>();
        beans.add(new ImageBean("J0091157.jpg", TestSettings.getUri("J0091157.JPG", "EOS77D")));
        return beans;
    }
}