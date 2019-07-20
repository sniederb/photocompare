package ch.want.imagecompare.ui.listimages;

import android.content.res.Resources;
import android.view.View;

import java.util.List;

import androidx.annotation.NonNull;
import ch.want.imagecompare.R;
import ch.want.imagecompare.data.ImageBean;
import ch.want.imagecompare.ui.thumbnails.ImageBeanListRecyclerViewAdapter;
import ch.want.imagecompare.ui.thumbnails.ImageLayoutSizeParams;

/**
 * Connects RecyclerView with the view attributes for {@link ListImagesInFolderActivity, and provides a {@link OpenCompareClickHandler}
 * for each thumbnail
 */
class ListImagesThumbnailsAdapter extends ImageBeanListRecyclerViewAdapter<SingleImageViewHolder> {

    ListImagesThumbnailsAdapter(final List<ImageBean> imageList) {
        super(imageList);
    }

    @Override
    public void onBindViewHolder(@NonNull final SingleImageViewHolder viewHolder, final int i) {
        super.onBind(viewHolder, i);
    }

    @Override
    protected int getLayoutForViewHolder() {
        return R.layout.view_image_only;
    }

    @Override
    protected int getThumbnailCardId() {
        return R.id.thumbnailCard;
    }

    @Override
    protected SingleImageViewHolder createViewHolder(final View view) {
        return new SingleImageViewHolder(view);
    }

    @Override
    protected View.OnClickListener createClickHandler(final ImageBean imageAndTitle) {
        throw new UnsupportedOperationException("Use signature with collection and index instead");
    }

    @Override
    protected View.OnClickListener createClickHandler(final List<ImageBean> galleryImageList, final int i) {
        return new OpenCompareClickHandler(galleryImageList, i);
    }

    @Override
    protected ImageLayoutSizeParams getSizeParams(final Resources resources) {
        return new ImageLayoutSizeParamsForImageOnly(resources);
    }
}
