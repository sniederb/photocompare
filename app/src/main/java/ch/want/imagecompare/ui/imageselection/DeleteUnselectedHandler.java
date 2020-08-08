package ch.want.imagecompare.ui.imageselection;

import java.util.ArrayList;
import java.util.List;

import ch.want.imagecompare.data.ImageBean;

class DeleteUnselectedHandler extends AbstractDeleteFilesHandler {

    DeleteUnselectedHandler(final SelectedImagesActivity selectedImagesActivity, final ArrayList<ImageBean> galleryImageList) {
        super(selectedImagesActivity, galleryImageList);
    }

    @Override
    protected List<ImageBean> getObsoleteImages() {
        return ImageBean.getUnselectedImageBeans(galleryImageList);
    }
}
