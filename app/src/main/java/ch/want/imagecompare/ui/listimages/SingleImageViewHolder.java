package ch.want.imagecompare.ui.listimages;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Optional;

import androidx.recyclerview.widget.RecyclerView;
import ch.want.imagecompare.R;
import ch.want.imagecompare.ui.thumbnails.ThumbnailViewHolder;

/**
 * Backing of 'view_image_and_titletle.xml'
 */
class SingleImageViewHolder extends RecyclerView.ViewHolder implements ThumbnailViewHolder {

    private final ImageView imageView;

    SingleImageViewHolder(final View view) {
        super(view);
        imageView = view.findViewById(R.id.imageThumbnail);
    }

    @Override
    public ImageView getImageView() {
        return imageView;
    }

    @Override
    public Optional<TextView> getTitleView() {
        return Optional.empty();
    }
}
