package ch.want.imagecompare.ui.imageselection;

import android.content.Intent;

import java.util.ArrayList;

import androidx.core.app.NavUtils;
import ch.want.imagecompare.BundleKeys;
import ch.want.imagecompare.data.ImageBean;

class BackToCompareImagesTransition {

    private final ArrayList<ImageBean> galleryImageList;
    private final SelectedImagesActivity sourceActivity;
    private final int topImageIndex;
    private final int bottomImageIndex;

    BackToCompareImagesTransition(final SelectedImagesActivity sourceActivity, final ArrayList<ImageBean> galleryImageList, final int topImageIndex, final int bottomImageIndex) {
        this.sourceActivity = sourceActivity;
        this.galleryImageList = galleryImageList;
        this.topImageIndex = topImageIndex;
        this.bottomImageIndex = bottomImageIndex;
    }

    void execute() {
        final Intent upIntent = sourceActivity.getParentActivityIntent();
        assert upIntent != null;
        upIntent.putParcelableArrayListExtra(BundleKeys.KEY_IMAGE_COLLECTION, galleryImageList);
        upIntent.putExtra(BundleKeys.KEY_TOPIMAGE_INDEX, topImageIndex);
        upIntent.putExtra(BundleKeys.KEY_BOTTOMIMAGE_INDEX, bottomImageIndex);
        NavUtils.navigateUpTo(sourceActivity, upIntent);
    }
}
