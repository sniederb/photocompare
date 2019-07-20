package ch.want.imagecompare.ui.compareimages;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

import ch.want.imagecompare.BundleKeys;
import ch.want.imagecompare.data.ImageBean;
import ch.want.imagecompare.domain.PhotoViewMediator;
import ch.want.imagecompare.ui.TransitionHandler;
import ch.want.imagecompare.ui.imageselection.SelectedImagesActivity;

class ShowSelectedImagesTransition {

    private final Context context;
    private final ArrayList<ImageBean> galleryImageList;
    private final int topImageIndex;
    private final int bottomImageIndex;

    ShowSelectedImagesTransition(final Context context, final PhotoViewMediator photoViewMediator) {
        this.context = context;
        galleryImageList = photoViewMediator.getGalleryImageList();
        topImageIndex = photoViewMediator.getTopIndex();
        bottomImageIndex = photoViewMediator.getBottomIndex();
    }

    void execute() {
        final Intent intent = new Intent(context, SelectedImagesActivity.class)//
                .putParcelableArrayListExtra(BundleKeys.KEY_IMAGE_COLLECTION, galleryImageList)//
                .putExtra(BundleKeys.KEY_TOPIMAGE_INDEX, topImageIndex)//
                .putExtra(BundleKeys.KEY_BOTTOMIMAGE_INDEX, bottomImageIndex);
        TransitionHandler.switchToActivity(context, intent);
    }
}
