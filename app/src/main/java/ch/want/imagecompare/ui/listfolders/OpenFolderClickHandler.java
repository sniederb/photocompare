package ch.want.imagecompare.ui.listfolders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import java.io.File;

import ch.want.imagecompare.BundleKeys;
import ch.want.imagecompare.ui.TransitionHandler;
import ch.want.imagecompare.ui.listimages.ListImagesActivity;

/**
 * Handle click to open {@link ListImagesActivity} specifically with a folder selection.
 */
class OpenFolderClickHandler implements View.OnClickListener {

    private final File imageDirectory;

    OpenFolderClickHandler(final File imageDirectory) {
        this.imageDirectory = imageDirectory;
    }

    @Override
    public void onClick(final View v) {
        final Context context = v.getContext();
        final Intent intent = new Intent(context, ListImagesActivity.class);
        intent.putExtra(BundleKeys.KEY_IMAGE_FOLDER, imageDirectory.getAbsolutePath());
        TransitionHandler.switchToActivity((Activity) context, intent);
    }
}
