package ch.want.imagecompare.ui.listimages;

import android.app.Activity;
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
    private final String imageFolder;
    private final boolean sortNewestFirst;
    private final int listIndex;

    OpenCompareClickHandler(final String imageFolder, final boolean sortNewestFirst, final List<ImageBean> galleryImageList, final int i) {
        this.imageFolder = imageFolder;
        this.sortNewestFirst = sortNewestFirst;
        this.galleryImageList = new ArrayList<>(galleryImageList);
        listIndex = i;
    }

    @Override
    public void onClick(final View v) {
        final Context context = v.getContext();
        final Intent intent = new Intent(context, CompareImagesActivity.class)//
                .putExtra(BundleKeys.KEY_IMAGE_FOLDER, imageFolder)//
                .putExtra(BundleKeys.KEY_SORT_NEWEST_FIRST, sortNewestFirst)//
                .putParcelableArrayListExtra(BundleKeys.KEY_SELECTION_COLLECTION, new ArrayList<>(ImageBean.getSelectedImageBeans(galleryImageList)));
        final Integer secondarySelection = getSecondarySelection();
        if (secondarySelection != null) {
            intent.putExtra(BundleKeys.KEY_TOPIMAGE_INDEX, secondarySelection)//
                    .putExtra(BundleKeys.KEY_BOTTOMIMAGE_INDEX, listIndex);
        } else {
            intent.putExtra(BundleKeys.KEY_TOPIMAGE_INDEX, listIndex);
        }
        TransitionHandler.switchToActivity((Activity) context, intent);
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
