package ch.want.imagecompare.ui.thumbnails;

import android.widget.ImageView;
import android.widget.TextView;

import java.util.Optional;

public interface ThumbnailViewHolder {
    /**
     * Get the ImageView (subclass). This will typically be a PhotoView instance.
     *
     * @return
     */
    ImageView getImageView();

    /**
     * Get the TextView, if there is any
     *
     * @return
     */
    Optional<TextView> getTitleView();
}
