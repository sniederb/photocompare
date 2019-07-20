package ch.want.imagecompare.ui.imageselection;

import android.content.DialogInterface;
import android.content.Intent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NavUtils;
import ch.want.imagecompare.R;
import ch.want.imagecompare.data.ImageBean;
import ch.want.imagecompare.domain.ImageMediaStore;
import ch.want.imagecompare.ui.listfolders.ListAllImageFoldersActivity;

class DeleteUnselectedHandler {

    private final SelectedImagesActivity sourceActivity;
    private final ArrayList<ImageBean> galleryImageList;

    DeleteUnselectedHandler(final SelectedImagesActivity selectedImagesActivity, final ArrayList<ImageBean> galleryImageList) {
        sourceActivity = selectedImagesActivity;
        this.galleryImageList = galleryImageList;
    }

    void execute() {
        final List<ImageBean> obsoleteImages = ImageBean.getUnselectedImageBeans(galleryImageList);
        if (!obsoleteImages.isEmpty()) {
            final File firstImage = obsoleteImages.get(0).getImageFile();
            showDeleteConfirmationDialog(obsoleteImages, firstImage.getParentFile().getName());
        }
    }

    private void showDeleteConfirmationDialog(final List<ImageBean> obsoleteImages, final String folderName) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(sourceActivity);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int id) {
                deleteUnSelected(obsoleteImages);
                navigateToFolderSelection();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        final String message = String.format(sourceActivity.getString(R.string.delete_unselected_confirmation), obsoleteImages.size(), folderName);
        builder.setMessage(message);
        builder.setTitle(R.string.delete_unselected_confirmation_title);
        builder.show();
    }

    private void deleteUnSelected(final List<ImageBean> obsoleteImages) {
        final ImageMediaStore mediaStore = new ImageMediaStore(sourceActivity.getContentResolver());
        for (final ImageBean imageBean : obsoleteImages) {
            final File imageFile = imageBean.getImageFile();
            imageFile.delete();
            mediaStore.delete(imageBean);
            galleryImageList.remove(imageBean);
        }
    }

    private void navigateToFolderSelection() {
        final Intent intent = new Intent(sourceActivity, ListAllImageFoldersActivity.class);
        NavUtils.navigateUpTo(sourceActivity, intent);
    }
}
