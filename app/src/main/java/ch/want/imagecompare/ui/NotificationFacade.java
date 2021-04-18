package ch.want.imagecompare.ui;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import java.util.Random;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import ch.want.imagecompare.R;

public class NotificationFacade {

    private static final String CHANNEL_ID = "ch.want.imagecompare.DeleteFilesChannel";
    private static final Random RND = new Random();
    private static boolean channelCreated = false;

    public static NotificationWithProgress createNotification(final Context context) {
        ensureChannelIsRegistered(context);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)//
                .setSmallIcon(R.drawable.ic_whiteeagle)//
                .setContentTitle(context.getString(R.string.channel_filedelete_title))//
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)//
                .setProgress(0, 0, true);
        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        return new NotificationWithProgress(notificationManager, builder, RND.nextInt())//
                .withLocalBroadCast(context);
    }

    private static void ensureChannelIsRegistered(final Context context) {
        if (!channelCreated) {
            createNotificationChannel(context);
        }
    }

    /**
     * Straight out of https://developer.android.com/training/notify-user/build-notification
     */
    private static synchronized void createNotificationChannel(final Context context) {
        if (!channelCreated && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // these values are reflected in the app settings -> notifications
            final CharSequence name = context.getString(R.string.channel_filedelete_name);
            final String description = context.getString(R.string.channel_filedelete_description);
            final NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_LOW);
            channel.setDescription(description);
            context.getSystemService(NotificationManager.class)//
                    .createNotificationChannel(channel);
        }
        channelCreated = true;
    }
}
