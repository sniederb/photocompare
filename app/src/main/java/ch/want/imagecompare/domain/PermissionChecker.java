package ch.want.imagecompare.domain;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import ch.want.imagecompare.R;

public class PermissionChecker {

    private final Activity activity;
    private final int permissionCheckCode;
    private boolean alreadyAskedBefore;

    public PermissionChecker(final Activity activity, final int permissionCheckCode) {
        this.activity = activity;
        this.permissionCheckCode = permissionCheckCode;
        alreadyAskedBefore = false;
    }

    public boolean hasPermissions() {
        return hasWritePermission() && hasReadPermission();
    }

    public void askNicely() {
        if (alreadyAskedBefore && shouldExplanationBeShownToUser()) {
            showExplanation();
        } else {
            requestPermissions();
        }
    }

    private boolean hasWritePermission() {
        return ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasReadPermission() {
        return ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean shouldExplanationBeShownToUser() {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) || //
                ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private void requestPermissions() {
        alreadyAskedBefore = true;
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, permissionCheckCode);
    }

    private void showExplanation() {
        new AlertDialog.Builder(activity) //
                .setPositiveButton(android.R.string.ok, null) //
                .setMessage(R.string.permissions_explanation) //
                .setTitle(R.string.permissions_title) //
                .show();
    }
}
