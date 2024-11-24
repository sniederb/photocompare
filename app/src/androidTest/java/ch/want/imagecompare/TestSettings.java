package ch.want.imagecompare;

import android.Manifest;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.test.rule.GrantPermissionRule;

public class TestSettings {

    public static final String DOWNLOAD_FOLDER = "/storage/emulated/0/Download";
    public static final String CAMERA_FOLDER = "/storage/emulated/0/Pictures";
    public static final String SELECTED_IMAGE_1 = "J0091157.JPG";

    private TestSettings() {
    }

    public static GrantPermissionRule permissionRule() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return GrantPermissionRule.grant(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.ACCESS_MEDIA_LOCATION);
        }
        return GrantPermissionRule.grant(Manifest.permission.READ_EXTERNAL_STORAGE);
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
