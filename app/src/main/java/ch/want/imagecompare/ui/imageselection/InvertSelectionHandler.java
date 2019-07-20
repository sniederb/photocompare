package ch.want.imagecompare.ui.imageselection;

import java.util.ArrayList;

import ch.want.imagecompare.data.ImageBean;

class InvertSelectionHandler {

    private final ArrayList<ImageBean> galleryImageList;

    InvertSelectionHandler(final ArrayList<ImageBean> galleryImageList) {
        this.galleryImageList = galleryImageList;
    }

    void execute() {
        for (final ImageBean image : galleryImageList) {
            image.setSelected(!image.isSelected());
        }
    }
}
