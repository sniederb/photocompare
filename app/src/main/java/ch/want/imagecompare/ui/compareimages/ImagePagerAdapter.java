package ch.want.imagecompare.ui.compareimages;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import androidx.viewpager.widget.PagerAdapter;
import ch.want.imagecompare.data.ImageBean;
import ch.want.imagecompare.domain.CrossViewEventHandler;

/**
 * Adapter for paging through PhotoView instances. Beware that the ViewPager will keep one page
 * on each side of the current selection "off screen". Thus instantiation isn't equivalent to
 * "showing to the user"
 */
class ImagePagerAdapter extends PagerAdapter {

    private static final int ID_OFFSET = 100000;
    private CrossViewEventHandler matrixChangeListener;
    private ZoomPanRestoreHandler zoomPanHandler;
    private final ImageViewListener imageViewListener = new ImageViewListener();

    private final ArrayList<ImageBean> galleryImageList;
    private final ConcurrentMap<Integer, SubsamplingScaleImageView> highResPositions = new ConcurrentHashMap<>();
    private Integer futureHighResIndex;

    ImagePagerAdapter(final ArrayList<ImageBean> galleryImageList) {
        this.galleryImageList = galleryImageList;
    }

    void setMatrixListener(final CrossViewEventHandler matrixChangeListener) {
        this.matrixChangeListener = matrixChangeListener;
        imageViewListener.addListener(matrixChangeListener);
    }

    void setZoomPanRestoreHandler(final ZoomPanRestoreHandler zoomPanHandler) {
        this.zoomPanHandler = zoomPanHandler;
        imageViewListener.addListener(zoomPanHandler);
    }

    @Override
    public int getCount() {
        return galleryImageList.size();
    }

    @Override
    public View instantiateItem(final ViewGroup container, final int position) {
        final SubsamplingScaleImageView photoView = buildPhotoView(container.getContext(), position);
        container.addView(photoView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        if (futureHighResIndex != null && futureHighResIndex == position) {
            loadHighResImage(photoView, position);
        } else {
            loadLowResImage(photoView, position);
        }
        return photoView;
    }

    /**
     * Handler for selecting a certain page position. This will switch the image to high resolution,
     * and attach the {@link #matrixChangeListener} to the PhotoView instance
     */
    void enableHighResolution(final SubsamplingScaleImageView onScreenPhotoView, final int position) {
        switchOffscreenHighResToLowRes(position);
        if (onScreenPhotoView != null) {
            matrixChangeListener.disableCrossViewEvents();
            loadHighResImage(onScreenPhotoView, position);
            // note that matrixChangeListener gets re-enabled by "image ready" event
        } else {
            // we get here when instantiating the entire view, and instantiateItem() hasn't run yet
            futureHighResIndex = position;
        }
    }

    private void switchOffscreenHighResToLowRes(final int excludePosition) {
        final Iterator<Map.Entry<Integer, SubsamplingScaleImageView>> entryIt = highResPositions.entrySet().iterator();
        while (entryIt.hasNext()) {
            final Map.Entry<Integer, SubsamplingScaleImageView> entry = entryIt.next();
            if (entry.getKey() != excludePosition) {
                loadLowResImage(entry.getValue(), entry.getKey());
                entryIt.remove();
            }
        }
    }

    private void loadLowResImage(final SubsamplingScaleImageView photoView, final int position) {
        photoView.setOnImageEventListener(null);
        photoView.setOnStateChangedListener(null);
        photoView.setImage(ImageSource.uri(galleryImageList.get(position).getFileUri()));
    }

    private void loadHighResImage(final SubsamplingScaleImageView photoView, final int position) {
        // register event handlers *before* setting image, so get the onReady event
        photoView.setOnImageEventListener(imageViewListener);
        photoView.setOnStateChangedListener(imageViewListener);
        zoomPanHandler.resetImageResourceState();
        photoView.setImage(ImageSource.uri(galleryImageList.get(position).getFileUri()));
        highResPositions.put(position, photoView);
    }

    private static SubsamplingScaleImageView buildPhotoView(final Context context, final int position) {
        final SubsamplingScaleImageView photoView = new SubsamplingScaleImageView(context);
        photoView.setOrientation(SubsamplingScaleImageView.ORIENTATION_USE_EXIF);
        // beware that we can't set a tag on PhotoView, as that will break Glide
        // "You must not call setTag() on a view Glide is targeting"
        photoView.setId(getPhotoViewId(position));
        return photoView;
    }

    static int getPhotoViewId(final int position) {
        return ID_OFFSET + position;
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        if (object instanceof View) {
            container.removeView((View) object);
        }
    }

    @Override
    public boolean isViewFromObject(final View view, final Object object) {
        return view == object;
    }

    ImageBean getImageBean(final int index) {
        return galleryImageList.get(index);
    }
}
