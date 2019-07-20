package ch.want.imagecompare.ui.listimages;

import android.content.Intent;
import android.view.View;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import ch.want.imagecompare.BundleKeys;
import ch.want.imagecompare.R;

import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class ListImagesInFolderActivityTest {
    private static final boolean START_IN_TOUCH_MODE = false;
    private static final boolean START_ACTIVITY_AUTOMATICALLY = false;
    private final String mStringToBetyped = "";
    @Rule
    public final ActivityTestRule<ListImagesInFolderActivity> mActivityRule = new ActivityTestRule<>(ListImagesInFolderActivity.class, START_IN_TOUCH_MODE, START_ACTIVITY_AUTOMATICALLY);

    @Test
    public void onCreate() {
        final Intent i = new Intent();
        i.putExtra(BundleKeys.KEY_IMAGE_FOLDER, "/storage/emulated/0/Download");
        mActivityRule.launchActivity(i);
    }

    @Test
    public void onClickSingleImage() {
        // arrange
        onCreate();
        final ListImagesInFolderActivity testee = mActivityRule.getActivity();
        final RecyclerView recyclerView = testee.findViewById(R.id.imageThumbnails);
        assertNotNull("Recycler view", recyclerView);
        final RecyclerView.ViewHolder viewHolderFirstItem = recyclerView.findViewHolderForAdapterPosition(0);
        assertNotNull("ViewHolder for first item", viewHolderFirstItem);
        final View cardView = viewHolderFirstItem.itemView.findViewById(R.id.thumbnailCard);
        // act
        cardView.performClick();
    }

    @Test
    public void onLongClickImage() {
        // arrange
        onCreate();
        final ListImagesInFolderActivity testee = mActivityRule.getActivity();
        final RecyclerView recyclerView = testee.findViewById(R.id.imageThumbnails);
        assertNotNull("Recycler view", recyclerView);
        final RecyclerView.ViewHolder viewHolderFirstItem = recyclerView.findViewHolderForAdapterPosition(0);
        assertNotNull("ViewHolder for first item", viewHolderFirstItem);
        final View cardView = viewHolderFirstItem.itemView.findViewById(R.id.thumbnailCard);
        // act
        cardView.performLongClick();
    }
}