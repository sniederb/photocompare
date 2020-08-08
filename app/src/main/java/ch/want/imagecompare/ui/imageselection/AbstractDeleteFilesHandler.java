package ch.want.imagecompare.ui.imageselection;

import android.content.Context;
import android.content.Intent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NavUtils;
import ch.want.imagecompare.R;
import ch.want.imagecompare.data.ImageBean;
import ch.want.imagecompare.domain.ImageMediaStore;
import ch.want.imagecompare.ui.NotificationFacade;
import ch.want.imagecompare.ui.NotificationWithProgress;
import ch.want.imagecompare.ui.ProgressCallback;
import ch.want.imagecompare.ui.listfolders.ListAllImageFoldersActivity;

abstract class AbstractDeleteFilesHandler {

    private final SelectedImagesActivity sourceActivity;
    protected final ArrayList<ImageBean> galleryImageList;

    AbstractDeleteFilesHandler(final SelectedImagesActivity selectedImagesActivity, final ArrayList<ImageBean> galleryImageList) {
        sourceActivity = selectedImagesActivity;
        this.galleryImageList = galleryImageList;
    }

    void execute() {
        final List<ImageBean> obsoleteImages = getObsoleteImages();
        if (!obsoleteImages.isEmpty()) {
            final File firstImage = obsoleteImages.get(0).getImageFile();
            showDeleteConfirmationDialog(obsoleteImages, firstImage.getParentFile().getName());
        }
    }

    protected abstract List<ImageBean> getObsoleteImages();

    private void showDeleteConfirmationDialog(final List<ImageBean> obsoleteImages, final String folderName) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(sourceActivity);
        builder.setPositiveButton(android.R.string.ok, (dialog, id) -> {
            startDeletingObsoleteFiles(obsoleteImages);
            navigateToFolderSelection();
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        final String message = String.format(sourceActivity.getString(R.string.delete_confirmation), obsoleteImages.size(), folderName);
        builder.setMessage(message);
        builder.setTitle(R.string.delete_confirmation_title);
        builder.show();
    }

    private void startDeletingObsoleteFiles(final List<ImageBean> obsoleteImages) {
        final NotificationWithProgress notificationWithProgress = NotificationFacade.createNotification(sourceActivity);
        Executors.newSingleThreadExecutor().execute(//
                new DeleteFilesRunnable(obsoleteImages, sourceActivity, galleryImageList, notificationWithProgress)//
        );
    }

    private void navigateToFolderSelection() {
        final Intent intent = new Intent(sourceActivity, ListAllImageFoldersActivity.class);
        NavUtils.navigateUpTo(sourceActivity, intent);
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
