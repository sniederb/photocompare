package ch.want.imagecompare.ui.compareimages;

import android.app.Activity;
import android.content.Intent;

import androidx.core.app.NavUtils;

import java.util.ArrayList;

import ch.want.imagecompare.BundleKeys;
import ch.want.imagecompare.data.ImageBean;
import ch.want.imagecompare.domain.FileImageMediaResolver;
import ch.want.imagecompare.domain.PhotoViewMediator;

class BackToImageListTransition {

    private final Activity sourceActivity;
    private final FileImageMediaResolver mediaResolver;
    private final ArrayList<ImageBean> galleryImageList;
    private final int topImageIndex;
    private final int bottomImageIndex;

    BackToImageListTransition(final Activity sourceActivity, final FileImageMediaResolver mediaResolver, final PhotoViewMediator photoViewMediator) {
        this.sourceActivity = sourceActivity;
        this.mediaResolver = mediaResolver;
        this.galleryImageList = photoViewMediator.getGalleryImageList();
        this.topImageIndex = photoViewMediator.getTopIndex();
        this.bottomImageIndex = photoViewMediator.getBottomIndex();
    }

    void execute() {
        final Intent upIntent = sourceActivity.getParentActivityIntent();
        assert upIntent != null;
        mediaResolver.putToIntent(upIntent);
        upIntent.putParcelableArrayListExtra(BundleKeys.KEY_SELECTION_COLLECTION, new ArrayList<>(ImageBean.getSelectedImageBeans(galleryImageList)))//
                .putExtra(BundleKeys.KEY_TOPIMAGE_INDEX, topImageIndex) //
                .putExtra(BundleKeys.KEY_BOTTOMIMAGE_INDEX, bottomImageIndex);
        NavUtils.navigateUpTo(sourceActivity, upIntent);
    }
}
