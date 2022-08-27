package ch.want.imagecompare.ui.listimages;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertNotNull;

import android.Manifest;
import android.content.Intent;
import android.view.View;

import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.want.imagecompare.BundleKeys;
import ch.want.imagecompare.R;
import ch.want.imagecompare.TestSettings;

@RunWith(AndroidJUnit4.class)
public class ListImagesActivityTest {

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.READ_EXTERNAL_STORAGE);

    @Test
    public void onCreate() {
        final ActivityScenario<ListImagesActivity> scenario = launchActivity();
        Assert.assertEquals(Lifecycle.State.RESUMED, scenario.getState());
    }

    @Test
    public void clickSortCheckbox() {
        launchActivity();
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getTargetContext());
        // see https://stackoverflow.com/questions/24738028/espresso-nomatchingviewexception-when-using-withid-matcher/24743493#24743493
        // Android renders the menu view WITHOUT IDs, so Espresso will not find a view withId()
        onView(withText("Show newest first")).perform(click());
    }

    @Test
    public void onClickSingleImage() {
        // arrange
        final ActivityScenario<ListImagesActivity> scenario = launchActivity();
        scenario.onActivity(activity -> {
            final RecyclerView recyclerView = activity.findViewById(R.id.imageThumbnails);
            assertNotNull("Recycler view", recyclerView);
            final RecyclerView.ViewHolder viewHolderFirstItem = recyclerView.findViewHolderForAdapterPosition(0);
            assertNotNull("ViewHolder for first item", viewHolderFirstItem);
            final View cardView = viewHolderFirstItem.itemView.findViewById(R.id.thumbnailCard);
            // act
            cardView.performClick();
        });
    }

    @Test
    public void onLongClickImage() {
        // arrange
        final ActivityScenario<ListImagesActivity> scenario = launchActivity();
        scenario.onActivity(activity -> {
            final RecyclerView recyclerView = activity.findViewById(R.id.imageThumbnails);
            assertNotNull("Recycler view", recyclerView);
            final RecyclerView.ViewHolder viewHolderFirstItem = recyclerView.findViewHolderForAdapterPosition(0);
            assertNotNull("ViewHolder for first item", viewHolderFirstItem);
            final View cardView = viewHolderFirstItem.itemView.findViewById(R.id.thumbnailCard);
            // act
            cardView.performLongClick();
        });
    }

    private static ActivityScenario<ListImagesActivity> launchActivity() {
        final Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ListImagesActivity.class);
        intent.putExtra(BundleKeys.KEY_IMAGE_FOLDER, TestSettings.DOWNLOAD_FOLDER);
        return ActivityScenario.launch(intent);
    }
}