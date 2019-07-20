package ch.want.imagecompare.ui.thumbnails;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ch.want.imagecompare.R;
import ch.want.imagecompare.data.ImageBean;

/**
 * A RecyclerView only creates minimum number of Views needed to fill the screen. And it works by reusing the old/created Views.
 * So that when you are scrolling down the View that hid during the scrolling to the top is removed and brought next to the last
 * visible View and added there. But since the View is currently bound with old data {@link #onBindViewHolder(RecyclerView.ViewHolder, int)}
 * is called again to ensure that the View is bound with only the correct data before it is rendered.
 * <p>
 * Similarly you'll notice that {@link #onCreateViewHolder(ViewGroup, int)} is only called the exact minimum number of Views it needs.
 *
 * @param <T>
 */
public abstract class ImageBeanListRecyclerViewAdapter<T extends RecyclerView.ViewHolder & ThumbnailViewHolder> extends RecyclerView.Adapter<T> {
    protected final List<ImageBean> galleryImageList;

    protected ImageBeanListRecyclerViewAdapter(final List<ImageBean> imageList) {
        galleryImageList = imageList;
    }

    public ImageBean getImageAndTitleBean(final int i) {
        return galleryImageList.get(i);
    }

    /**
     * Create as many views as needed to fill the screen
     *
     * @param viewGroup
     * @param i
     * @return
     */
    @NonNull
    @Override
    public T onCreateViewHolder(@NonNull final ViewGroup viewGroup, final int i) {
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(getLayoutForViewHolder(), viewGroup, false);
        adjustViewBasedOnScreenWidth(view);
        return createViewHolder(view);
    }

    /**
     * Get the layout id for the recycler view item (eg 'view_image_and_title')
     *
     * @return
     */
    protected abstract int getLayoutForViewHolder();

    protected abstract T createViewHolder(final View view);

    /**
     * Adjust the size of the layout holding the image card plus - optionally - text to
     * the calculated view size.
     *
     * @param view
     */
    private void adjustViewBasedOnScreenWidth(final View view) {
        final ImageLayoutSizeParams imageLayoutSizeParams = getSizeParams(view.getResources());
        // layout params take pixel!
        final ViewGroup.LayoutParams lp = view.getLayoutParams();
        lp.height = imageLayoutSizeParams.getViewSizeInPixel();
        lp.width = imageLayoutSizeParams.getViewSizeInPixel();
        adjustCardview(view);
    }

    /**
     * Adjust the size of a single card to that of the image.
     *
     * @param view
     */
    private void adjustCardview(final View view) {
        final View cardview = view.findViewById(getThumbnailCardId());
        final ImageLayoutSizeParams imageLayoutSizeParams = getSizeParams(view.getResources());
        final ViewGroup.LayoutParams lp = cardview.getLayoutParams();
        lp.height = imageLayoutSizeParams.getImageSizeInPixel();
        lp.width = imageLayoutSizeParams.getImageSizeInPixel();
    }

    protected abstract int getThumbnailCardId();

    /**
     * Helper method to be called during {@link #onBindViewHolder(RecyclerView.ViewHolder, int)}
     *
     * @param viewHolder
     * @param i
     */
    protected void onBind(@NonNull final T viewHolder, final int i) {
        final ImageView imageView = viewHolder.getImageView();
        final ImageBean imageAndTitle = getImageAndTitleBean(i);
        Glide.with(imageView.getContext())//
                .load(imageAndTitle.getFileUri())//
                .diskCacheStrategy(DiskCacheStrategy.RESULT) //
                .placeholder(R.mipmap.ic_image_placeholder)//
                .centerCrop() //
                .into(imageView);
        imageView.setOnClickListener(createClickHandler(galleryImageList, i));
        final View.OnLongClickListener longClickListener = createLongClickHandler(this, i);
        if (longClickListener != null) {
            imageView.setOnLongClickListener(longClickListener);
        }
        postBind(imageView, imageAndTitle);
    }

    protected void postBind(final ImageView imageView, final ImageBean imageAndTitle) {
    }

    protected abstract View.OnClickListener createClickHandler(ImageBean imageAndTitle);

    protected abstract View.OnLongClickListener createLongClickHandler(ImageBeanListRecyclerViewAdapter viewAdapter, final int selectedIndex);

    protected View.OnClickListener createClickHandler(final List<ImageBean> galleryImageList, final int selectedIndex) {
        final ImageBean imageAndTitle = getImageAndTitleBean(selectedIndex);
        return createClickHandler(imageAndTitle);
    }

    protected abstract ImageLayoutSizeParams getSizeParams(final Resources resources);

    @Override
    public int getItemCount() {
        return galleryImageList == null ? 0 : galleryImageList.size();
    }
}
