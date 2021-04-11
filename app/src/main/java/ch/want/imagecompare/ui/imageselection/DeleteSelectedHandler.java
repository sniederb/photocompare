package ch.want.imagecompare.ui.imageselection;

import java.util.ArrayList;
import java.util.List;

import ch.want.imagecompare.data.ImageBean;
import ch.want.imagecompare.domain.FileImageMediaResolver;

class DeleteSelectedHandler extends AbstractDeleteFilesHandler {

    DeleteSelectedHandler(final SelectedImagesActivity selectedImagesActivity, final FileImageMediaResolver mediaResolver, final ArrayList<ImageBean> galleryImageList) {
        super(selectedImagesActivity, mediaResolver, galleryImageList);
    }

    @Override
    protected List<ImageBean> getObsoleteImages() {
        return ImageBean.getSelectedImageBeans(galleryImageList);
    }
}
