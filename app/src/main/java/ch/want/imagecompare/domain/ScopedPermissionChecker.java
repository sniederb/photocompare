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
 * {@link PermissionChecker} for Android 11 (API 30) and higher.
 */
@RequiresApi(api = Build.VERSION_CODES.R)
public class ScopedPermissionChecker implements PermissionChecker {

    private final String checkedReadPermission;
    private final String[] requestReadPermissions;
    private final Activity activity;
    private final int permissionCheckCode;
    private boolean alreadyAskedBefore;

    public ScopedPermissionChecker(final Activity activity, final int permissionCheckCode) {
        this.activity = activity;
        this.permissionCheckCode = permissionCheckCode;
        this.alreadyAskedBefore = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            this.checkedReadPermission = Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED;
            this.requestReadPermissions = new String[]{this.checkedReadPermission, Manifest.permission.READ_MEDIA_IMAGES};
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
            this.checkedReadPermission = Manifest.permission.READ_MEDIA_IMAGES;
            this.requestReadPermissions = new String[]{this.checkedReadPermission};
        } else {
            this.checkedReadPermission = Manifest.permission.READ_EXTERNAL_STORAGE;
            this.requestReadPermissions = new String[]{this.checkedReadPermission};
        }
    }

    @Override
    public boolean hasPermissions() {
        return ContextCompat.checkSelfPermission(activity.getApplicationContext(), checkedReadPermission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * From API 34 onwards, selecting "Select photos" for access results in the app <strong>not</strong> having
     * READ_MEDIA_IMAGES access, but READ_MEDIA_VISUAL_USER_SELECTED is granted.
     */
    @Override
    public boolean supportsReselect() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE &&
                ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void askNicely(boolean forceCheck) {
        if (forceCheck) {
            alreadyAskedBefore = false;
        }
        if (alreadyAskedBefore && shouldExplanationBeShownToUser()) {
            showExplanation();
        } else {
            requestPermissions();
        }
    }

    private boolean shouldExplanationBeShownToUser() {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, checkedReadPermission);
    }

    private void requestPermissions() {
        alreadyAskedBefore = true;
        ActivityCompat.requestPermissions(activity, requestReadPermissions, permissionCheckCode);
    }

    private void showExplanation() {
        new AlertDialog.Builder(activity) //
                .setPositiveButton(android.R.string.ok, null) //
                .setMessage(R.string.permissions_explanation) //
                .setTitle(R.string.permissions_title) //
                .show();
    }
}
