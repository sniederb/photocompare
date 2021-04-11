package ch.want.imagecompare.ui.listfolders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import java.util.Date;

import ch.want.imagecompare.BundleKeys;
import ch.want.imagecompare.ui.TransitionHandler;
import ch.want.imagecompare.ui.listimages.ListImagesActivity;

class ApplyDateSelectionHandler {

    private final Date imageDate;

    ApplyDateSelectionHandler(final Date imageDate) {
        this.imageDate = imageDate;
    }

    void navigateToListImages(final View view) {
        if (imageDate == null) {
            return;
        }
        final Context context = view.getContext();
        final Intent intent = new Intent(context, ListImagesActivity.class);
        intent.putExtra(BundleKeys.KEY_IMAGE_DATE, imageDate.getTime());
        TransitionHandler.switchToActivity((Activity) context, intent);
    }
}
