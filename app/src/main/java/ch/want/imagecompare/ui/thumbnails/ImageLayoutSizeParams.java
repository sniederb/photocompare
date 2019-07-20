package ch.want.imagecompare.ui.thumbnails;

public interface ImageLayoutSizeParams {

    /**
     * Get the height/width of a view for a single image. This will typically be a LinearLayout, possibly with some padding
     *
     * @return
     */
    int getViewSizeInPixel();

    /**
     * Get the height/width for the image within the single layout.
     *
     * @return
     */
    int getImageSizeInPixel();
}
