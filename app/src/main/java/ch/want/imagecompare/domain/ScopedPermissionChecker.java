package ch.want.imagecompare.domain;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import ch.want.imagecompare.R;

/**
 * {@link PermissionChecker} for Android 11 and higher.
 */
@RequiresApi(api = Build.VERSION_CODES.R)
public class ScopedPermissionChecker implements PermissionChecker {

    private final Activity activity;
    private final int permissionCheckCode;
    private boolean alreadyAskedBefore;

    public ScopedPermissionChecker(final Activity activity, final int permissionCheckCode) {
        this.activity = activity;
        this.permissionCheckCode = permissionCheckCode;
        alreadyAskedBefore = false;
    }

    @Override
    public boolean hasPermissions() {
        // with Build.VERSION_CODES.TIRAMISU, switch this to READ_MEDIA_IMAGES
        return ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void askNicely() {
        if (alreadyAskedBefore && shouldExplanationBeShownToUser()) {
            showExplanation();
        } else {
            requestPermissions();
        }
    }

    private boolean shouldExplanationBeShownToUser() {
        // with Build.VERSION_CODES.TIRAMISU, switch this to READ_MEDIA_IMAGES
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private void requestPermissions() {
        alreadyAskedBefore = true;
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, permissionCheckCode);
    }

    private void showExplanation() {
        new AlertDialog.Builder(activity) //
                .setPositiveButton(android.R.string.ok, null) //
                .setMessage(R.string.permissions_explanation) //
                .setTitle(R.string.permissions_title) //
                .show();
    }
}
