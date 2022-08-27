package ch.want.imagecompare.ui.imageselection;

import android.content.res.Resources;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import ch.want.imagecompare.R;
import ch.want.imagecompare.data.ImageBean;
import ch.want.imagecompare.ui.ImageLayoutSizeParamsForImageOnly;
import ch.want.imagecompare.ui.thumbnails.ImageBeanListRecyclerViewAdapter;
import ch.want.imagecompare.ui.thumbnails.ImageLayoutSizeParams;

public class ListSelectionThumbnailsAdapter extends ImageBeanListRecyclerViewAdapter<SingleImageViewHolder> {

    private final List<ImageBean> selectedImageList = new ArrayList<>();

    ListSelectionThumbnailsAdapter(final List<ImageBean> imageList) {
        super(imageList);
        selectedImageList.addAll(ImageBean.getSelectedImageBeans(imageList));
    }

    @Override
    public void onBindViewHolder(@NonNull final SingleImageViewHolder viewHolder, final int i) {
        super.onBind(viewHolder, i);
        final ImageBean imageAndTitle = getImageAndTitleBean(i);
        final CheckBox checkbox = viewHolder.getCheckBox();
        checkbox.setOnClickListener(new ToggleImageSelectionHandler(imageAndTitle));
        checkbox.setChecked(imageAndTitle.isSelected());
    }

    @Override
    public ImageBean getImageAndTitleBean(final int i) {
        return selectedImageList.get(i);
    }

    @Override
    public int getItemCount() {
        return selectedImageList.size();
    }

    /**
     * As this adapter is based on a filtered data set, but sure to call this method
     * instead of {@link #notifyDataSetChanged()} } directly
     */
    void notifySelectionChanged() {
        selectedImageList.clear();
        selectedImageList.addAll(ImageBean.getSelectedImageBeans(galleryImageList));
        notifyDataSetChanged();
    }

    @Override
    protected int getLayoutForViewHolder() {
        return R.layout.view_image_withselectbutton;
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
        return null;
    }

    @Override
    protected View.OnLongClickListener createLongClickHandler(final ImageBeanListRecyclerViewAdapter viewAdapter, final int selectedIndex) {
        return null;
    }

    @Override
    protected ImageLayoutSizeParams getSizeParams(final Resources resources) {
        return new ImageLayoutSizeParamsForImageOnly(resources);
    }
}
