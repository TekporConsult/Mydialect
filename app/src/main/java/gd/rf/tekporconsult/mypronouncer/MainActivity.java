package gd.rf.tekporconsult.mypronouncer;

import static gd.rf.tekporconsult.mypronouncer.service.App.OFFLINE_CHANNEL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.simple.JSONValue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import gd.rf.tekporconsult.mypronouncer.CallBacks.BroadCastReceiver;
import gd.rf.tekporconsult.mypronouncer.CallBacks.JavaScriptCallBack;
import gd.rf.tekporconsult.mypronouncer.model.Definition;
import gd.rf.tekporconsult.mypronouncer.model.MigrationHistory;
import gd.rf.tekporconsult.mypronouncer.model.Notification;
import gd.rf.tekporconsult.mypronouncer.model.Pronunciation;
import gd.rf.tekporconsult.mypronouncer.model.Summary;
import gd.rf.tekporconsult.mypronouncer.model.Transcribe;
import gd.rf.tekporconsult.mypronouncer.model.Trending;
import gd.rf.tekporconsult.mypronouncer.service.DatabaseAccess;
import gd.rf.tekporconsult.mypronouncer.service.OfflineService;

public class MainActivity extends AppCompatActivity {

    ServiceConnection serviceConnection;
    OfflineService offlineService;
    protected WebView webView;
    protected int a = 0;
    boolean isBonded = false;
    FloatingActionButton sharedValueSet;
    protected FirebaseFirestore db;
    protected String key;
    boolean sent = true;
   public  DatabaseAccess databaseAccess;
    final static int REQ_CODE_SPEECH_INPUT = 99;
    public static String defaultLang = "dictionary";
    public static boolean isNotCompleted = true;
    public static boolean isNotCanceled = true;
    boolean isDownloading = false;
    int wordOfTheDay = Math.toIntExact(Math.round(Math.random() * 10000 + 1));
    Notification notification;
    NotificationManagerCompat notificationManagerCompat;
    private Intent serviceIntent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.webView);
        sharedValueSet = findViewById(R.id.sharedValueSet);


        sharedValueSet.setOnClickListener(view -> {
            webView.evaluateJavascript("javascript:startShare()", s ->
                    {
//                                                    Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                    }
            );
        });


        db = FirebaseFirestore.getInstance();
        databaseAccess = DatabaseAccess.getInstance(MainActivity.this);
        databaseAccess.open();

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setLoadsImagesAutomatically(true);
        webView.getSettings().setDomStorageEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.red));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.redPrimary));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            webSettings.setOffscreenPreRaster(true);
        }
        webSettings.setUseWideViewPort(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            webSettings.setForceDark(WebSettings.FORCE_DARK_AUTO);
        }
        webSettings.setNeedInitialFocus(true);


        Intent intent1 = getIntent();
        String getProgress = intent1.getStringExtra("progress");
        if (getProgress != null && getProgress.equals("progress")) {
            webView.loadUrl("file:///android_asset/statistics.html");
        } else {
            webView.loadUrl("file:///android_asset/index.html");
        }

        if (isNetworkAvailable(this)) {
            db.collection("api").document("dictionary")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot result = task.getResult();

                            webView.addJavascriptInterface(new JavaScriptCallBack(MainActivity.this, webView, db, databaseAccess, String.valueOf(result.get("app_id")), String.valueOf(result.get("app_key"))), "Android");


                        }
                    });


            db.collection("api").document("location")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot result = task.getResult();
                            key = String.valueOf(result.get("key"));

                        }
                    });
        } else {
            webView.addJavascriptInterface(new JavaScriptCallBack(MainActivity.this, webView, db, databaseAccess, null, null), "Android");
        }


        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (request.getUrl() != null && (request.getUrl().toString().startsWith("http://") || request.getUrl().toString().startsWith(("https://")))) {
                        view.getContext().startActivity(
                                new Intent(Intent.ACTION_VIEW, Uri.parse(request.getUrl().toString())));
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    if (view.getUrl() != null && (view.getUrl().startsWith("http://") || view.getUrl().startsWith(("https://")))) {
                        view.getContext().startActivity(
                                new Intent(Intent.ACTION_VIEW, Uri.parse(view.getUrl())));
                        return true;
                    } else {
                        return false;
                    }
                }
            }


            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                view.loadUrl("file:///android_asset/home.html");
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                notification = databaseAccess.getNotification();

                if(url.contains("lookup")){
                    sharedValueSet.setVisibility(View.VISIBLE);
                }else{
                    sharedValueSet.setVisibility(View.GONE);
                }


                if (url.contains("statistics")) {
                    getStatistics();

                    if (notification != null && notification.getRememberMe() == 3) {
                        webView.evaluateJavascript("javascript:unhidden()", s ->
                                {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                                }
                        );
                    }

                } else if (url.contains("home")) {
                    view.clearHistory();

                    if (isNetworkAvailable(MainActivity.this)) {
                        db.collection(defaultLang).orderBy("date", Query.Direction.DESCENDING).limit(1)
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        ArrayList<Object> arrayList = new ArrayList<>();
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Map<String, Object> data = document.getData();
                                            arrayList.add(data);

                                            db.collection("pronunciation").document(document.getId())
                                                    .get()
                                                    .addOnCompleteListener(task1 -> {
                                                        if (task1.isSuccessful()) {
                                                            ArrayList<Object> arrayList1 = new ArrayList<>();
                                                            DocumentSnapshot result = task1.getResult();
                                                            Map<String, Object> objectMap = new HashMap<>();
                                                            objectMap.put("id", result.getId());
                                                            objectMap.put("phoneticSpelling", result.get("phoneticSpelling"));

                                                            arrayList1.add(objectMap);

                                                            String jsonText1 = JSONValue.toJSONString(arrayList1);
                                                            webView.evaluateJavascript("javascript:pronounce(" + jsonText1 + ")", s ->
                                                                    {
//                                                    Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                                                                    }
                                                            );

                                                        }
                                                    });
                                        }


                                        String jsonText = JSONValue.toJSONString(arrayList);
                                        webView.evaluateJavascript("javascript:recent(" + jsonText + ")", s ->
                                                {
//                                                    Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                                                }
                                        );


                                    }
                                });


                        db.collection(defaultLang).orderBy("view", Query.Direction.DESCENDING).limit(15)
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        ArrayList<Object> arrayList = new ArrayList<>();
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Map<String, Object> data = new HashMap<>();
                                            ArrayList<String> arrayList1 = (ArrayList<String>) document.get("definitions");
                                            data.put("id", document.getId());
                                            data.put("definitions", arrayList1.get(0));
                                            arrayList.add(data);
                                        }

                                        String jsonText = JSONValue.toJSONString(arrayList);
                                        webView.evaluateJavascript("javascript:trending(" + jsonText + ")", s ->
                                                {
//                                                    Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                                                }
                                        );

                                    }
                                });
                    } else {
                        //offline
                      int  migration = databaseAccess.getMigration();
                        if(migration>5){
                            final Definition wordOfTheDay1 = databaseAccess.getWordOfTheDay(wordOfTheDay);
                            final Pronunciation pronunciationOfTheDay = databaseAccess.getPronunciation(wordOfTheDay1.getWord());

                            ArrayList<Object> arrayList1 = new ArrayList<>();
                            Map<String, Object> objectMap = new HashMap<>();
                            if (pronunciationOfTheDay != null) {
                                objectMap.put("id", pronunciationOfTheDay.getWord());
                                objectMap.put("phoneticSpelling", pronunciationOfTheDay.getPhonics().split("_")[0]);
                            } else {
                                objectMap.put("id", wordOfTheDay1.getWord());
                                objectMap.put("phoneticSpelling", null);
                            }
                            arrayList1.add(objectMap);
                            String jsonText1 = JSONValue.toJSONString(arrayList1);
                            webView.evaluateJavascript("javascript:pronounce(" + jsonText1 + ")", s ->
                                    {
//                                                    Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                                    }
                            );


                            ArrayList<Object> arrayList = new ArrayList<>();
                            ArrayList<String> arrayList2 = (ArrayList<String>) JSONValue.parse(wordOfTheDay1.getDefinition());
                            Map<String, Object> map = new HashMap<>();
                            map.put("definitions", arrayList2);
                            map.put("id", wordOfTheDay1.getWord());
                            map.put("date", new Date().getTime() - (Math.random() * 1000 * 60 * 60 * 24));
                            arrayList.add(map);

                            String jsonText = JSONValue.toJSONString(arrayList);

                            webView.evaluateJavascript("javascript:recent(" + jsonText + ")", s ->
                                    {
//                                                    Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                                    }
                            );


                            ArrayList<Trending> bookmark = databaseAccess.getHistoryLimit();
                            ArrayList<Object> arrayListAll = new ArrayList<>();
                            for (Trending trending : bookmark) {
                                Map<String, Object> data = new HashMap<>();
                                data.put("id", trending.getDefinition());
                                data.put("definitions", trending.getWord());
                                arrayListAll.add(data);
                            }

                            String jsonTextAll = JSONValue.toJSONString(arrayListAll);
//                        System.out.println(jsonTextAll);
                            webView.evaluateJavascript("javascript:trending(" + jsonTextAll + ")", s ->
                                    {
//                                                    Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                                    }
                            );
                        }



                    }


                    ArrayList<Map<String, String>> arrayList = new ArrayList<>();
                    Map<String, String> stringStringMap = new HashMap<>();
                    stringStringMap.put("key", key);
                    arrayList.add(stringStringMap);

                    String jsonText = JSONValue.toJSONString(arrayList);

                    webView.evaluateJavascript("javascript:getLocation(" + jsonText + ")", s ->
                            {
//                                                    Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                            }
                    );


                    if (notification == null && sent) {
                        new Handler().postDelayed(() -> offlineDialog(), 1000*60*3 );
                        sent = false;
                    } else if (notification != null && notification.getRememberMe() == 1 && notification.getData()+1000*60*60*24*5 <= new Date().getTime()) {
                            new Handler().postDelayed(() -> offlineDialog(), 1000*60*3);
                        } else if (notification != null && notification.getRememberMe() == 4 && notification.getData() + 1000 * 60 * 60 * 24 <= new Date().getTime()) {
                            offlineReminderConfirm();
                        } else if (notification != null && notification.getRememberMe() == 0) {
                                int migration = databaseAccess.getMigration();
                                    if (migration < 31 && isNetworkAvailable(MainActivity.this)) {
                                        startServices();
                                    }
                            }


                    if (notification != null) {
                        if (isDownloading || notification.getRememberMe() == 3 || notification.getRememberMe() == 0) {
                            webView.evaluateJavascript("javascript:unhidden(" + jsonText + ")", s ->
                                    {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                                    }
                            );
                        }
                    }

                } else if (url.contains("bookmark")) {

                    ArrayList<Trending> bookmark = databaseAccess.getBookmark();
                    ArrayList<Object> arrayList = new ArrayList<>();

                    for (Trending trending : bookmark) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("id", trending.getWord());
                        data.put("definition", trending.getDefinition());
                        arrayList.add(data);
                    }
                    String jsonText = JSONValue.toJSONString(arrayList);
                    webView.evaluateJavascript("javascript:getHistory(" + jsonText + ")", s ->
                            {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                            }
                    );
                } else if (url.contains("history")) {


                    ArrayList<Trending> bookmark = databaseAccess.getHistory();
                    ArrayList<Object> arrayList = new ArrayList<>();

                    for (Trending trending : bookmark) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("id", trending.getDefinition());
                        data.put("definition", trending.getWord());
                        arrayList.add(data);
                    }
                    String jsonText = JSONValue.toJSONString(arrayList);
                    webView.evaluateJavascript("javascript:getHistory(" + jsonText + ")", s ->
                            {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                            }
                    );
                } else if (url.contains("translate")) {

                    ArrayList<Transcribe> bookmark = databaseAccess.getTranscribe();
                    ArrayList<Object> arrayList = new ArrayList<>();

                    for (Transcribe transcribe : bookmark) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("id", transcribe.getId());
                        data.put("fromLang", transcribe.getFromLang());
                        data.put("fromKey", transcribe.getFromKey());
                        data.put("message", transcribe.getMessage());
                        data.put("toLang", transcribe.getToLang());
                        data.put("toKey", transcribe.getToKey());
                        arrayList.add(data);
                    }
                    String jsonText = JSONValue.toJSONString(arrayList);
                    webView.evaluateJavascript("javascript:getTranscribe(" + jsonText + ")", s ->
                            {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                            }
                    );

                }


            }
        });

        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        Uri appLinkData = appLinkIntent.getData();

        if(appLinkData != null){
            if(!String.valueOf(appLinkData).isEmpty()){
                String path = appLinkData.toString();
                int i = path.indexOf('=');
                String substring = path.substring(i+1);
                webView.loadUrl("file:///android_asset/home.html?word="+substring);
            }
        }

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent);
            }
        }




    }

    public void offlineDialog() {
        if (isNetworkAvailable(this)) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Dictionary available in offline")
                    .setMessage("Hello there,\nThis dictionary can now be made offline.")
                    .setCancelable(false)
                    .setOnCancelListener(dialogInterface -> {
                        databaseAccess.setNotification(1);
                    })
                    .setPositiveButton("Ignore", (dialog, id) -> {
                        dialog.cancel();
                        databaseAccess.setNotification(1);
                    })
                    .setNegativeButton("Download", (dialog, id) -> {
                        dialog.cancel();
                        new Handler().postDelayed(() -> {
                            offlineDialogConfirm();
                        }, 500);
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

    }

    public void offlineDialogConfirm(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert!")
                .setMessage("Depending on the speed of your network, this process should take a few minutes.\nAt any time, you can stop and start again.")
                .setCancelable(false)
                .setPositiveButton("Cancel", (dialog, id) -> {
                            databaseAccess.setNotification(4);
                            dialog.cancel();
                        }
                )
                .setNegativeButton("Continue", (dialog, id) -> {
                    dialog.cancel();
                    if (isNetworkAvailable(MainActivity.this)) {
                        startServices();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }


    public void offlineReminderConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reminder!")
                .setMessage("Hello there, You forgot to continue your download.")
                .setCancelable(false)
                .setPositiveButton("ignore", (dialog, id) -> {
                            databaseAccess.setNotification(1);
                            dialog.cancel();
                        }
                )
                .setNegativeButton("Continue", (dialog, id) -> {
                    databaseAccess.setNotification(0);
                    dialog.cancel();
                    if (isNetworkAvailable(MainActivity.this)) {
                        startServices();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    public void startServices() {
        if (isNetworkAvailable(this)) {
            serviceIntent = new Intent(getApplicationContext(), OfflineService.class);
            startService(serviceIntent);
            if (serviceConnection == null) {
                serviceConnection = new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                        OfflineService.MyBinder myBinder = (OfflineService.MyBinder) iBinder;
                        offlineService = myBinder.MyNewBinder();
                        isBonded = true;
                        databaseAccess.setNotification(0);
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName componentName) {
                        isBonded = false;
                    }
                };
            }

            bindService(serviceIntent, serviceConnection, 0);
            makeOffline();
            isDownloading = true;
        }
    }

    public void makeOffline() {

        new Handler().postDelayed(() -> {
            if (isBonded) {
                getProgress();
            }
        }, 1000);
    }

    private void getProgress() {
       new Thread(() -> {

           Intent intent  =new Intent(MainActivity.this, BroadCastReceiver.class);
           intent.putExtra("unbind","unbind");
           PendingIntent pauseIntent  = PendingIntent.getBroadcast(getApplicationContext(),1,intent,PendingIntent.FLAG_IMMUTABLE);

           Intent intent1  =new Intent(MainActivity.this, MainActivity.class);
           intent1.putExtra("progress","progress");
           PendingIntent mainIntent  = PendingIntent.getActivity(getApplicationContext(),2,intent1,PendingIntent.FLAG_IMMUTABLE);


           notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
           NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this,OFFLINE_CHANNEL);
           builder.setSmallIcon(R.mipmap.ic_launcher_round)
                   .setProgress(100, 0, false)
                   .setOnlyAlertOnce(true)
                   .setPriority(NotificationManager.IMPORTANCE_HIGH)
                   .setOngoing(true)
                   .setContentTitle("Offline Dictionary download")
                   .setContentText("download in progress")
                   .setAutoCancel(false)
                   .addAction(R.mipmap.ic_launcher_round, "Pause", pauseIntent)
                   .setContentIntent(mainIntent)
                   .setCategory(DOWNLOAD_SERVICE);
           notificationManagerCompat.notify(0,builder.build());

           while (isNotCompleted){
               try {
                   Summary progress = offlineService.Statistics();
                   if (progress.getTotalProgress() <= 100) {
                       builder.setProgress(100, (int) progress.getTotalProgress(), false);
                       builder.setContentText((int) progress.getTotalProgress()+"%");
                       builder.setPriority(NotificationManager.IMPORTANCE_LOW);
                       notificationManagerCompat.notify(0, builder.build());
                   } else {
                       isNotCompleted = false;
                   }
                   Thread.sleep(20000);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
           if(isNotCanceled){
               builder.setProgress(0,0,false);
               builder.setContentText("Congratulation!!, you now have MyDialect in offline mode");
               builder.setOngoing(false);
               builder.setOnlyAlertOnce(false);
               builder.setPriority(NotificationManager.IMPORTANCE_HIGH);
               notificationManagerCompat.notify(0,builder.build());
               unbindService(serviceConnection);
           }else {
               notificationManagerCompat.cancel(0);
           }

       }).start();

    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            if(sharedText.split("[\n ]").length < 4){
                webView.loadUrl("file:///android_asset/home.html?word="+sharedText.replaceAll("[\n]", " "));
            }else{
                webView.loadUrl("file:///android_asset/home.html?word="+sharedText.split("[\n ]")[0]);
            }
        }
    }

    public static void setDefaultLang(String value){
        defaultLang = value;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();

            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);

    }


    @Override
    public void onBackPressed() {
        if (a > 2) {
            super.onBackPressed();
        } else {
            a++;
        }

    }


    static public void startListening(Activity activity) {
//Intent to listen to user vocal input and return result in same activity
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //Use a language model based on free-form speech recognition.
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        //Message to display in dialog box
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, activity.getString(R.string.speech_to_text_info));
        try {
            activity.startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            a.fillInStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Toast.makeText(this, result.get(0), Toast.LENGTH_SHORT).show();
                    ArrayList<Object> arrayList = new ArrayList<>();
                    Map<String, Object> dataList = new HashMap<>();
                    dataList.put("text", result.get(0));
                    arrayList.add(dataList);
                    String jsonText = JSONValue.toJSONString(arrayList);
                    webView.evaluateJavascript("javascript:listingCallBack(" + jsonText + ")", s ->
                            {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                            }
                    );
                }
                break;
            }
        }
    }


    public void getStatistics() {
        Summary summary = new Summary(0, 0, 0);
        ArrayList<MigrationHistory> allMigrationHistory = databaseAccess.getAllMigrationHistory();
        long dictionary = 0, phonics = 0;

        for (MigrationHistory migrationHistory : allMigrationHistory) {
            if (migrationHistory.getType().equals("dictionary")) {
                phonics += migrationHistory.getAt();
            } else {
                dictionary += migrationHistory.getAt();
            }
        }
        double d = (double) dictionary / 150000.0;
        double p = (double) phonics / 150000.0;
        double t = (double) dictionary + phonics;
        summary.setPhonicsProgress(Math.round(p * 100));
        summary.setDictionaryProgress(Math.round(d * 100));
        summary.setTotalProgress(Math.round((t / (150000 * 2)) * 100));
        ArrayList<Map<String, Long>> arrayList = new ArrayList<>();
        Map<String, Long> map = new HashMap<>();
        map.put("total", summary.getTotalProgress());
        map.put("phonics", summary.getPhonicsProgress());
        map.put("definition", summary.getDictionaryProgress());

        arrayList.add(map);
        String jsonText = JSONValue.toJSONString(arrayList);
        webView.evaluateJavascript("javascript:getStatistics(" + jsonText + ")", s ->
                {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                }
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseAccess.close();
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}