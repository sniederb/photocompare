package ch.want.imagecompare.ui.compareimages;

import android.app.Activity;
import android.content.Intent;

import java.util.ArrayList;

import androidx.core.app.NavUtils;
import ch.want.imagecompare.BundleKeys;
import ch.want.imagecompare.data.ImageBean;
import ch.want.imagecompare.domain.FileImageMediaResolver;

class BackToImageListTransition {

    private final Activity sourceActivity;
    private final FileImageMediaResolver mediaResolver;
    private final ArrayList<ImageBean> galleryImageList;

    BackToImageListTransition(final Activity sourceActivity, final FileImageMediaResolver mediaResolver, final ArrayList<ImageBean> galleryImageList) {
        this.sourceActivity = sourceActivity;
        this.mediaResolver = mediaResolver;
        this.galleryImageList = galleryImageList;
    }

    void execute() {
        final Intent upIntent = sourceActivity.getParentActivityIntent();
        assert upIntent != null;
        mediaResolver.putToIntent(upIntent);
        upIntent.putParcelableArrayListExtra(BundleKeys.KEY_SELECTION_COLLECTION, new ArrayList<>(ImageBean.getSelectedImageBeans(galleryImageList)));
        NavUtils.navigateUpTo(sourceActivity, upIntent);
    }
}
