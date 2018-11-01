package poc.android.com.qrtsecurity.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.Map;

import poc.android.com.qrtsecurity.AppController;
import poc.android.com.qrtsecurity.Models.NotificationModel;
import poc.android.com.qrtsecurity.Models.ResponderModel;
import poc.android.com.qrtsecurity.R;
import poc.android.com.qrtsecurity.activities.ActivateDutyActivity;
import poc.android.com.qrtsecurity.activities.HomeActivity;
import poc.android.com.qrtsecurity.utils.AppPreferencesHandler;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String dataKey = "NOTIFICATION_DATA";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        sendNotification(remoteMessage.getNotification().getBody());

    }

    private void sendNotification(String messageBody) {

        NotificationModel data = new Gson().fromJson(messageBody, NotificationModel.class);

        Intent intent = new Intent(this, ActivateDutyActivity.class);
        intent.putExtra(dataKey, messageBody);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, AppController.CHANNEL_ID_QRT)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setChannelId(AppController.CHANNEL_ID_QRT)
                .setContentTitle("QRT-Security")
                .setContentText(String.format(getString(R.string.notification_message), data.getPassengerName()))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        notificationManager.notify(1001, notificationBuilder.build());
    }
}

