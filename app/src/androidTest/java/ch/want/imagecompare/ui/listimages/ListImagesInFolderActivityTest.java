package ch.want.imagecompare.ui.listimages;

import android.content.Intent;
import android.view.View;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import ch.want.imagecompare.BundleKeys;
import ch.want.imagecompare.R;
import ch.want.imagecompare.ui.compareimages.CompareImagesActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class ListImagesInFolderActivityTest {

    @Test
    public void onCreate() {
        actAndAssert(Assert::assertNotNull);
    }

    @Test
    public void clickSortCheckbox() {
        actAndAssert(activity -> {
            openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getTargetContext());
            // see https://stackoverflow.com/questions/24738028/espresso-nomatchingviewexception-when-using-withid-matcher/24743493#24743493
            // Android renders the menu view WITHOUT IDs, so Espresso will not find a view withId()
            onView(withText("Show newest first")).perform(click());
        });
    }

    @Test
    public void onClickSingleImage() {
        // arrange
        actAndAssert(testee -> {
            final RecyclerView recyclerView = testee.findViewById(R.id.imageThumbnails);
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
        actAndAssert(testee -> {
            final RecyclerView recyclerView = testee.findViewById(R.id.imageThumbnails);
            assertNotNull("Recycler view", recyclerView);
            final RecyclerView.ViewHolder viewHolderFirstItem = recyclerView.findViewHolderForAdapterPosition(0);
            assertNotNull("ViewHolder for first item", viewHolderFirstItem);
            final View cardView = viewHolderFirstItem.itemView.findViewById(R.id.thumbnailCard);
            // act
            cardView.performLongClick();
        });
    }

    private static void actAndAssert(final ActivityScenario.ActivityAction<CompareImagesActivity> action) {
        final Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CompareImagesActivity.class);
        intent.putExtra(BundleKeys.KEY_IMAGE_FOLDER, "/storage/emulated/0/Download");
        try (final ActivityScenario<CompareImagesActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(action);
        }
    }
}