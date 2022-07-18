package gd.rf.tekporconsult.mypronouncer.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.widget.Toast;

import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import javax.annotation.meta.When;
import javax.net.ssl.HttpsURLConnection;

import gd.rf.tekporconsult.mypronouncer.MainActivity;
import gd.rf.tekporconsult.mypronouncer.R;
import gd.rf.tekporconsult.mypronouncer.model.Pronunciation;

public class OfflineService extends Service {
    public OfflineService() {
    }

    public  class MyBinder extends Binder{
     public OfflineService MyNewBinder(){
            return OfflineService.this;
        }
    }


    private MyBinder myBinder =  new MyBinder();

     private int progress = 0;
     boolean continueTread = true;
    DatabaseAccess databaseAccess;
    Thread thread;
    @Override
    public void onCreate() {
        super.onCreate();
        databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        databaseAccess.close();
        continueTread = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       new Thread(() ->
       {

           while (continueTread){
               progress +=5;
               try {
                   Thread.sleep(1000);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }

       }
               ).start();
        return Service.START_REDELIVER_INTENT;
    }
    public void setPronunciationsNow(String something,String table){
        new Thread(() -> {
            URL url;
            StringBuilder stringBuilder = new StringBuilder();
            try {
                url = new URL(something);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Accept", "application/json");
                // read the output from the server
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }

                ArrayList<Map<Object, Object>> parse = (ArrayList<Map<Object, Object>>) JSONValue.parse(stringBuilder.toString());

                for(Map<Object, Object> map : parse){

                    String word = String.valueOf(map.get("word")).toLowerCase();
                    String IPAS = (String) map.get("IPA");

                    if(word == null) continue;
                    if(word.contains("/")) continue;
                    if(word.charAt(0) == '-') continue;
                    Pronunciation pronunciation = new Pronunciation(word,IPAS);
                    databaseAccess.setPronunciation(pronunciation,table);
                    Thread.sleep(100);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }

    public void setDefinitionsAndExamples(String something, String table){
        new Thread(() -> {
            URL url;
            StringBuilder stringBuilder = new StringBuilder();
            try {
                url = new URL(something);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Accept", "application/json");
                // read the output from the server
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }

                ArrayList<Map<Object, Object>> parse = (ArrayList<Map<Object, Object>>) JSONValue.parse(stringBuilder.toString());

                for(Map<Object, Object> map : parse){

                    String word = String.valueOf(map.get("id")).toLowerCase();
                    ArrayList<String> definitions = (ArrayList<String>) map.get("definitions");
                    ArrayList<String> categories = (ArrayList<String>) map.get("categories");
                    ArrayList<String> examples = (ArrayList<String>) map.get("categories");
//                    databaseAccess.getDefinitions();
                    Thread.sleep(100);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }

    public  int getProgress(){
        return  progress;
    };


}