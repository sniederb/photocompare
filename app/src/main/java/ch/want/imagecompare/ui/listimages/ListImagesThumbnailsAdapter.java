package ch.want.imagecompare.ui.listimages;

import android.content.res.Resources;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.util.List;

import ch.want.imagecompare.R;
import ch.want.imagecompare.data.ImageBean;
import ch.want.imagecompare.domain.FileImageMediaResolver;
import ch.want.imagecompare.ui.ImageLayoutSizeParamsForImageOnly;
import ch.want.imagecompare.ui.thumbnails.ImageBeanListRecyclerViewAdapter;
import ch.want.imagecompare.ui.thumbnails.ImageLayoutSizeParams;

/**
 * Connects RecyclerView with the view attributes for {@link ListImagesActivity}, and provides a {@link OpenCompareClickHandler}
 * for each thumbnail
 */
class ListImagesThumbnailsAdapter extends ImageBeanListRecyclerViewAdapter<SingleImageViewHolder> {

    private final FileImageMediaResolver mediaResolver;

    ListImagesThumbnailsAdapter(final FileImageMediaResolver mediaResolver, final List<ImageBean> imageList) {
        super(imageList);
        this.mediaResolver = mediaResolver;
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
    protected View.OnClickListener createClickHandler(final List<ImageBean> galleryImageList, final int selectedIndex) {
        return new OpenCompareClickHandler(mediaResolver, galleryImageList, selectedIndex);
    }

    @Override
    protected View.OnLongClickListener createLongClickHandler(final ImageBeanListRecyclerViewAdapter viewAdapter, final int selectedIndex) {
        return new MarkForCompareLongClickHandler(viewAdapter, selectedIndex);
    }

    @Override
    protected void postBind(final ImageView imageView, final ImageBean imageAndTitle) {
        if (imageAndTitle.isInitialForCompare()) {
            imageView.setAlpha(0.5f);
        } else {
            imageView.setAlpha(1f);
        }
    }

    @Override
    protected ImageLayoutSizeParams getSizeParams(final Resources resources) {
        return new ImageLayoutSizeParamsForImageOnly(resources);
    }
}
