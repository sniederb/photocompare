package ch.want.imagecompare.domain;

import android.content.ContentResolver;
import android.net.Uri;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FolderImageMediaQuery {

    private final ContentResolver contentResolver;
    private final Map<String, Uri> targetFolderMap = new HashMap<>();

    public FolderImageMediaQuery(final ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public Map<String, Uri> execute() {
        new ImageMediaQuery(contentResolver) {

            @Override
            public void doWithCursor(final String bucketPath, final String imageFilePath, final Uri imageContentUri) {
                if (!targetFolderMap.containsKey(bucketPath)) {
                    final File file = new File(imageFilePath);
                    if (file.exists()) {
                        targetFolderMap.put(bucketPath, Uri.fromFile(file));
                    }
                }
            }
        }.execute();
        return targetFolderMap;
    }
}
