package ch.want.imagecompare.domain;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ch.want.imagecompare.BundleKeys;
import ch.want.imagecompare.data.ImageBean;

public class FileImageMediaResolver {

    private final ContentResolver contentResolver;
    private final String bucketPath;
    private final Date imageDate;
    private boolean sortNewToOld;
    private boolean useFilenamesForSort;

    public static FileImageMediaResolver create(final Activity activity, @Nullable final Bundle savedInstanceState) {
        final String imageFolder;
        final long imageDateTime;
        if (savedInstanceState == null) {
            final Intent intent = activity.getIntent();
            imageFolder = intent.getStringExtra(BundleKeys.KEY_IMAGE_FOLDER);
            imageDateTime = intent.getLongExtra(BundleKeys.KEY_IMAGE_DATE, -1);
        } else {
            imageFolder = savedInstanceState.getString(BundleKeys.KEY_IMAGE_FOLDER);
            imageDateTime = savedInstanceState.getLong(BundleKeys.KEY_IMAGE_DATE, -1);
        }
        final PhotoComparePreferences storedPrefs = new PhotoComparePreferences(activity);
        final boolean sortNewToOld = storedPrefs.isSortNewestFirst();
        final boolean useFilenamesForSort = storedPrefs.isFilenamesForSort();
        return new FileImageMediaResolver(activity.getContentResolver(),//
                imageDateTime < 0 ? null : new Date(imageDateTime), //
                imageFolder,//
                sortNewToOld,//
                useFilenamesForSort);
    }

    private FileImageMediaResolver(final ContentResolver contentResolver, final Date imageDate, final String bucketPath, final boolean sortNewToOld, final boolean useFilenamesForSort) {
        this.contentResolver = contentResolver;
        this.bucketPath = bucketPath;
        this.imageDate = imageDate;
        this.sortNewToOld = sortNewToOld;
        this.useFilenamesForSort = useFilenamesForSort;
    }

    public boolean isSortNewToOld() {
        return sortNewToOld;
    }

    public void setSortNewToOld(final boolean sortNewToOld) {
        this.sortNewToOld = sortNewToOld;
    }

    public boolean useFilenamesForSort() {
        return useFilenamesForSort;
    }

    public void setFilenamesForSort(final boolean useFilenamesForSort) {
        this.useFilenamesForSort = useFilenamesForSort;
    }

    public Date getImageDate() {
        return imageDate;
    }

    public String getImageFolder() {
        return bucketPath;
    }

    public List<ImageBean> execute() {
        final List<ImageBean> galleryImageList = new ArrayList<>();
        new ImageMediaQueryStore(contentResolver, getSelectExpression(), getSelectArguments()) {

            @Override
            public void doWithCursor(final String bucketPath, final String imageFilePath, final Uri imageContentUri) {
                final File file = new File(imageFilePath);
                if (file.exists()) {
                    galleryImageList.add(new ImageBean(file.getName(), file.lastModified(), Uri.fromFile(file), imageContentUri));
                }
            }
        }//
                .setSortNewToOld(sortNewToOld)//
                .execute();
        if (useFilenamesForSort) {
            sortByFilename(galleryImageList);
        }
        return galleryImageList;
    }

    public void saveInstanceState(@NonNull final Bundle savedInstanceState) {
        if (imageDate != null) {
            savedInstanceState.putLong(BundleKeys.KEY_IMAGE_DATE, imageDate.getTime());
        } else {
            savedInstanceState.putString(BundleKeys.KEY_IMAGE_FOLDER, bucketPath);
        }
    }

    public Intent putToIntent(final Intent intent) {
        if (imageDate != null) {
            intent.putExtra(BundleKeys.KEY_IMAGE_DATE, imageDate.getTime());
        } else {
            intent.putExtra(BundleKeys.KEY_IMAGE_FOLDER, bucketPath);
        }
        return intent;
    }

    private String getSelectExpression() {
        if (imageDate != null) {
            return MediaStore.MediaColumns.DATE_TAKEN + " >= ? and " + MediaStore.Images.ImageColumns.DATE_TAKEN + " <= ?";
        }
        return MediaStore.Images.ImageColumns.DATA + " like ?";
    }

    private String[] getSelectArguments() {
        if (imageDate != null) {
            // DATE_TAKEN = number of milliseconds since 1970-01-01T00:00:00Z
            final Date startOfDay = DateUtils.truncate(imageDate, java.util.Calendar.DATE);
            final Date endOfDay = DateUtils.ceiling(imageDate, java.util.Calendar.DATE);
            return new String[]{Long.toString(startOfDay.getTime()), Long.toString(endOfDay.getTime())};
        }
        return new String[]{bucketPath + "%"};
    }

    /**
     * Some apps such as "Canon Connect" completely mess up the "date taken" timestamp when copying data
     * from the camera to the phone. For such cases, sorting by filename is much more reliable.
     */
    private void sortByFilename(final List<ImageBean> galleryImageList) {
        Collections.sort(galleryImageList, sortNewToOld ? ImageBeanComparators.byDateDesc() : ImageBeanComparators.byDateAsc());
    }

    static class ImageBeanComparators {

        static Comparator<ImageBean> byImageName() {
            return (a, b) -> StringUtils.compare(a.getDisplayName(), b.getDisplayName(), true);
        }

        /**
         * File create date is not accessible via Android, and is questionable anyway. Downloading
         * images from a camera to the phone will result in files with a create date of when the
         * download occurred, not "image taken". Thus this method sorts by filename.
         */
        static Comparator<ImageBean> byDateAsc() {
            return byImageName();
        }

        static Comparator<ImageBean> byDateDesc() {
            return byDateAsc().reversed();
        }
    }
}
