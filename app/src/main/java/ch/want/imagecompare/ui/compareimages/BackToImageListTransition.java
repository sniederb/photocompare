package ch.want.imagecompare.ui.compareimages;

import android.app.Activity;
import android.content.Intent;

import java.util.ArrayList;

import androidx.core.app.NavUtils;
import ch.want.imagecompare.BundleKeys;
import ch.want.imagecompare.data.ImageBean;

class BackToImageListTransition {

    private final Activity sourceActivity;
    private final String currentImageFolder;
    private final boolean sortNewestFirst;
    private final ArrayList<ImageBean> galleryImageList;

    BackToImageListTransition(final Activity sourceActivity, final String currentImageFolder, final boolean sortNewestFirst, final ArrayList<ImageBean> galleryImageList) {
        this.sourceActivity = sourceActivity;
        this.currentImageFolder = currentImageFolder;
        this.sortNewestFirst = sortNewestFirst;
        this.galleryImageList = galleryImageList;
    }

    void execute() {
        final Intent upIntent = sourceActivity.getParentActivityIntent();
        assert upIntent != null;
        upIntent.putExtra(BundleKeys.KEY_IMAGE_FOLDER, currentImageFolder);
        upIntent.putExtra(BundleKeys.KEY_SORT_NEWEST_FIRST, sortNewestFirst);
        upIntent.putParcelableArrayListExtra(BundleKeys.KEY_SELECTION_COLLECTION, new ArrayList<>(ImageBean.getSelectedImageBeans(galleryImageList)));
        NavUtils.navigateUpTo(sourceActivity, upIntent);
    }
}
