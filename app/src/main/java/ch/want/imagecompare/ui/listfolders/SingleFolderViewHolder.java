package ch.want.imagecompare.ui.listfolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Optional;

import androidx.recyclerview.widget.RecyclerView;
import ch.want.imagecompare.R;
import ch.want.imagecompare.ui.thumbnails.ThumbnailViewHolder;

/**
 * Backing of 'layout/view_image_and_title.xml'
 */
class SingleFolderViewHolder extends RecyclerView.ViewHolder implements ThumbnailViewHolder {

    private final ImageView imageView;
    private final TextView titleView;

    SingleFolderViewHolder(final View view) {
        super(view);
        imageView = view.findViewById(R.id.imageThumbnail);
        titleView = view.findViewById(R.id.imageDisplayName);
    }

    @Override
    public ImageView getImageView() {
        return imageView;
    }

    @Override
    public Optional<TextView> getTitleView() {
        return Optional.of(titleView);
    }
}
