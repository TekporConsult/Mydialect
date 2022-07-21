package gd.rf.tekporconsult.mypronouncer.CallBacks;

import static gd.rf.tekporconsult.mypronouncer.service.OfflineService.continueTread;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;

import gd.rf.tekporconsult.mypronouncer.MainActivity;
import gd.rf.tekporconsult.mypronouncer.service.DatabaseAccess;

public class BroadCastReceiver  extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
        String unbind = intent.getStringExtra("unbind");
        if(unbind != null && unbind.equals("unbind")){
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.cancel(0);
            continueTread = false;
            MainActivity.isNotCompleted = false;
            MainActivity.isNotCanceled = false;
            Toast.makeText(context, "Download Paused", Toast.LENGTH_LONG).show();
            databaseAccess.setNotification(3);
        }

    }
}
