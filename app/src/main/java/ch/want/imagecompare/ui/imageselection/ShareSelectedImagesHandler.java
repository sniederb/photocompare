package ch.want.imagecompare.ui.imageselection;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import ch.want.imagecompare.R;
import ch.want.imagecompare.data.ImageBean;

class ShareSelectedImagesHandler implements View.OnClickListener {

    private static final int MAX_SHARED_IMAGES_FOR_TRANSACTION = 50;
    private final Activity sourceActivity;
    private final List<ImageBean> imagesBeans;

    ShareSelectedImagesHandler(final Activity sourceActivity, final List<ImageBean> imagesBeans) {
        this.sourceActivity = sourceActivity;
        this.imagesBeans = imagesBeans;
    }

    /**
     * Share images by choosing intent. A few relevant points:
     * <ul>
     * <li>EXTRA_STREAM expects a "content://" URI ("file://" works on Android 6.0 and older)</li>
     * <li>In order to temporarily grant access to a content URI, there are several options:
     * <ol>
     * <li>Use Context.grantUriPermission(package, Uri, mode_flags)</li>
     * <li>Put the content URI in an Intent by calling setData() (but "Upload to Photos" crashes with this approach)</li>
     * <li>Call shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)</li>
     * </ol>
     * </li>
     * </ul>
     *
     * @param v
     */
    @Override
    public void onClick(final View v) {
        final ArrayList<Uri> imageUris = new ArrayList<>();
        for (final ImageBean selectedImage : ImageBean.getSelectedImageBeans(imagesBeans)) {
            imageUris.add(selectedImage.getContentUri());
        }
        if (imageUris.size() < MAX_SHARED_IMAGES_FOR_TRANSACTION) {
            startIntent(imageUris);
        } else {
            showConfirmationDialog(imageUris);
        }
    }

    private void startIntent(final ArrayList<Uri> imageUris) {
        final Intent shareIntent = new Intent();
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.setType("image/*");
        if (imageUris.size() == 1) {
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUris.get(0));
        } else {
            shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        }
        sourceActivity.startActivity(Intent.createChooser(shareIntent, sourceActivity.getResources().getText(R.string.share_images_intent)));
    }

    /**
     * Launching an Intent with a large amount of data can cause a TransactionTooLargeException. This dialog
     * warns the user that the app might crash
     *
     * @param imageUris
     */
    private void showConfirmationDialog(final ArrayList<Uri> imageUris) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(sourceActivity);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int id) {
                startIntent(imageUris);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        final String message = String.format(sourceActivity.getString(R.string.shareselected_largetx_confirmation), imageUris.size());
        builder.setMessage(message);
        builder.setTitle(R.string.shareselected_largetx_confirmation_title);
        builder.show();
    }
}
