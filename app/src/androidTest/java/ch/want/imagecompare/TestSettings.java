package ch.want.imagecompare;

import android.net.Uri;

import androidx.annotation.Nullable;

public class TestSettings {

    public static final String DOWNLOAD_FOLDER = "/storage/emulated/0/Download";
    public static final String SELECTED_IMAGE_1 = "J0091157.JPG";

    private TestSettings() {
    }

    public static Uri getUri(final String filename, @Nullable final String downloadSubfolder) {
        final String fullPath;
        if (downloadSubfolder == null) {
            fullPath = String.format("file://%s/%s", DOWNLOAD_FOLDER, filename);
        } else {
            fullPath = String.format("file://%s/%s/%s", DOWNLOAD_FOLDER, downloadSubfolder, filename);
        }
        return Uri.parse(fullPath);
    }

    public static Uri getUriSelectedImage1() {
        return getUri(SELECTED_IMAGE_1, "EOS77D");
    }
}
