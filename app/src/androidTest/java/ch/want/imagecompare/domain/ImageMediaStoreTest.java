package ch.want.imagecompare.domain;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.want.imagecompare.BundleKeys;
import ch.want.imagecompare.TestSettings;
import ch.want.imagecompare.ui.listimages.ListImagesActivity;

@RunWith(AndroidJUnit4.class)
public class ImageMediaStoreTest {

    private static final Uri MEDIA_CONTENT_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    private static final String[] PROJECTION = {MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA,
            // DATE_TAKEN: CurrentTimeMillisLong
            MediaStore.Images.Media.DATE_TAKEN,
            // DATE_ADDED: CurrentTimeSecondsLong
            MediaStore.Images.Media.DATE_ADDED,
            // DATE_MODIFIED: CurrentTimeSecondsLong
            MediaStore.Images.Media.DATE_MODIFIED};

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.READ_EXTERNAL_STORAGE);

    @Test
    public void contentResolverQuery() {
        final ActivityScenario<ListImagesActivity> scenario = launchActivity();
        final List<String> mediaFiles = new ArrayList<>();
        scenario.onActivity(activity -> {
            final ContentResolver contentResolver = activity.getContentResolver();
            final Cursor cursor = contentResolver.query(MEDIA_CONTENT_URI, PROJECTION, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    final String bucketPath = cursor.getString(0);
                    final String imagePath = cursor.getString(1);
                    final long dateTaken = cursor.getLong(2);
                    final long dateAdded = cursor.getLong(3);
                    final long dateModified = cursor.getLong(4);
                    mediaFiles.add(String.format("%s - taken: %s, added: %s, modified: %s", imagePath, new Date(dateTaken).toString(),//
                            new Date(dateAdded * 1000).toString(), new Date(dateModified * 1000).toString()));
                }
                cursor.close();
            }
        });
        final String output = String.join("\n", mediaFiles);
        // note that output is an empty string if READ_EXTERNAL_STORAGE is *not* granted
        MatcherAssert.assertThat(output, CoreMatchers.containsString("taken"));
    }

    private static ActivityScenario<ListImagesActivity> launchActivity() {
        final Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ListImagesActivity.class);
        intent.putExtra(BundleKeys.KEY_IMAGE_FOLDER, TestSettings.DOWNLOAD_FOLDER);
        return ActivityScenario.launch(intent);
    }
}
