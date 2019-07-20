package ch.want.imagecompare.domain;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.MediaStore;

import ch.want.imagecompare.data.ImageBean;

public class ImageMediaStore {

    /**
     * {@code content://media/external/images/media, Store public data on the shared external storage.}
     * Note that MediaStore.Images.Media.INTERNAL_CONTENT_URI referes to data +private to the app+
     * {@code content://media/internal/images/media, Store private data on the device memory.}
     */
    static final Uri MEDIA_CONTENT_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    /**
     * <p>BUCKET_DISPLAY_NAME: a string like "Camera" which is the directory name of where an image or video is in</p>
     * <p>DATA: Path to the file on disk, eg. {@code /storage/emulated/0/Download/J0144366.JPG}</p>
     * <p>_ID: The internal image media identifier</p>
     */
    static final String[] PROJECTION = {MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
    static final String ORDER_BY = MediaStore.Images.Media.DATE_TAKEN + " DESC";
    final ContentResolver contentResolver;

    public ImageMediaStore(final ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public void delete(final ImageBean imageBean) {
        contentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.ImageColumns.DATA + "=?", new String[]{imageBean.getFileUri().getPath()});
    }
}
