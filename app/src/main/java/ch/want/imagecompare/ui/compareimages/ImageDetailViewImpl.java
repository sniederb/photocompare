package ch.want.imagecompare.ui.compareimages;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import androidx.exifinterface.media.ExifInterface;
import androidx.viewpager.widget.ViewPager;
import ch.want.imagecompare.R;
import ch.want.imagecompare.data.Dimension;
import ch.want.imagecompare.data.ImageBean;
import ch.want.imagecompare.domain.CrossViewEventHandler;
import ch.want.imagecompare.domain.ImageDetailView;

/**
 * This class provides access to one image + details such as exif text, select toggle etc.
 * Responsbilities are:
 * <ul>
 * <li>Send events to {@link CrossViewEventHandler} (ViewPager#OnPageChangeListener, ViewPagerAdapter#MatrixListener</li>
 * <li>Scroll image pager on clicks to nav left/right</li>
 * <li>Pass selection to {@link SelectImageHandler}</li>
 * </ul>
 * Note that actual glide/PhotoView handling happens in the {@link ImagePagerAdapter}
 */
public class ImageDetailViewImpl implements ImageDetailView {

    private static final int OFFSCREEN_PAGES_TO_KEEP = 1;
    private CrossViewEventHandler crossViewEventHandler;
    private final ZoomPanRestoreHandler zoomPanHandler;

    /*
     * The imageViewPager holds the state of the currently shown image. It relies
     * on the imageViewPagerAdapter to create views on-the-fly.
     */
    private final ViewPager imageViewPager;
    private final TextView exifTextView;
    private String exifData = "";
    private final CheckBox imageSelectionCheckbox;
    /*
     * The adapter is responsible for creating and deleting view objects. It does NOT
     * hold the state of the current item index (see imageViewPager for that)
     */
    private ImagePagerAdapter imageViewPagerAdapter;

    ImageDetailViewImpl(final View containerView) {
        imageViewPager = containerView.findViewById(R.id.viewpager);
        imageViewPager.setOffscreenPageLimit(OFFSCREEN_PAGES_TO_KEEP);
        exifTextView = containerView.findViewById(R.id.exifIso);
        imageSelectionCheckbox = containerView.findViewById(R.id.selectImageCheckbox);
        imageSelectionCheckbox.setOnClickListener(new SelectImageHandler(this));
        containerView.findViewById(R.id.left_nav).setOnClickListener(v -> imageViewPager.arrowScroll(View.FOCUS_LEFT));
        containerView.findViewById(R.id.left_nav).setOnLongClickListener(v -> {
            imageViewPager.setCurrentItem(crossViewEventHandler.getOtherImageIndex() - 1);
            return true;
        });
        containerView.findViewById(R.id.right_nav).setOnClickListener(v -> imageViewPager.arrowScroll(View.FOCUS_RIGHT));
        containerView.findViewById(R.id.right_nav).setOnLongClickListener(v -> {
            imageViewPager.setCurrentItem(crossViewEventHandler.getOtherImageIndex() + 1);
            return true;
        });
        zoomPanHandler = buildZoomPanRestoreHandler();
    }

    @Override
    public int getCurrentIndex() {
        return imageViewPager.getCurrentItem();
    }

    @Override
    public void setCrossImageEventHandler(final CrossViewEventHandler crossViewEventHandler) {
        if (imageViewPagerAdapter == null) {
            throw new IllegalStateException("Cannot add event handlers until image list was initialized. Call setImageList() before calling this method");
        }
        this.crossViewEventHandler = crossViewEventHandler;
        crossViewEventHandler.setImageDetailView(this);
        imageViewPager.addOnPageChangeListener(zoomPanHandler);
        imageViewPagerAdapter.setMatrixListener(crossViewEventHandler);
    }

    @Override
    public void setImageList(final ArrayList<ImageBean> galleryImageList) {
        imageViewPagerAdapter = new ImagePagerAdapter(galleryImageList);
        imageViewPagerAdapter.setZoomPanRestoreHandler(zoomPanHandler);
        imageViewPager.setAdapter(imageViewPagerAdapter);
    }

    @Override
    public ImageBean getCurrentImageBean() {
        return imageViewPagerAdapter.getImageBean(imageViewPager.getCurrentItem());
    }

    @Override
    public void notifyDataSetChanged() {
        imageViewPagerAdapter.notifyDataSetChanged();
    }

    /**
     * Passes an image index to the ViewPager. Note that indexes violating the underlying
     * image array will be quietly changed to be within the array boundaries.
     */
    @Override
    public void setCurrentIndex(final int imageIndex) {
        if (imageViewPager.getCurrentItem() == imageIndex) {
            preparePositionForDisplay(imageIndex);
        } else {
            // this will eventually trigger onNewPageSelected()
            imageViewPager.setCurrentItem(imageIndex, true);
        }
    }

    private void preparePositionForDisplay(final int position) {
        // do layout-insensitive stuff
        imageViewPagerAdapter.enableHighResolution(getOnScreenPhotoView(), position);
        imageSelectionCheckbox.setChecked(getCurrentImageBean().isSelected());
        // in case we'd ever want to change the drawable tint with code:
//        Drawable drawable = imageSelectionCheckbox.getButtonDrawable();
//        drawable = DrawableCompat.wrap(drawable);
//        DrawableCompat.setTint(drawable, Color.LTGRAY);
        updateExifData();
    }

    @Override
    public void disableStateChangedListener() {
        crossViewEventHandler.disableCrossViewEvents();
    }

    @Override
    public void enableStateChangedListener() {
        crossViewEventHandler.enableCrossViewEvents();
    }

    @Override
    public PanAndZoomState getPanAndZoomState() {
        final SubsamplingScaleImageView currentPhoto = getOnScreenPhotoView();
        return new PanAndZoomState(currentPhoto.getScale(), currentPhoto.getCenter());
    }

    @Override
    public void setPanAndZoomState(final PanAndZoomState panAndZoomState) {
        final SubsamplingScaleImageView currentPhoto = getOnScreenPhotoView();
        currentPhoto.setScaleAndCenter(panAndZoomState.getScale(), panAndZoomState.getCenterPoint().orElse(null));
        updateExifTextView(currentPhoto.getScale());
        // as pan/zoom events are only passed for "origin = touch", we need to remember state here
        // for the sync'ed view where origin is "animation"
        zoomPanHandler.onPanOrZoomChanged(panAndZoomState);
    }

    @Override
    public void resetPanAndZoomState() {
        final SubsamplingScaleImageView currentPhoto = getOnScreenPhotoView();
        currentPhoto.resetScaleAndCenter();
        updateExifTextView(null);
        // beware that this will NOT trigger a pan/zoom event, so we need to reset
        // the handler manually
        zoomPanHandler.onPanOrZoomChanged(PanAndZoomState.DEFAULT);
    }

    @Override
    public Dimension getSourceDimension() {
        final SubsamplingScaleImageView currentPhoto = getOnScreenPhotoView();
        return new Dimension(currentPhoto.getSHeight(), currentPhoto.getSWidth());
    }

    @Override
    public void setCheckboxStyleDark(final boolean isDark) {
        // beware of the alpha of 0.7 defined in view_image_details.xml
        final int newColor = isDark ? Color.BLACK : Color.WHITE;
        imageSelectionCheckbox.setButtonTintList(ColorStateList.valueOf(newColor));
    }

    @Override
    public void setShowExif(final boolean showExifDetails) {
        if (showExifDetails) {
            exifTextView.setVisibility(View.VISIBLE);
        } else {
            exifTextView.setVisibility(View.GONE);
        }
    }

    private SubsamplingScaleImageView getOnScreenPhotoView() {
        return getPhotoView(imageViewPager.getCurrentItem());
    }

    private SubsamplingScaleImageView getPhotoView(final int position) {
        return imageViewPager.findViewById(ImagePagerAdapter.getPhotoViewId(position));
    }

    private ZoomPanRestoreHandler buildZoomPanRestoreHandler() {
        return new ZoomPanRestoreHandler() {

            @Override
            void onApplyPanAndZoomState(final PanAndZoomState targetPanAndZoomState) {
                getOnScreenPhotoView().setScaleAndCenter(targetPanAndZoomState.getScale(), targetPanAndZoomState.getCenterPoint().orElse(null));
                crossViewEventHandler.enableCrossViewEvents();
            }

            @Override
            void onNewPageSelected(final int position) {
                final boolean positionAccepted = crossViewEventHandler.onIntentForNewImagePage(position);
                if (positionAccepted) {
                    preparePositionForDisplay(position);
                }
            }
        };
    }

    @Override
    public void updateDynamicViewStateText(final float newScale) {
        // scale is a "source/view pixels ratio"
        updateExifTextView(newScale);
    }

    private void updateExifData() {
        try {
            final File imageFile = getCurrentImageBean().getImageFile();
            final ExifInterface exif = new ExifInterface(Objects.requireNonNull(getCurrentImageBean().getFileUri().getPath()));
            final long megaPixel = exif.getAttributeInt(ExifInterface.TAG_PIXEL_X_DIMENSION, 0) * (long) exif.getAttributeInt(ExifInterface.TAG_PIXEL_Y_DIMENSION, 0) / //
                    (1000 * 1000);
            exifData = String.format(Locale.ENGLISH, "%s: ƒ/%.1f %s %.0fmm ISO %s %dMP",//
                    imageFile.getName(), //
                    exif.getAttributeDouble(ExifInterface.TAG_F_NUMBER, 0d), //
                    formatShutterSpeed(exif.getAttributeDouble(ExifInterface.TAG_EXPOSURE_TIME, 0d)), //
                    exif.getAttributeDouble(ExifInterface.TAG_FOCAL_LENGTH, 0d), //
                    exif.getAttribute(ExifInterface.TAG_PHOTOGRAPHIC_SENSITIVITY),//
                    megaPixel);
        } catch (final IOException e) {
            exifData = e.getMessage();
        }
        updateExifTextView(null);
    }

    private void updateExifTextView(final Float newScale) {
        if (newScale == null) {
            exifTextView.setText(exifData);
        } else {
            exifTextView.setText(String.format(Locale.ENGLISH, "%s, %.0f%%", exifData, newScale * 100));
        }
    }

    private static String formatShutterSpeed(final double shutterSpeed) {
        if (shutterSpeed >= 1) {
            return Math.round(shutterSpeed * 10) / 10 + "s";
        }
        return "1/" + Math.round(1 / shutterSpeed) + "s";
    }
}


