package ch.want.imagecompare.ui.compareimages;

import android.app.Activity;
import android.content.Intent;

import java.util.ArrayList;

import ch.want.imagecompare.BundleKeys;
import ch.want.imagecompare.data.ImageBean;
import ch.want.imagecompare.domain.FileImageMediaResolver;
import ch.want.imagecompare.domain.PhotoViewMediator;
import ch.want.imagecompare.ui.TransitionHandler;
import ch.want.imagecompare.ui.imageselection.SelectedImagesActivity;

class ShowSelectedImagesTransition {

    private final Activity context;
    private final ArrayList<ImageBean> galleryImageList;
    private final FileImageMediaResolver mediaResolver;
    private final int topImageIndex;
    private final int bottomImageIndex;

    ShowSelectedImagesTransition(final Activity context, final FileImageMediaResolver mediaResolver, final PhotoViewMediator photoViewMediator) {
        this.context = context;
        this.mediaResolver = mediaResolver;
        galleryImageList = photoViewMediator.getGalleryImageList();
        topImageIndex = photoViewMediator.getTopIndex();
        bottomImageIndex = photoViewMediator.getBottomIndex();
    }

    void execute() {
        final Intent intent = new Intent(context, SelectedImagesActivity.class)//
                .putParcelableArrayListExtra(BundleKeys.KEY_SELECTION_COLLECTION, new ArrayList<>(ImageBean.getSelectedImageBeans(galleryImageList)))//
                .putExtra(BundleKeys.KEY_TOPIMAGE_INDEX, topImageIndex)//
                .putExtra(BundleKeys.KEY_BOTTOMIMAGE_INDEX, bottomImageIndex);
        mediaResolver.putToIntent(intent);
        TransitionHandler.switchToActivity(context, intent);
    }
}
