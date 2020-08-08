package ch.want.imagecompare.ui;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationWithProgress implements ProgressCallback {
    private final NotificationManagerCompat notificationManager;
    private final NotificationCompat.Builder builder;

    private final int notificationId;
    private int maxProgress;

    NotificationWithProgress(final NotificationManagerCompat notificationManager, final NotificationCompat.Builder builder, final int notificationId) {
        this.notificationManager = notificationManager;
        this.builder = builder;
        this.notificationId = notificationId;
    }

    @Override
    public void starting(final int maxProgress) {
        this.maxProgress = maxProgress;
        builder.setProgress(maxProgress, 0, false);
        notificationManager.notify(notificationId, builder.build());
    }

    @Override
    public void progress(final int deletedImagesCount) {
        builder.setProgress(maxProgress, deletedImagesCount, false);
        notificationManager.notify(notificationId, builder.build());
    }

    @Override
    public void finished() {
        notificationManager.cancel(notificationId);
    }
}
