package ch.want.imagecompare.domain;

import android.content.ContentResolver;
import android.net.Uri;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FolderImageMediaResolver {

    private final ContentResolver contentResolver;
    private final boolean sortNewToOld;
    private final Map<String, Uri> targetFolderMap = new HashMap<>();

    public FolderImageMediaResolver(final ContentResolver contentResolver, final boolean sortNewToOld) {
        this.contentResolver = contentResolver;
        this.sortNewToOld = sortNewToOld;
    }

    public Map<String, Uri> execute() {
        new ImageMediaQueryStore(contentResolver) {

            @Override
            public void doWithCursor(final String bucketPath, final String imageFilePath, final Uri imageContentUri) {
                if (!targetFolderMap.containsKey(bucketPath)) {
                    final File file = new File(imageFilePath);
                    if (file.exists()) {
                        targetFolderMap.put(bucketPath, Uri.fromFile(file));
                    }
                }
            }
        }//
                .setSortNewToOld(sortNewToOld)//
                .execute();
        return targetFolderMap;
    }
}
