package ch.want.imagecompare.ui.imageselection;

import android.app.Activity;
import android.content.Intent;

import java.util.ArrayList;

import androidx.core.app.NavUtils;
import ch.want.imagecompare.BundleKeys;
import ch.want.imagecompare.data.ImageBean;
import ch.want.imagecompare.domain.FileImageMediaResolver;

/**
 * From "show selection", handle navigation to {@link ch.want.imagecompare.ui.compareimages.CompareImagesActivity}
 */
class BackToCompareImagesTransition {

    private final ArrayList<ImageBean> galleryImageList;
    private final Activity sourceActivity;
    private final FileImageMediaResolver mediaResolver;
    private final int topImageIndex;
    private final int bottomImageIndex;

    BackToCompareImagesTransition(final Activity sourceActivity, final FileImageMediaResolver mediaResolver, final ArrayList<ImageBean> galleryImageList, final int topImageIndex, final int bottomImageIndex) {
        this.sourceActivity = sourceActivity;
        this.mediaResolver = mediaResolver;
        this.galleryImageList = galleryImageList;
        this.topImageIndex = topImageIndex;
        this.bottomImageIndex = bottomImageIndex;
    }

    void execute() {
        final Intent upIntent = sourceActivity.getParentActivityIntent();
        assert upIntent != null;
        upIntent.putParcelableArrayListExtra(BundleKeys.KEY_SELECTION_COLLECTION, new ArrayList<>(ImageBean.getSelectedImageBeans(galleryImageList)))//
                .putExtra(BundleKeys.KEY_TOPIMAGE_INDEX, topImageIndex).putExtra(BundleKeys.KEY_BOTTOMIMAGE_INDEX, bottomImageIndex);
        mediaResolver.putToIntent(upIntent);
        NavUtils.navigateUpTo(sourceActivity, upIntent);
    }
}
