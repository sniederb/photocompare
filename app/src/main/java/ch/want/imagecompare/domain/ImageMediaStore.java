package ch.want.imagecompare.domain;

import android.annotation.SuppressLint;
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
     * Note that Android has defined all these constants per API-29, but on different classes. Due to them being moved around,
     * the IDE will report an issue with availability only from API 29.
     */
    @SuppressLint("InlinedApi")
    static final String[] PROJECTION = {MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
    @SuppressLint("InlinedApi")
    static final String ORDER_BY_DATE_TAKEN_ASC = MediaStore.Images.ImageColumns.DATE_TAKEN + ", " + MediaStore.Images.ImageColumns.DATE_ADDED;
    @SuppressLint("InlinedApi")
    static final String ORDER_BY_DATE_TAKEN_DESC = MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC, " + MediaStore.Images.ImageColumns.DATE_ADDED + " DESC";
    final ContentResolver contentResolver;

    public ImageMediaStore(final ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public void delete(final ImageBean imageBean) {
        contentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.ImageColumns.DATA + "=?", new String[]{imageBean.getFileUri().getPath()});
    }
}
