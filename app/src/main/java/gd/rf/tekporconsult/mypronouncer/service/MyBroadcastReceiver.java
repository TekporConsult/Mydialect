package gd.rf.tekporconsult.mypronouncer.service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context,MyService.class);
        context.stopService(intent1);
        Toast.makeText(context, "Download paused and process stopped", Toast.LENGTH_LONG).show();
    }
}
