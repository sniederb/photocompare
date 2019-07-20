package ch.want.imagecompare.ui.listimages;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import ch.want.imagecompare.BundleKeys;
import ch.want.imagecompare.data.ImageBean;
import ch.want.imagecompare.ui.TransitionHandler;
import ch.want.imagecompare.ui.compareimages.CompareImagesActivity;

class OpenCompareClickHandler implements View.OnClickListener {

    private final ArrayList<ImageBean> galleryImageList;
    private final int listIndex;

    OpenCompareClickHandler(final List<ImageBean> galleryImageList, final int i) {
        this.galleryImageList = new ArrayList<>(galleryImageList);
        listIndex = i;
    }

    @Override
    public void onClick(final View v) {
        final Context context = v.getContext();
        final Intent intent = new Intent(context, CompareImagesActivity.class)//
                .putParcelableArrayListExtra(BundleKeys.KEY_IMAGE_COLLECTION, galleryImageList);
        final Integer secondarySelection = getSecondarySelection();
        if (secondarySelection != null) {
            intent.putExtra(BundleKeys.KEY_TOPIMAGE_INDEX, secondarySelection)//
                    .putExtra(BundleKeys.KEY_BOTTOMIMAGE_INDEX, listIndex);
        } else {
            intent.putExtra(BundleKeys.KEY_TOPIMAGE_INDEX, listIndex);
        }
        TransitionHandler.switchToActivity(context, intent);
    }

    private Integer getSecondarySelection() {
        for (int i = 0; i < galleryImageList.size(); i++) {
            if (i != listIndex && galleryImageList.get(i).isInitialForCompare()) {
                return i;
            }
        }
        return null;
    }
}
