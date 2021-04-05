package ch.want.imagecompare.ui.listfolders;

import android.view.View;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import ch.want.imagecompare.R;

import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class ListAllImageFoldersActivityTest {

    @Test
    public void onCreate() {
        final ActivityScenario<ListAllImageFoldersActivity> scenario = launchActivity();
        Assert.assertEquals(Lifecycle.State.RESUMED, scenario.getState());
    }

    @Test
    public void onClickFolder() {
        // arrange
        final ActivityScenario<ListAllImageFoldersActivity> scenario = launchActivity();
        scenario.onActivity(activity -> {
            final RecyclerView recyclerView = activity.findViewById(R.id.folderThumbnails);
            assertNotNull("Recycler view", recyclerView);
            final RecyclerView.ViewHolder viewHolderFirstItem = recyclerView.findViewHolderForAdapterPosition(0);
            assertNotNull("ViewHolder for first item", viewHolderFirstItem);
            final View cardView = viewHolderFirstItem.itemView.findViewById(R.id.thumbnailCard);
            // act
            cardView.performClick();
        });
    }

    private static ActivityScenario<ListAllImageFoldersActivity> launchActivity() {
        return ActivityScenario.launch(ListAllImageFoldersActivity.class);
    }
}