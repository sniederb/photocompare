package ch.want.imagecompare.ui.listimages;

import android.app.Activity;
import android.content.Intent;

import androidx.core.app.NavUtils;
import ch.want.imagecompare.domain.FileImageMediaResolver;

class BackToPoolSelectionTransition {

    private final Activity sourceActivity;
    private final FileImageMediaResolver mediaResolver;

    BackToPoolSelectionTransition(final Activity sourceActivity, final FileImageMediaResolver mediaResolver) {
        this.sourceActivity = sourceActivity;
        this.mediaResolver = mediaResolver;
    }

    void execute() {
        final Intent upIntent = sourceActivity.getParentActivityIntent();
        assert upIntent != null;
        mediaResolver.putToIntent(upIntent);
        NavUtils.navigateUpTo(sourceActivity, upIntent);
    }
}
