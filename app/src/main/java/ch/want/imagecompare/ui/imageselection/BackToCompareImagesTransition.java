package ch.want.imagecompare.ui.imageselection;

import android.app.Activity;
import android.content.Intent;

import java.util.ArrayList;

import androidx.core.app.NavUtils;
import ch.want.imagecompare.BundleKeys;
import ch.want.imagecompare.data.ImageBean;

/**
 * From "show selection", handle navigation to {@link ch.want.imagecompare.ui.compareimages.CompareImagesActivity}
 */
class BackToCompareImagesTransition {

    private final ArrayList<ImageBean> galleryImageList;
    private final Activity sourceActivity;
    private final String currentImageFolder;
    private final int topImageIndex;
    private final int bottomImageIndex;

    BackToCompareImagesTransition(final Activity sourceActivity, final String currentImageFolder, final ArrayList<ImageBean> galleryImageList, final int topImageIndex, final int bottomImageIndex) {
        this.sourceActivity = sourceActivity;
        this.currentImageFolder = currentImageFolder;
        this.galleryImageList = galleryImageList;
        this.topImageIndex = topImageIndex;
        this.bottomImageIndex = bottomImageIndex;
    }

    void execute() {
        final Intent upIntent = sourceActivity.getParentActivityIntent();
        assert upIntent != null;
        upIntent.putExtra(BundleKeys.KEY_IMAGE_FOLDER, currentImageFolder)//
                .putParcelableArrayListExtra(BundleKeys.KEY_SELECTION_COLLECTION, new ArrayList<>(ImageBean.getSelectedImageBeans(galleryImageList)))//
                .putExtra(BundleKeys.KEY_TOPIMAGE_INDEX, topImageIndex).putExtra(BundleKeys.KEY_BOTTOMIMAGE_INDEX, bottomImageIndex);
        NavUtils.navigateUpTo(sourceActivity, upIntent);
    }
}
