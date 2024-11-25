package ch.want.imagecompare.ui.imageselection;

import android.app.AlertDialog;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import ch.want.imagecompare.R;
import ch.want.imagecompare.data.ImageBean;
import ch.want.imagecompare.domain.FileImageMediaResolver;

class DeleteUnselectedHandler extends AbstractDeleteFilesHandler {

    DeleteUnselectedHandler(final SelectedImagesActivity selectedImagesActivity, final FileImageMediaResolver mediaResolver, final ArrayList<ImageBean> galleryImageList) {
        super(selectedImagesActivity, mediaResolver, galleryImageList);
    }

    @Override
    void execute() {
        final Set<File> protectedDirectories = getProtectedDirectories();
        boolean hasCameraImages = getObsoleteImages().stream()
                .map(img -> img.getImageFile().getParentFile())
                .filter(Objects::nonNull)
                .anyMatch(protectedDirectories::contains);
        if (hasCameraImages) {
            showAlertDeleteUnselectedFromCameraFolder();
        } else {
            super.execute();
        }
    }

    @Override
    protected List<ImageBean> getObsoleteImages() {
        return ImageBean.getUnselectedImageBeans(galleryImageList);
    }

    private void showAlertDeleteUnselectedFromCameraFolder() {
        new AlertDialog.Builder(sourceActivity)
                .setTitle(R.string.delete_unselected_dcim_title)
                .setMessage(R.string.delete_unselected_dcim_message)
                .setNeutralButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * The "emulator" suggests that Camera will place images in {@link Environment#DIRECTORY_PICTURES},
     * real-life phones seem have them in a "Camera" folder <em>within</em> {@link Environment#DIRECTORY_DCIM}.
     */
    private Set<File> getProtectedDirectories() {
        final File dcimDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        final File cameraDirectory = new File(dcimDirectory, "Camera");
        final File picturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return Set.of(dcimDirectory, cameraDirectory, picturesDirectory);
    }
}
