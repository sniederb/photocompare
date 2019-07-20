package ch.want.imagecompare.ui.compareimages;

import android.content.Intent;

import java.util.ArrayList;

import androidx.core.app.NavUtils;
import ch.want.imagecompare.BundleKeys;
import ch.want.imagecompare.data.ImageBean;

class BackToImageListTransition {

    private final CompareImagesActivity sourceActivity;
    private final ArrayList<ImageBean> galleryImageList;

    BackToImageListTransition(final CompareImagesActivity sourceActivity, final ArrayList<ImageBean> galleryImageList) {
        this.sourceActivity = sourceActivity;
        this.galleryImageList = galleryImageList;
    }

    void execute() {
        final Intent upIntent = sourceActivity.getParentActivityIntent();
        assert upIntent != null;
        upIntent.putParcelableArrayListExtra(BundleKeys.KEY_IMAGE_COLLECTION, galleryImageList);
        NavUtils.navigateUpTo(sourceActivity, upIntent);
    }
}
