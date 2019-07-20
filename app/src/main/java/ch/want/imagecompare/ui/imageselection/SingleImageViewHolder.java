package ch.want.imagecompare.ui.imageselection;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Optional;

import androidx.recyclerview.widget.RecyclerView;
import ch.want.imagecompare.R;
import ch.want.imagecompare.ui.thumbnails.ThumbnailViewHolder;

/**
 * Backing of 'view_image_withselectbutton.xml'
 */
class SingleImageViewHolder extends RecyclerView.ViewHolder implements ThumbnailViewHolder {

    private final ImageView imageView;
    private final CheckBox selectionCheckbox;

    SingleImageViewHolder(final View view) {
        super(view);
        imageView = view.findViewById(R.id.imageThumbnail);
        selectionCheckbox = view.findViewById(R.id.selectImageCheckbox);
    }

    @Override
    public ImageView getImageView() {
        return imageView;
    }

    @Override
    public Optional<TextView> getTitleView() {
        return Optional.empty();
    }

    CheckBox getCheckBox() {
        return selectionCheckbox;
    }
}
