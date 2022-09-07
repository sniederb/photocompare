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

    private final String readPermission;
    private final Activity activity;
    private final int permissionCheckCode;
    private boolean alreadyAskedBefore;

    public ScopedPermissionChecker(final Activity activity, final int permissionCheckCode) {
        this.activity = activity;
        this.permissionCheckCode = permissionCheckCode;
        this.alreadyAskedBefore = false;
        this.readPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ? Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.READ_EXTERNAL_STORAGE;
    }

    @Override
    public boolean hasPermissions() {
        return ContextCompat.checkSelfPermission(activity.getApplicationContext(), readPermission) == PackageManager.PERMISSION_GRANTED;
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
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, readPermission);
    }

    private void requestPermissions() {
        alreadyAskedBefore = true;
        ActivityCompat.requestPermissions(activity, new String[]{readPermission}, permissionCheckCode);
    }

    private void showExplanation() {
        new AlertDialog.Builder(activity) //
                .setPositiveButton(android.R.string.ok, null) //
                .setMessage(R.string.permissions_explanation) //
                .setTitle(R.string.permissions_title) //
                .show();
    }
}
