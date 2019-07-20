package ch.want.imagecompare.ui;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import ch.want.imagecompare.R;

public class TransitionHandler {

    public static void switchToActivity(final Context context, final Intent intent) {
        final Bundle bndlAnimation = ActivityOptions.makeCustomAnimation(context, R.transition.enter_activity, R.transition.exit_activity).toBundle();
        context.startActivity(intent, bndlAnimation);
    }
}
