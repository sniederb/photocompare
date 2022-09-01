package ch.want.imagecompare.ui.imageselection;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import ch.want.imagecompare.R;
import ch.want.imagecompare.data.ImageBean;
import ch.want.imagecompare.domain.FileImageMediaResolver;
import ch.want.imagecompare.domain.ImageMediaStore;
import ch.want.imagecompare.ui.NotificationFacade;
import ch.want.imagecompare.ui.NotificationWithProgress;
import ch.want.imagecompare.ui.ProgressCallback;

abstract class AbstractDeleteFilesHandler {

    public static final int IMAGE_DELETED_ACTIONCODE = 43018;
    private final SelectedImagesActivity sourceActivity;
    protected final ArrayList<ImageBean> galleryImageList;
    private final FileImageMediaResolver mediaResolver;

    AbstractDeleteFilesHandler(final SelectedImagesActivity selectedImagesActivity, final FileImageMediaResolver mediaResolver, final ArrayList<ImageBean> galleryImageList) {
        sourceActivity = selectedImagesActivity;
        this.galleryImageList = galleryImageList;
        this.mediaResolver = mediaResolver;
    }

    void execute() {
        final List<ImageBean> obsoleteImages = getObsoleteImages();
        if (obsoleteImages.isEmpty()) {
            showNothingToDeleteDialog();
        } else {
            showDeleteConfirmationDialog(obsoleteImages);
        }
    }

    protected abstract List<ImageBean> getObsoleteImages();

    private void showDeleteConfirmationDialog(final List<ImageBean> obsoleteImages) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(sourceActivity);
        builder.setPositiveButton(android.R.string.ok, (dialog, id) -> {
            deleteObsoleteFiles(obsoleteImages);
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        final String message;
        if (mediaResolver.getImageDate() != null) {
            final DateFormat dateFormat = android.text.format.DateFormat.getMediumDateFormat(sourceActivity.getApplicationContext());
            message = String.format(sourceActivity.getString(R.string.delete_confirmation_date), obsoleteImages.size(), dateFormat.format(mediaResolver.getImageDate()));
        } else {
            message = String.format(sourceActivity.getString(R.string.delete_confirmation_folder), obsoleteImages.size(), mediaResolver.getImageFolder());
        }
        builder.setMessage(message);
        builder.setTitle(R.string.delete_confirmation_title);
        builder.show();
    }

    private void showNothingToDeleteDialog() {
        new AlertDialog.Builder(sourceActivity)
                .setPositiveButton(android.R.string.ok, (dialog, id) -> {
                })
                .setMessage(R.string.nothing_to_delete)
                .setTitle(R.string.nothing_to_delete_title)
                .show();
    }

    private void deleteObsoleteFiles(final List<ImageBean> obsoleteImages) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            startTrashRequest(obsoleteImages);
        } else {
            startDeletingObsoleteFiles(obsoleteImages);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void startTrashRequest(final List<ImageBean> obsoleteImages) {
        List<Uri> urisToModify = obsoleteImages.stream()
                .map(ImageBean::getContentUri)
                .collect(Collectors.toList());
        // Note: PendingIntents are designed that they can be launched from other applications, i.e. it isn't clear who should receive the result.
        // That's why startActivityForResult() is meaningless for PendingIntent.
        PendingIntent editPendingIntent = MediaStore.createDeleteRequest(sourceActivity.getApplicationContext().getContentResolver(), urisToModify);
        try {
            sourceActivity.startIntentSenderForResult(editPendingIntent.getIntentSender(), IMAGE_DELETED_ACTIONCODE, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException ex) {
            Log.w("startTrashRequest", "Failed to start delete request intent due to " + ex.getMessage());
        }
    }

    private void startDeletingObsoleteFiles(final List<ImageBean> obsoleteImages) {
        final NotificationWithProgress notificationWithProgress = NotificationFacade.createNotification(sourceActivity);
        Executors.newSingleThreadExecutor().execute(//
                new DeleteFilesRunnable(obsoleteImages, sourceActivity, galleryImageList, notificationWithProgress)//
        );
        sourceActivity.onActivityResult(IMAGE_DELETED_ACTIONCODE, Activity.RESULT_OK, null);
    }

    private static class DeleteFilesRunnable implements Runnable {

        private final ImageMediaStore mediaStore;
        private final List<ImageBean> obsoleteImages;
        private final ArrayList<ImageBean> galleryImageList;
        private final ProgressCallback callback;

        private DeleteFilesRunnable(final List<ImageBean> obsoleteImages, final Context context, final ArrayList<ImageBean> galleryImageList, final ProgressCallback callback) {
            mediaStore = new ImageMediaStore(context.getContentResolver());
            this.galleryImageList = galleryImageList;
            this.obsoleteImages = obsoleteImages;
            this.callback = callback;
        }

        @Override
        public void run() {
            callback.starting(obsoleteImages.size());
            try {
                int deletedImagesCount = 0;
                for (final ImageBean imageBean : obsoleteImages) {
                    final File imageFile = imageBean.getImageFile();
                    if (imageFile.delete()) {
                        mediaStore.delete(imageBean);
                        galleryImageList.remove(imageBean);
                    }
                    deletedImagesCount++;
                    callback.progress(deletedImagesCount);
                }
            } finally {
                callback.finished();
            }
        }
    }
}
