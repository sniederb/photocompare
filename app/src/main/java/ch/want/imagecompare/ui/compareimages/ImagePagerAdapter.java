package ch.want.imagecompare.ui.compareimages;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;

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
    private static final int MAX_PHOTOVIEW_ZOOM = 10;
    private static final int ID_OFFSET = 100000;
    private CrossViewEventHandler matrixChangeListener;
    private ZoomPanRestoreHandler zoomPanHandler;

    private final ArrayList<ImageBean> galleryImageList;
    private final ConcurrentMap<Integer, PhotoView> highResPositions = new ConcurrentHashMap<>();
    private Integer futureHighResIndex;

    public ImagePagerAdapter(final ArrayList<ImageBean> galleryImageList) {
        this.galleryImageList = galleryImageList;
    }

    void setMatrixListener(final CrossViewEventHandler matrixChangeListener) {
        this.matrixChangeListener = matrixChangeListener;
    }

    void setZoomPanRestoreHandler(final ZoomPanRestoreHandler zoomPanHandler) {
        this.zoomPanHandler = zoomPanHandler;
    }

    @Override
    public int getCount() {
        return galleryImageList.size();
    }

    @Override
    public View instantiateItem(final ViewGroup container, final int position) {
        final PhotoView photoView = buildPhotoView(container.getContext(), position);
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
     *
     * @param onScreenPhotoView
     * @param position
     */
    void enableHighResolution(final PhotoView onScreenPhotoView, final int position) {
        switchOffscreenHighResToLowRes(position);
        if (onScreenPhotoView != null) {
            matrixChangeListener.disableMatrixListener();
            loadHighResImage(onScreenPhotoView, position);
        } else {
            // we get here when instantiating the entire view, and instantiateItem() hasn't run yet
            futureHighResIndex = position;
        }
    }

    private void switchOffscreenHighResToLowRes(final int excludePosition) {
        final Iterator<Map.Entry<Integer, PhotoView>> entryIt = highResPositions.entrySet().iterator();
        while (entryIt.hasNext()) {
            final Map.Entry<Integer, PhotoView> entry = entryIt.next();
            if (entry.getKey() != excludePosition) {
                loadLowResImage(entry.getValue(), entry.getKey());
                entryIt.remove();
            }
        }
    }

    private void loadLowResImage(final PhotoView photoView, final int position) {
        photoView.setOnMatrixChangeListener(null);
        photoView.removeOnLayoutChangeListener(zoomPanHandler);
        Glide.with(photoView.getContext())//
                .load(galleryImageList.get(position).getFileUri())//
                .dontAnimate() //
                .centerCrop() //
                .into(photoView);
    }

    private void loadHighResImage(final PhotoView photoView, final int position) {
        photoView.setOnMatrixChangeListener(matrixChangeListener);
        photoView.addOnLayoutChangeListener(zoomPanHandler);
        zoomPanHandler.resetImageResourceState();
        Glide.with(photoView.getContext())//
                .load(galleryImageList.get(position).getFileUri())//
                .dontAnimate() //
                // tell Glide to load full image, so zoom will look ok
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)//
                .centerCrop() //
                .listener(zoomPanHandler)
                // reduce footprint as much as possible
                .diskCacheStrategy(DiskCacheStrategy.NONE) //
                .skipMemoryCache(true) //
                .into(photoView);
        highResPositions.put(position, photoView);
    }

    private static PhotoView buildPhotoView(final Context context, final int position) {
        final PhotoView photoView = new PhotoView(context);
        // beware that we can't set a tag on PhotoView, as that will break Glide
        // "You must not call setTag() on a view Glide is targeting"
        photoView.setId(getPhotoViewId(position));
        photoView.setMaximumScale(MAX_PHOTOVIEW_ZOOM);
        return photoView;
    }

    static int getPhotoViewId(final int position) {
        return ID_OFFSET + position;
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        if (object instanceof View) {
            Glide.clear((View) object);
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
