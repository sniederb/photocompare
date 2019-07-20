package ch.want.imagecompare.ui.listfolders;

import android.view.View;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import ch.want.imagecompare.R;

import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class ListAllImageFoldersActivityTest {

    @Rule
    public ActivityTestRule<ListAllImageFoldersActivity> mActivityRule = new ActivityTestRule<>(ListAllImageFoldersActivity.class);

    @Test
    public void onCreate() {
        // rule will launch activity automatically, so "no exception" is success indicator
    }

    @Test
    public void onClickFolder() {
        // arrange
        final ListAllImageFoldersActivity testee = mActivityRule.getActivity();
        final RecyclerView recyclerView = testee.findViewById(R.id.folderThumbnails);
        assertNotNull("Recycler view", recyclerView);
        final RecyclerView.ViewHolder viewHolderFirstItem = recyclerView.findViewHolderForAdapterPosition(0);
        assertNotNull("ViewHolder for first item", viewHolderFirstItem);
        final View cardView = viewHolderFirstItem.itemView.findViewById(R.id.thumbnailCard);
        // act
        cardView.performClick();
    }
}