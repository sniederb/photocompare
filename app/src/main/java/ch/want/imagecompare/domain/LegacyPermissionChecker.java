package ch.want.imagecompare.domain;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import ch.want.imagecompare.R;

public class LegacyPermissionChecker implements PermissionChecker {

    private final Activity activity;
    private final int permissionCheckCode;
    private boolean alreadyAskedBefore;

    public LegacyPermissionChecker(final Activity activity, final int permissionCheckCode) {
        this.activity = activity;
        this.permissionCheckCode = permissionCheckCode;
        alreadyAskedBefore = false;
    }

    @Override
    public boolean hasPermissions() {
        return ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public boolean supportsReselect() {
        return false;
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
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) || //
                ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private void requestPermissions() {
        alreadyAskedBefore = true;
        // below android 11, note that WRITE_EXTERNAL_STORAGE implies READ_EXTERNAL_STORAGE
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, permissionCheckCode);
    }

    private void showExplanation() {
        new AlertDialog.Builder(activity) //
                .setPositiveButton(android.R.string.ok, null) //
                .setMessage(R.string.permissions_explanation) //
                .setTitle(R.string.permissions_title) //
                .show();
    }
}
