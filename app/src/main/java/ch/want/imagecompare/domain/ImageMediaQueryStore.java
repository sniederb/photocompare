package ch.want.imagecompare.domain;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public abstract class ImageMediaQueryStore extends ImageMediaStore {

    private final String selectionExpression;
    private final String[] selectionArgs;
    private String orderBy = ORDER_BY_DATE_TAKEN_DESC;

    ImageMediaQueryStore(final ContentResolver contentResolver) {
        super(contentResolver);
        selectionExpression = null;
        selectionArgs = null;
    }

    ImageMediaQueryStore(final ContentResolver contentResolver, final String selectionExpression, final String[] selectionArgs) {
        super(contentResolver);
        this.selectionExpression = selectionExpression;
        this.selectionArgs = selectionArgs;
    }

    ImageMediaQueryStore setSortNewToOld(final boolean sortNewToOld) {
        orderBy = sortNewToOld ? ORDER_BY_DATE_TAKEN_DESC : ORDER_BY_DATE_TAKEN_ASC;
        return this;
    }

    public void execute() {
        final Cursor cursor = contentResolver.query(MEDIA_CONTENT_URI, PROJECTION, selectionExpression, selectionArgs, orderBy);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                final String bucketPath = cursor.getString(0);
                final String imagePath = cursor.getString(1);
                doWithCursor(bucketPath, imagePath, buildContentUri(cursor.getInt(2)));
            }
            cursor.close();
        }
    }

    private static Uri buildContentUri(final int mediaId) {
        return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Integer.toString(mediaId));
    }

    /**
     * @param bucketPath      Column MediaStore.Images.Media.BUCKET_DISPLAY_NAME, a string like "Camera" which is the directory name of
     *                        where an image or video is in
     * @param imageFilePath   Column MediaStore.Images.Media.DATA, Path to the file on disk, eg.
     *                        {@code /storage/emulated/0/Download/J0144366.JPG}
     * @param imageContentUri Something like {@code content://media/external/images/media/52}, where '52' will refer to the media ID
     *                        of the image
     */
    public abstract void doWithCursor(String bucketPath, String imageFilePath, Uri imageContentUri);
}
