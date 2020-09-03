package ch.want.imagecompare.domain;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ch.want.imagecompare.data.ImageBean;

public class FileImageMediaQuery {

    private final ContentResolver contentResolver;
    private final String bucketPath;
    private final boolean sortNewToOld;
    private final List<ImageBean> galleryImageList = new ArrayList<>();

    public FileImageMediaQuery(final ContentResolver contentResolver, final String bucketPath, final boolean sortNewToOld) {
        this.contentResolver = contentResolver;
        this.bucketPath = bucketPath;
        this.sortNewToOld = sortNewToOld;
    }

    public List<ImageBean> execute() {
        new ImageMediaQuery(contentResolver, MediaStore.Images.Media.DATA + " like ?", new String[]{bucketPath + "%"}) {

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
        return galleryImageList;
    }
}
