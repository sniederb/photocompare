package ch.want.imagecompare.ui.listfolders;

import android.view.View;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import ch.want.imagecompare.R;
import ch.want.imagecompare.ui.compareimages.CompareImagesActivity;

import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class ListAllImageFoldersActivityTest {

    @Test
    public void onCreate() {
        actAndAssert(Assert::assertNotNull);
    }

    @Test
    public void onClickFolder() {
        // arrange
        actAndAssert(testee -> {
            final RecyclerView recyclerView = testee.findViewById(R.id.folderThumbnails);
            assertNotNull("Recycler view", recyclerView);
            final RecyclerView.ViewHolder viewHolderFirstItem = recyclerView.findViewHolderForAdapterPosition(0);
            assertNotNull("ViewHolder for first item", viewHolderFirstItem);
            final View cardView = viewHolderFirstItem.itemView.findViewById(R.id.thumbnailCard);
            // act
            cardView.performClick();
        });
    }

    private static void actAndAssert(final ActivityScenario.ActivityAction<CompareImagesActivity> action) {
        try (final ActivityScenario<CompareImagesActivity> scenario = ActivityScenario.launch(CompareImagesActivity.class)) {
            scenario.onActivity(action);
        }
    }
}