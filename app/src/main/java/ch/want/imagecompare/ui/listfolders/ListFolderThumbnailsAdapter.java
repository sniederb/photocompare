package ch.want.imagecompare.ui.listfolders;

import android.content.res.Resources;
import android.view.View;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import ch.want.imagecompare.R;
import ch.want.imagecompare.data.ImageBean;
import ch.want.imagecompare.ui.thumbnails.ImageBeanListRecyclerViewAdapter;
import ch.want.imagecompare.ui.thumbnails.ImageLayoutSizeParams;

/**
 * Connects RecyclerView with the view attributes for {@link ListAllImageFoldersActivity, and provides a {@link OpenFolderClickHandler}
 * for each thumbnail
 */
class ListFolderThumbnailsAdapter extends ImageBeanListRecyclerViewAdapter<SingleFolderViewHolder> {

    ListFolderThumbnailsAdapter(final List<ImageBean> imageList) {
        super(imageList);
    }

    @Override
    public void onBindViewHolder(@NonNull final SingleFolderViewHolder viewHolder, final int i) {
        final ImageBean imageAndTitle = getImageAndTitleBean(i);
        viewHolder.getTitleView().get().setText(imageAndTitle.getDisplayName());
        super.onBind(viewHolder, i);
    }

    @Override
    protected int getLayoutForViewHolder() {
        return R.layout.view_image_and_title;
    }

    @Override
    protected int getThumbnailCardId() {
        return R.id.thumbnailCard;
    }

    @Override
    protected SingleFolderViewHolder createViewHolder(final View view) {
        return new SingleFolderViewHolder(view);
    }

    @Override
    protected View.OnClickListener createClickHandler(final ImageBean imageAndTitle) {
        final File imageDirectory = imageAndTitle.getImageFile().getParentFile();
        return new OpenFolderClickHandler(imageDirectory);
    }

    @Override
    protected ImageLayoutSizeParams getSizeParams(final Resources resources) {
        return new ImageLayoutSizeParamsForImageAndTitle(resources);
    }
}
