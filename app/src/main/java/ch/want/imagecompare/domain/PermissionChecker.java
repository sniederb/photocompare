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
        // TODO: uncomment once targetSdkVersion==30
//        if (SDK_INT >= Build.VERSION_CODES.R) {
//            return Environment.isExternalStorageManager();
//        }
        return ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public void askNicely() {
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
        // TODO: uncomment once targetSdkVersion==30
//        if (SDK_INT >= Build.VERSION_CODES.R) {
//            try {
//                final Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//                intent.addCategory("android.intent.category.DEFAULT");
//                intent.setData(Uri.parse(String.format("package:%s", activity.getApplicationContext().getPackageName())));
//                activity.startActivityForResult(intent, permissionCheckCode);
//            } catch (final Exception e) {
//                final Intent intent = new Intent();
//                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
//                activity.startActivityForResult(intent, permissionCheckCode);
//            }
//        } else {
        // below android 11, note that WRITE_EXTERNAL_STORAGE implies READ_EXTERNAL_STORAGE
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, permissionCheckCode);
//        }
    }

    private void showExplanation() {
        new AlertDialog.Builder(activity) //
                .setPositiveButton(android.R.string.ok, null) //
                .setMessage(R.string.permissions_explanation) //
                .setTitle(R.string.permissions_title) //
                .show();
    }
}
