package ch.want.imagecompare.ui;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

public class TransitionHandler {

    public static void switchToActivity(final Activity activity, final Intent intent) {
        final Bundle targetBundle = ActivityOptions.makeSceneTransitionAnimation(activity).toBundle();
        activity.startActivity(intent, targetBundle);
    }
}
