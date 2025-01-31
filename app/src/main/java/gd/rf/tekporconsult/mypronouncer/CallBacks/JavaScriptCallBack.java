package gd.rf.tekporconsult.mypronouncer.CallBacks;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import gd.rf.tekporconsult.mypronouncer.MainActivity;
import gd.rf.tekporconsult.mypronouncer.model.Category;
import gd.rf.tekporconsult.mypronouncer.model.Definition;
import gd.rf.tekporconsult.mypronouncer.model.Example;
import gd.rf.tekporconsult.mypronouncer.model.Pronunciation;
import gd.rf.tekporconsult.mypronouncer.model.Transcribe;
import gd.rf.tekporconsult.mypronouncer.model.Trending;
import gd.rf.tekporconsult.mypronouncer.model.Word;
import gd.rf.tekporconsult.mypronouncer.service.DatabaseAccess;


public class JavaScriptCallBack {
    Context context;
    WebView webView;
    protected String app_id;
    AlertDialog.Builder builder;
    ProgressDialog progressDialog;
    protected FirebaseFirestore db;
    protected   String app_key;
    String activeLange = "en-gb";
    String defaultLang = "dictionary";
    Activity activity;
    boolean isNotPlaying = true;
    TextToSpeech textToSpeech;
    int migration;
    protected MediaPlayer mediaPlayerTextReader, mediaPlayer;
    DatabaseAccess databaseAccess;
    public JavaScriptCallBack(Context context, WebView webView, FirebaseFirestore db, DatabaseAccess databaseAccess,String app_id, String app_key) {
        this.context = context;
        this.webView = webView;
        this.db = db;
        this.databaseAccess = databaseAccess;
        this.app_id = app_id;
        this.app_key = app_key;
        builder = new AlertDialog.Builder(this.context);
        progressDialog = new ProgressDialog(this.context);
        activity = (Activity) context;
        migration = databaseAccess.getMigration();
        textToSpeech = new TextToSpeech(context, status -> {
            if (status != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(Locale.UK);
            }
        });

    }

    @JavascriptInterface
    public void resumeDownload() {
        databaseAccess.setNotification(0);
        MainActivity.isNotCompleted = true;
        MainActivity.isNotCanceled = true;
        Toast.makeText(context, "Download will resume soon.", Toast.LENGTH_LONG).show();
    }


    @JavascriptInterface
    public void textToSpeech(String string) {


        if(isNotPlaying){
            isNotPlaying = false;
            textToSpeech.speak(string, TextToSpeech.QUEUE_FLUSH, null);
        }



        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {
                isNotPlaying = false;
                activity.runOnUiThread(() -> webView.evaluateJavascript("javascript:playStart()", se ->
                        {
//                                                 Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                        }
                ));
            }

            @Override
            public void onDone(String s) {
                isNotPlaying = true;
                activity.runOnUiThread(() -> webView.evaluateJavascript("javascript:playEnd()", es ->
                        {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                        }
                ));
            }

            @Override
            public void onError(String s) {
                isNotPlaying = true;
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

        new Handler().postDelayed(() -> {
            Toast.makeText(context, "use to online mode for natural sound", Toast.LENGTH_SHORT).show();
        }, 2000);

    }

    @JavascriptInterface
    public String getDefaultLang() {
        return  activeLange;
    }

    @JavascriptInterface
    public void getLanguages(String s) {
        activeLange  = s;
        switch (s){
            case "en-gb" :
                MainActivity.setDefaultLang("dictionary");
                defaultLang = "dictionary";
                break;
            case "es" :
                MainActivity.setDefaultLang("spanish");
                defaultLang = "spanish";
                break;
            case "fr" :
                MainActivity.setDefaultLang("french");
                defaultLang = "french";
                break;
            case "hi" :
                MainActivity.setDefaultLang("hindi");
                defaultLang= "hindi";
                break;
        }

        activity.runOnUiThread(() -> webView.evaluateJavascript("javascript:changeHomeTexts()", sr ->{}));
    }

    @JavascriptInterface
    public void loadLanguages() {
        if (isNetworkAvailable(context)) {
            db.collection("languages")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            ArrayList<Object> arrayList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> data = document.getData();
                                arrayList.add(data);
                            }

                            String jsonText = JSONValue.toJSONString(arrayList);
                            webView.evaluateJavascript("javascript:setLanguages(" + jsonText + ")", s ->
                                    {
//                                                    Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                                    }
                            );

                        }
                    });
        }
    }


    @JavascriptInterface
    public void startReader(String url) throws IOException {

            if(mediaPlayerTextReader == null){

                activity.runOnUiThread(() -> webView.evaluateJavascript("javascript:toast()", s ->
                        {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                        }
                ));

                mediaPlayerTextReader = new MediaPlayer();

                mediaPlayerTextReader.setOnCompletionListener(mediaPlayer -> {
                    activity.runOnUiThread(() -> webView.evaluateJavascript("javascript:playEnd()", s ->
                            {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                            }
                    ));
                });

               mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                   @Override
                   public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                       activity.runOnUiThread(() -> webView.evaluateJavascript("javascript:playEnd()", s ->
                               {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                               }
                       ));
                       return false;
                   }
               });

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mediaPlayerTextReader.setAudioAttributes(
                            new AudioAttributes
                                    .Builder()
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                    .build());
                } else {
                    mediaPlayerTextReader.setAudioStreamType(AudioManager.STREAM_MUSIC);
                }

                mediaPlayerTextReader.setDataSource(url);
                mediaPlayerTextReader.prepare();
                mediaPlayerTextReader.start();

                activity.runOnUiThread(() -> webView.evaluateJavascript("javascript:playStart()", s ->
                        {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                        }
                ));

            }else{
                if(mediaPlayerTextReader.isPlaying()){
                    mediaPlayerTextReader.pause();
                    activity.runOnUiThread(() -> webView.evaluateJavascript("javascript:playEnd()", s ->
                            {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                            }
                    ));
                }else{
                    mediaPlayerTextReader.start();
                    activity.runOnUiThread(() -> webView.evaluateJavascript("javascript:playStart()", s ->
                            {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                            }
                    ));
                }
            }




    }

    @JavascriptInterface
    public void stopReader() {

        if (mediaPlayerTextReader != null) {
            mediaPlayerTextReader.pause();
            activity.runOnUiThread(() -> webView.evaluateJavascript("javascript:playEnd()", s ->
                    {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                    }
            ));
        }
    }

    @JavascriptInterface
    public void toast() {
        Toast.makeText(context, "Please!, Network is require", Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public void cleanMediaPlayerTextReader() {
        if (mediaPlayerTextReader != null) {
            mediaPlayerTextReader.stop();
            mediaPlayerTextReader = null;
            activity.runOnUiThread(() -> webView.evaluateJavascript("javascript:playEnd()", s ->
                    {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                    }
            ));
        }
    }


    @JavascriptInterface
    public void cleanMediaPlayer() {
        if (mediaPlayerTextReader != null){
            mediaPlayerTextReader.stop();
            mediaPlayerTextReader = null;

        }
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer = null;
        }
    }



    @JavascriptInterface
    public void playAudio(String url, String name) throws IOException {


            mediaPlayer = new MediaPlayer();


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mediaPlayer.setAudioAttributes(
                        new AudioAttributes
                                .Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .build());
            } else {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }

            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();


    }

    @JavascriptInterface
    public void shareNow(String word, String definition) {

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, word + "\n" + definition + "\n\nThis is share from My Dialect, My mannerisms https://tekporconsult.rf.gd/dictionary?word=" + word + "\n\n" + "Get on google play https://play.google.com/store/apps/details?id=gd.rf.tekporconsult.mypronouncer");
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, "sharing " + word);
        context.startActivity(shareIntent);

    }


    @JavascriptInterface
    public void sendData(String ip, String country, String city, String latitude, String longitude, String organisation, String continent, String lastUpdated, String bgpPrefix, String countryFlagEmoji) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("ip", ip);
        userData.put("country", country);
        userData.put("city", city);
        userData.put("lag", latitude);
        userData.put("lng", longitude);
        userData.put("organisation", organisation);
        userData.put("continent", continent);
        userData.put("lastUpdated", lastUpdated);
        userData.put("bgPrefix", bgpPrefix);
        userData.put("countryFlagEmoji", countryFlagEmoji);
        db.collection("UserData").document(ip)
                .set(userData);
    }




    @JavascriptInterface
    public void recordSound() {

        MainActivity.startListening((Activity) context);

    }


    @JavascriptInterface
    public void deleteFromHistory() {
        databaseAccess.DeleteHistory();

        new Handler().postDelayed(() -> {

            ArrayList<Trending> bookmark = databaseAccess.getHistory();
            ArrayList<Object> arrayList = new ArrayList<>();

            for (Trending trending : bookmark) {
                Map<String, Object> data = new HashMap<>();
                data.put("id", trending.getDefinition());
                data.put("definition", trending.getWord());
                arrayList.add(data);
            }
            String jsonText = JSONValue.toJSONString(arrayList);
         activity.runOnUiThread(() -> webView.evaluateJavascript("javascript:getHistory(" + jsonText + ")", s ->{}
         ));

        }, 500);
    }



    @JavascriptInterface
    public void deleteFromBookMark() {
        databaseAccess.DeleteBookMark();

        new Handler().postDelayed(() -> {
            ArrayList<Trending> bookmark = databaseAccess.getBookmark();
            ArrayList<Object> arrayList = new ArrayList<>();

            for (Trending trending : bookmark) {
                Map<String, Object> data = new HashMap<>();
                data.put("id", trending.getWord());
                data.put("definition", trending.getDefinition());
                arrayList.add(data);
            }
            String jsonText = JSONValue.toJSONString(arrayList);
          activity.runOnUiThread(() -> webView.evaluateJavascript("javascript:getHistory(" + jsonText + ")", s ->{}
          ));
        }, 500);
    }



    @JavascriptInterface
    public void deleteFromTranscribe() {
databaseAccess.DeleteTranscribe();

        new Handler().postDelayed(() -> {

            ArrayList<Transcribe> bookmark = databaseAccess.getTranscribe();
            ArrayList<Object> arrayList = new ArrayList<>();

            for (Transcribe transcribe: bookmark) {
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
            activity.runOnUiThread(() -> {
                webView.evaluateJavascript("javascript:getTranscribe(" + jsonText + ")", s ->
                        {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                        }
                );
            });
        }, 500);
    }

    @JavascriptInterface
    public void sendText(String fromLang,  String fromKey, String message,String toLang,  String toKey ) throws IOException {

        double v = (Math.random() * 28.0)+1.0;
        URL url = new URL("https://t"+Math.round(v)+".translatedict.com/1.php?p1=auto&p2="+toKey+"&p3="+message);
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

        StringBuilder stringBuilder = new StringBuilder();

        String line = null;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line + "\n");
        }


        String newMessage = message+"="+stringBuilder;

        databaseAccess.transcribe(new Transcribe(0, fromLang, fromKey, newMessage, toLang, toKey));
        new Handler().postDelayed(() -> {

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
            activity.runOnUiThread(() -> {
                webView.evaluateJavascript("javascript:getTranscribe(" + jsonText + ")", s ->
                        {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                        }
                );
            });
        }, 500);



    }


    @JavascriptInterface
    public void BookMarks(String word, String definition) {
        databaseAccess.bookmark(new Trending(word, definition));
        Toast.makeText(context, "Bookmarked", Toast.LENGTH_SHORT).show();
    }


    @JavascriptInterface
    public void getExample(String word, HashMap hashMap) {

        ArrayList<String> arrayList = (ArrayList<String>) hashMap.get("categories");
        ArrayList<String> stringArrayList = (ArrayList<String>) hashMap.get("definitions");
        HashMap<String, HashMap<String, ArrayList<Object>>> partOfSpeech = new HashMap<>();

        if (isNetworkAvailable(context) && migration<30) {
            db.collection("example").document(word)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                if (task.getResult().get("examples") != null) {
                                    ArrayList<HashMap<Object, Object>> examples = (ArrayList<HashMap<Object, Object>>) task.getResult().get("examples");
                                    for (int index = 0; index < arrayList.size(); index++) {
                                        if (partOfSpeech.get(arrayList.get(index)) == null) {
                                            ArrayList<Object> arrayList1 = new ArrayList<>();
                                            arrayList1.add(stringArrayList.get(index));
                                            HashMap<String, ArrayList<Object>> hashMap1 = new HashMap<>();
                                            hashMap1.put("definitions", arrayList1);
                                            for (HashMap<Object, Object> arrayList2 : examples) {

                                                if (arrayList2.get(arrayList.get(index)) != null) {
                                                    hashMap1.put("examples", (ArrayList<Object>) arrayList2.get(arrayList.get(index)));
                                                }
                                            }
                                            partOfSpeech.put(arrayList.get(index), hashMap1);

                                        } else {
                                            partOfSpeech.get(arrayList.get(index)).get("definitions").add(stringArrayList.get(index));
                                        }
                                    }

                                    String jsonText = JSONValue.toJSONString(partOfSpeech);
                                    System.out.println(jsonText);
                                    activity.runOnUiThread(() -> webView.evaluateJavascript("javascript:getDataExample(" + jsonText + ")", s ->
                                            {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                                            }
                                    ));

                                } else {
                                    new Thread(() -> {
                                        URL url;
                                        StringBuilder stringBuilder = new StringBuilder();
                                        try {
                                            url = new URL(dictionaryEntries(word, "examples"));
                                            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                                            urlConnection.setRequestProperty("Accept", "application/json");
                                            urlConnection.setRequestProperty("app_id", app_id);
                                            urlConnection.setRequestProperty("app_key", app_key);

                                            // read the output from the server
                                            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));


                                            String line = null;
                                            while ((line = reader.readLine()) != null) {
                                                stringBuilder.append(line + "\n");
                                            }
                                            Map<Object, Object> parse = (Map<Object, Object>) JSONValue.parse(stringBuilder.toString());

                                            ArrayList<Map<String, ArrayList<String>>> examples = new ArrayList<>();


                                            ArrayList<Map<Object, Object>> d = new ArrayList<>();
                                            d.addAll((Collection<? extends Map<Object, Object>>) parse.get("results"));

                                            for (int a = 0; a < d.size(); a++) {
                                                ArrayList<Map<Object, Object>> lexicalEntries = (ArrayList<Map<Object, Object>>) d.get(a).get("lexicalEntries");
                                                ArrayList<String> example = new ArrayList<>();

                                                for (Map<Object, Object> objectObjectMap : lexicalEntries) {

                                                    ArrayList<Object> entries = (ArrayList<Object>) objectObjectMap.get("entries");

                                                    Map<Object, Object> lexicalCat = (Map<Object, Object>) objectObjectMap.get("lexicalCategory");
                                                    String lexicalCategory = lexicalCat.get("id").toString();

                                                    if (entries != null) {
                                                        Map<Object, Object> o1 = (Map<Object, Object>) entries.get(0);

                                                        if (o1.get("senses") != null) {
                                                            ArrayList<Map<Object, Object>> senses = (ArrayList<Map<Object, Object>>) o1.get("senses");
                                                            for (Map<Object, Object> objects : senses) {
                                                                if (objects.get("examples") != null) {
                                                                    ArrayList<Map<Object, Object>> definitions1 = (ArrayList<Map<Object, Object>>) objects.get("examples");
                                                                    for (Map<Object, Object> ob : definitions1) {
                                                                        example.add((String) ob.get("text"));
                                                                    }
                                                                }


                                                                ArrayList<Map<Object, Object>> subsenses = (ArrayList<Map<Object, Object>>) objects.get("subsenses");

                                                                if (subsenses != null) {
                                                                    for (Object obj : subsenses) {
                                                                        Map<Object, Object> obj1 = (Map<Object, Object>) obj;
                                                                        if (obj1.get("examples") != null) {
                                                                            ArrayList<Map<Object, Object>> definitions2 = (ArrayList<Map<Object, Object>>) obj1.get("examples");
                                                                            for (Map<Object, Object> e : definitions2) {
                                                                                example.add((String) e.get("text"));
                                                                            }
                                                                        }

                                                                    }
                                                                }


                                                            }
                                                        }

                                                    }


                                                    Map<String, ArrayList<String>> arrayListMap = new HashMap<>();
                                                    arrayListMap.put(lexicalCategory, example);
                                                    examples.add(arrayListMap);
                                                }

                                            }
                                            ArrayList<HashMap<String, Object>> arrayListNew = new ArrayList<>();
                                            HashMap<String, Object> data = new HashMap<>();
                                            data.put("id", word);
                                            data.put("examples", examples);
                                            arrayListNew.add(data);

                                            for (int index = 0; index < arrayList.size(); index++) {
                                                if (partOfSpeech.get(arrayList.get(index)) == null) {
                                                    ArrayList<Object> arrayList1 = new ArrayList<>();
                                                    arrayList1.add(stringArrayList.get(index));
                                                    HashMap<String, ArrayList<Object>> hashMap1 = new HashMap<>();
                                                    hashMap1.put("definitions", arrayList1);
                                                    for (HashMap<String, Object> arrayList2 : arrayListNew) {
                                                        if (arrayList2.get(arrayList.get(index)) != null) {
                                                            hashMap1.put("examples", (ArrayList<Object>) arrayList2.get(arrayList.get(index)));
                                                        }
                                                    }
                                                    partOfSpeech.put(arrayList.get(index), hashMap1);

                                                } else {
                                                    partOfSpeech.get(arrayList.get(index)).get("definitions").add(stringArrayList.get(index));
                                                }
                                            }

                                            activity.runOnUiThread(() -> {

                                                String jsonText = JSONValue.toJSONString(partOfSpeech);

                                                System.out.println(jsonText);

                                                webView.evaluateJavascript("javascript:getDataExample(" + jsonText + ")", s ->
                                                        {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                                                        }
                                                );
                                            });

                                            db.collection("example").document(word)
                                                    .set(data);

//

                                        } catch (MalformedURLException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }).start();
                                }


                            } else {
                                new Thread(() -> {
                                    URL url;
                                    StringBuilder stringBuilder = new StringBuilder();
                                    try {
                                        url = new URL(dictionaryEntries(word, "examples"));
                                        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                                        urlConnection.setRequestProperty("Accept", "application/json");
                                        urlConnection.setRequestProperty("app_id", app_id);
                                        urlConnection.setRequestProperty("app_key", app_key);

                                        // read the output from the server
                                        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));


                                        String line = null;
                                        while ((line = reader.readLine()) != null) {
                                            stringBuilder.append(line + "\n");
                                        }
                                        Map<Object, Object> parse = (Map<Object, Object>) JSONValue.parse(stringBuilder.toString());

                                        ArrayList<Map<String, ArrayList<String>>> examples = new ArrayList<>();


                                        ArrayList<Map<Object, Object>> d = new ArrayList<>();
                                        d.addAll((Collection<? extends Map<Object, Object>>) parse.get("results"));

                                        for (int a = 0; a < d.size(); a++) {
                                            ArrayList<Map<Object, Object>> lexicalEntries = (ArrayList<Map<Object, Object>>) d.get(a).get("lexicalEntries");
                                            ArrayList<String> example = new ArrayList<>();

                                            for (Map<Object, Object> objectObjectMap : lexicalEntries) {

                                                ArrayList<Object> entries = (ArrayList<Object>) objectObjectMap.get("entries");

                                                Map<Object, Object> lexicalCat = (Map<Object, Object>) objectObjectMap.get("lexicalCategory");
                                                String lexicalCategory = lexicalCat.get("id").toString();

                                                if (entries != null) {
                                                    Map<Object, Object> o1 = (Map<Object, Object>) entries.get(0);

                                                    if (o1.get("senses") != null) {
                                                        ArrayList<Map<Object, Object>> senses = (ArrayList<Map<Object, Object>>) o1.get("senses");
                                                        for (Map<Object, Object> objects : senses) {
                                                            if (objects.get("examples") != null) {
                                                                ArrayList<Map<Object, Object>> definitions1 = (ArrayList<Map<Object, Object>>) objects.get("examples");
                                                                for (Map<Object, Object> ob : definitions1) {
                                                                    example.add((String) ob.get("text"));
                                                                }
                                                            }


                                                            ArrayList<Map<Object, Object>> subsenses = (ArrayList<Map<Object, Object>>) objects.get("subsenses");

                                                            if (subsenses != null) {
                                                                for (Object obj : subsenses) {
                                                                    Map<Object, Object> obj1 = (Map<Object, Object>) obj;
                                                                    if (obj1.get("examples") != null) {
                                                                        ArrayList<Map<Object, Object>> definitions2 = (ArrayList<Map<Object, Object>>) obj1.get("examples");
                                                                        for (Map<Object, Object> e : definitions2) {
                                                                            example.add((String) e.get("text"));
                                                                        }
                                                                    }

                                                                }
                                                            }


                                                        }
                                                    }

                                                }


                                                Map<String, ArrayList<String>> arrayListMap = new HashMap<>();
                                                arrayListMap.put(lexicalCategory, example);
                                                examples.add(arrayListMap);
                                            }

                                        }
                                        ArrayList<HashMap<String, Object>> arrayListNew = new ArrayList<>();
                                        HashMap<String, Object> data = new HashMap<>();
                                        data.put("id", word);
                                        data.put("examples", examples);
                                        arrayListNew.add(data);

                                        for (int index = 0; index < arrayList.size(); index++) {
                                            if (partOfSpeech.get(arrayList.get(index)) == null) {
                                                ArrayList<Object> arrayList1 = new ArrayList<>();
                                                arrayList1.add(stringArrayList.get(index));
                                                HashMap<String, ArrayList<Object>> hashMap1 = new HashMap<>();
                                                hashMap1.put("definitions", arrayList1);
                                                for (HashMap<String, Object> arrayList2 : arrayListNew) {
                                                    if (arrayList2.get(arrayList.get(index)) != null) {
                                                        hashMap1.put("examples", (ArrayList<Object>) arrayList2.get(arrayList.get(index)));
                                                    }
                                                }
                                                partOfSpeech.put(arrayList.get(index), hashMap1);

                                            } else {
                                                partOfSpeech.get(arrayList.get(index)).get("definitions").add(stringArrayList.get(index));
                                            }
                                        }

                                        activity.runOnUiThread(() -> {
                                            String jsonText = JSONValue.toJSONString(partOfSpeech);
                                            System.out.println(jsonText);
                                            webView.evaluateJavascript("javascript:getDataExample(" + jsonText + ")", s ->
                                                    {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                                                    }
                                            );
                                        });

                                        db.collection("example").document(word)
                                                .set(data);

//

                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }).start();


                            }
                        }
                    });
        } else {

            if(migration>3){
                Example example = databaseAccess.getExample(word);

                ArrayList<HashMap<Object, Object>> examples = (ArrayList<HashMap<Object, Object>>) JSONValue.parse(example.getExample());
                for (int index = 0; index < arrayList.size(); index++) {
                    if (partOfSpeech.get(arrayList.get(index)) == null) {
                        ArrayList<Object> arrayList1 = new ArrayList<>();
                        arrayList1.add(stringArrayList.get(index));
                        HashMap<String, ArrayList<Object>> hashMap1 = new HashMap<>();
                        hashMap1.put("definitions", arrayList1);
                        for (HashMap<Object, Object> arrayList2 : examples) {

                            if (arrayList2.get(arrayList.get(index)) != null) {
                                hashMap1.put("examples", (ArrayList<Object>) arrayList2.get(arrayList.get(index)));
                            }
                        }
                        partOfSpeech.put(arrayList.get(index), hashMap1);

                    } else {
                        partOfSpeech.get(arrayList.get(index)).get("definitions").add(stringArrayList.get(index));
                    }
                }

                String jsonText = JSONValue.toJSONString(partOfSpeech);
                System.out.println(jsonText);
                activity.runOnUiThread(() -> webView.evaluateJavascript("javascript:getDataExample(" + jsonText + ")", s ->
                        {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                        }
                ));
            }else{
                Toast.makeText(context, "Not ready for offline use", Toast.LENGTH_LONG).show();
            }


        }

    }

    @JavascriptInterface
    public void getWords(String word) {
        final ArrayList<Word> words = databaseAccess.getWords(word);
        ArrayList<String> arrayList = new ArrayList<>();
        if (words != null) {
            for (Word word1 : words) {
                arrayList.add(word1.getWord());
            }
        }
        activity.runOnUiThread(() -> {
            webView.evaluateJavascript("javascript:setAutoSuggest(" + JSONValue.toJSONString(arrayList) + ")", s ->
                    {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                    }
            );
        });
    }

    @JavascriptInterface
    public void setClipBordText(String message) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        clipboard.setText(message);
        Toast.makeText(context, "Text saved to clipboard", Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public String getClipBordText() {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
         return clipboard.getText().toString();

    }

    @JavascriptInterface
    public void getInflections(String word) {

        if (isNetworkAvailable(context)) {
            db.collection("inflections").document(word.trim().toLowerCase())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {

                                Map<String, Object> data = new HashMap<>();
                                ArrayList<Object> arrayList = new ArrayList<>();
                                DocumentSnapshot result = task.getResult();
                                data.put("id", result.get("id"));
                                data.put("inflections", result.get("inflections"));
                                arrayList.add(data);

                                String jsonText = JSONValue.toJSONString(arrayList);
//                            System.out.println(jsonText);
                                webView.evaluateJavascript("javascript:getInflections(" + jsonText + ")", s ->
                                        {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                                        }
                                );
                            } else {

                                new Thread(() -> {
                                    URL url;
                                    StringBuilder stringBuilder = new StringBuilder();
                                    try {
                                        url = new URL(dictionaryInflections(word));
                                        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                                        urlConnection.setRequestProperty("Accept", "application/json");
                                        urlConnection.setRequestProperty("app_id", app_id);
                                        urlConnection.setRequestProperty("app_key", app_key);

                                        // read the output from the server
                                        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));


                                        String line = null;
                                        while ((line = reader.readLine()) != null) {
                                            stringBuilder.append(line + "\n");
                                        }
                                        Map<Object, Object> parse = (Map<Object, Object>) JSONValue.parse(stringBuilder.toString());
//                                    System.out.println(parse);
                                        ArrayList<Map<String, ArrayList<String>>> inject = new ArrayList<>();
                                        if (parse.get("results") != null) {
                                            ArrayList<Map<String, Object>> results = (ArrayList<Map<String, Object>>) parse.get("results");
                                            if (results.get(0).get("lexicalEntries") != null) {
                                                ArrayList<Map<String, Object>> lexicalEntries = (ArrayList<Map<String, Object>>) results.get(0).get("lexicalEntries");
                                                for (Map<String, Object> map : lexicalEntries) {
                                                    Map<String, ArrayList<String>> map1 = new HashMap<>();
                                                    if (map.get("lexicalCategory") != null) {
                                                        Map<String, Object> lexicalCategory = (Map<String, Object>) map.get("lexicalCategory");
                                                        if (lexicalCategory.get("id") != null) {
                                                            if (map.get("inflections") != null) {
                                                                ArrayList<Map<String, Object>> inflections = (ArrayList<Map<String, Object>>) map.get("inflections");
                                                                ArrayList<String> stringArrayList = new ArrayList<>();
                                                                for (Map<String, Object> map2 : inflections) {
                                                                    stringArrayList.add((String) map2.get("inflectedForm"));
                                                                }
                                                                map1.put((String) lexicalCategory.get("id"), stringArrayList);
                                                            }
                                                        }

                                                    }
                                                    inject.add(map1);
                                                }
                                            }


                                        }


                                        ArrayList<Object> arrayList = new ArrayList<>();
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("id", word);
                                        data.put("inflections", inject);
                                        arrayList.add(data);
                                        db.collection("inflections").document(word)
                                                .set(data);
                                        activity.runOnUiThread(() -> {
                                            String jsonText = JSONValue.toJSONString(arrayList);
//                                        System.out.println(jsonText);
                                            webView.evaluateJavascript("javascript:getInflections(" + jsonText + ")", s ->
                                                    {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                                                    }
                                            );
                                        });

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }).start();

                            }
                        }
                    });
        }
    }

    @JavascriptInterface
    public void getSentences(String word) {
        if (isNetworkAvailable(context)) {
            db.collection("sentences").document(word.trim().toLowerCase())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {

                                Map<String, Object> data = new HashMap<>();
                                ArrayList<Object> arrayList = new ArrayList<>();
                                DocumentSnapshot result = task.getResult();
                                data.put("id", result.get("id"));
                                data.put("sentences", result.get("sentences"));
                                arrayList.add(data);

                                String jsonText = JSONValue.toJSONString(arrayList);

                                webView.evaluateJavascript("javascript:getSentences(" + jsonText + ")", s ->
                                        {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                                        }
                                );
                            } else {

                                new Thread(() -> {
                                    URL url;
                                    StringBuilder stringBuilder = new StringBuilder();
                                    try {
                                        url = new URL(dictionarySentence(word));
                                        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                                        urlConnection.setRequestProperty("Accept", "application/json");
                                        urlConnection.setRequestProperty("app_id", app_id);
                                        urlConnection.setRequestProperty("app_key", app_key);

                                        // read the output from the server
                                        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));


                                        String line = null;
                                        while ((line = reader.readLine()) != null) {
                                            stringBuilder.append(line + "\n");
                                        }
                                        Map<Object, Object> parse = (Map<Object, Object>) JSONValue.parse(stringBuilder.toString());

                                        ArrayList<String> etymology = new ArrayList<>();

                                        if (parse.get("results") != null) {
                                            ArrayList<Map<String, Object>> results = (ArrayList<Map<String, Object>>) parse.get("results");
                                            if (results.get(0).get("lexicalEntries") != null) {
                                                ArrayList<Map<String, Object>> lexicalEntries = (ArrayList<Map<String, Object>>) results.get(0).get("lexicalEntries");
                                                if (lexicalEntries.get(0).get("sentences") != null) {
                                                    ArrayList<Map<String, Object>> entries = (ArrayList<Map<String, Object>>) lexicalEntries.get(0).get("sentences");

                                                    for (Map<String, Object> map : entries) {
                                                        etymology.add((String) map.get("text"));
                                                    }
                                                }
                                            }


                                        }


                                        ArrayList<Object> arrayList = new ArrayList<>();
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("id", word);
                                        data.put("sentences", etymology);
                                        arrayList.add(data);
                                        db.collection("sentences").document(word)
                                                .set(data);
                                        activity.runOnUiThread(() -> {
                                            String jsonText = JSONValue.toJSONString(arrayList);
//                                        System.out.println(jsonText);
                                            webView.evaluateJavascript("javascript:getSentences(" + jsonText + ")", s ->
                                                    {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                                                    }
                                            );
                                        });


//

                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();

                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();

                                    } catch (IOException e) {
                                        e.printStackTrace();


                                    }
                                }).start();

                            }
                        }
                    });
        }
    }


    @JavascriptInterface
    public void getEtymology(String word) {
        if (isNetworkAvailable(context)) {
            db.collection("etymologies").document(word.trim().toLowerCase())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {

                                Map<String, Object> data = new HashMap<>();
                                ArrayList<Object> arrayList = new ArrayList<>();
                                DocumentSnapshot result = task.getResult();
                                data.put("id", result.get("id"));
                                data.put("etymology", result.get("etymology"));
                                arrayList.add(data);

                                String jsonText = JSONValue.toJSONString(arrayList);
//                            System.out.println(jsonText);
                                webView.evaluateJavascript("javascript:getEtymology(" + jsonText + ")", s ->
                                        {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                                        }
                                );
                            } else {

                                new Thread(() -> {
                                    URL url;
                                    StringBuilder stringBuilder = new StringBuilder();
                                    try {
                                        url = new URL(dictionaryEntries(word, "etymologies"));
                                        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                                        urlConnection.setRequestProperty("Accept", "application/json");
                                        urlConnection.setRequestProperty("app_id", app_id);
                                        urlConnection.setRequestProperty("app_key", app_key);

                                        // read the output from the server
                                        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));


                                        String line = null;
                                        while ((line = reader.readLine()) != null) {
                                            stringBuilder.append(line + "\n");
                                        }
                                        Map<Object, Object> parse = (Map<Object, Object>) JSONValue.parse(stringBuilder.toString());

                                        ArrayList<String> etymology = new ArrayList<>();

                                        if (parse.get("results") != null) {
                                            ArrayList<Map<String, Object>> results = (ArrayList<Map<String, Object>>) parse.get("results");
                                            if (results.get(0).get("lexicalEntries") != null) {
                                                ArrayList<Map<String, Object>> lexicalEntries = (ArrayList<Map<String, Object>>) results.get(0).get("lexicalEntries");
                                                if (lexicalEntries.get(0).get("entries") != null) {
                                                    ArrayList<Map<String, Object>> entries = (ArrayList<Map<String, Object>>) lexicalEntries.get(0).get("entries");
                                                    if (lexicalEntries.get(0).get("entries") != null) {
                                                        ArrayList<String> etymologies = (ArrayList<String>) entries.get(0).get("etymologies");
                                                        if (entries.get(0).get("etymologies") != null) {
                                                            etymology.addAll(etymologies);
                                                        }

                                                    }
                                                }
                                            }
                                        }


                                        ArrayList<Object> arrayList = new ArrayList<>();
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("id", word);
                                        data.put("etymology", etymology);
                                        arrayList.add(data);
                                        db.collection("etymologies").document(word)
                                                .set(data);
                                        activity.runOnUiThread(() -> {
                                            String jsonText = JSONValue.toJSONString(arrayList);
//                                        System.out.println(jsonText);
                                            webView.evaluateJavascript("javascript:getEtymology(" + jsonText + ")", s ->
                                                    {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                                                    }
                                            );
                                        });


//

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }).start();

                            }
                        }
                    });
        }
    }


    @JavascriptInterface
    public void getDefinition(String word) {

        if (isNetworkAvailable(context) && migration<30) {
            db.collection(defaultLang).document(word.trim().toLowerCase())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {

                                if (task.getResult().get("definitions") == null) {
                                    new Thread(() -> {
                                        URL url;
                                        StringBuilder stringBuilder = new StringBuilder();
                                        try {
                                            url = new URL(dictionaryEntries(word, "definitions"));
                                            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                                            urlConnection.setRequestProperty("Accept", "application/json");
                                            urlConnection.setRequestProperty("app_id", app_id);
                                            urlConnection.setRequestProperty("app_key", app_key);

                                            // read the output from the server
                                            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));


                                            String line = null;
                                            while ((line = reader.readLine()) != null) {
                                                stringBuilder.append(line + "\n");
                                            }
                                            Map<Object, Object> parse = (Map<Object, Object>) JSONValue.parse(stringBuilder.toString());

                                            ArrayList<String> definitions = new ArrayList<>();
                                            ArrayList<String> lexicalCategory = new ArrayList<>();

                                            ArrayList<Map<Object, Object>> d = new ArrayList<>();
                                            d.addAll((Collection<? extends Map<Object, Object>>) parse.get("results"));

                                            if (parse.get("results") != null) {
                                                for (int a = 0; a < d.size(); a++) {
                                                    ArrayList<Object> lexicalEntries = (ArrayList<Object>) d.get(a).get("lexicalEntries");

                                                    Map<Object, Object> o = (Map<Object, Object>) lexicalEntries.get(0);
                                                    ArrayList<Object> entries = (ArrayList<Object>) o.get("entries");


                                                    if (o.get("entries") != null) {
                                                        Map<Object, Object> lexicalCat = (Map<Object, Object>) o.get("lexicalCategory");
                                                        Map<Object, Object> o1 = (Map<Object, Object>) entries.get(0);
                                                        if (o1.get("senses") != null) {
                                                            ArrayList<Map<Object, Object>> senses = (ArrayList<Map<Object, Object>>) o1.get("senses");
                                                            for (Map<Object, Object> objects : senses) {
                                                                if (objects.get("definitions") != null) {
                                                                    ArrayList<Object> definitions1 = (ArrayList<Object>) objects.get("definitions");
                                                                    for (Object ob : definitions1) {
                                                                        definitions.add(ob.toString());
                                                                        lexicalCategory.add((String) lexicalCat.get("id"));
                                                                    }
                                                                }


                                                                ArrayList<Map<Object, Object>> subsenses = (ArrayList<Map<Object, Object>>) objects.get("subsenses");

                                                                if (subsenses != null) {
                                                                    for (Object obj : subsenses) {
                                                                        Map<Object, Object> obj1 = (Map<Object, Object>) obj;
                                                                        if (obj1.get("definitions") == null) {
                                                                            ArrayList<Object> definitions2 = (ArrayList<Object>) obj1.get("definitions");
                                                                            for (Object e : definitions2) {
                                                                                definitions.add(e.toString());
                                                                                lexicalCategory.add((String) lexicalCat.get("id"));
                                                                            }
                                                                        }

                                                                    }
                                                                }


                                                            }
                                                        }
                                                    }


                                                }
                                            }

                                            ArrayList<String> stringArrayList = new ArrayList<>();
                                            ArrayList<String> stringArrayList1 = new ArrayList<>();

                                            if (task.getResult().get("definitions") != null && task.getResult().get("categories") != null) {
                                                stringArrayList.addAll((ArrayList<String>) task.getResult().get("definitions"));
                                                stringArrayList1.addAll((ArrayList<String>) task.getResult().get("categories"));
                                            }

                                            for (int i = 0; i < lexicalCategory.size(); i++) {
                                                if (!stringArrayList.contains(definitions.get(i))) {
                                                    stringArrayList.add(definitions.get(i));
                                                    stringArrayList1.add(lexicalCategory.get(i));
                                                }
                                            }

                                            ArrayList<HashMap<String, Object>> arrayList = new ArrayList<>();
                                            HashMap<String, Object> objectHashMap = new HashMap<>();
                                            objectHashMap.put("definitions", stringArrayList);
                                            objectHashMap.put("date", new Date().getTime());
                                            objectHashMap.put("id", word);
                                            objectHashMap.put("categories", stringArrayList1);

                                            arrayList.add(objectHashMap);


                                            if (definitions.size() > 0) {
                                                databaseAccess.setHistory(new Trending(word, definitions.get(0)));
                                            } else {
                                                definitions.add("Please, we are sorry to let you know that we did not get the definition of your searched word from our database, but other functionality may be available.");
                                            }

                                            activity.runOnUiThread(() -> {
                                                String jsonText = JSONValue.toJSONString(arrayList);
                                                webView.evaluateJavascript("javascript:getData(" + jsonText + ")", s ->
                                                        {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                                                        }
                                                );
                                            });


                                            db.collection(defaultLang).document(word)
                                                    .update(objectHashMap);


//

                                        } catch (MalformedURLException e) {
                                            e.printStackTrace();
                                            activity.runOnUiThread(() -> {
                                                webView.evaluateJavascript("javascript:toastError()", s ->
                                                        {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                                                        }
                                                );
                                            });
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                            activity.runOnUiThread(() -> {
                                                webView.evaluateJavascript("javascript:toastError()", s ->
                                                        {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                                                        }
                                                );
                                            });
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            activity.runOnUiThread(() -> {
                                                webView.evaluateJavascript("javascript:toastError()", s ->
                                                        {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                                                        }
                                                );
                                            });

                                        }
                                    }).start();
                                } else {

                                    ArrayList<HashMap<String, Object>> arrayList = new ArrayList<>();
                                    HashMap<String, Object> objectHashMap = new HashMap<>();
                                    objectHashMap.put("definitions", task.getResult().get("definitions"));
                                    objectHashMap.put("date", new Date().getTime());
                                    objectHashMap.put("id", word);
                                    objectHashMap.put("categories", task.getResult().get("categories"));

                                    arrayList.add(objectHashMap);

                                    activity.runOnUiThread(() -> {
                                        String jsonText = JSONValue.toJSONString(arrayList);
                                        webView.evaluateJavascript("javascript:getData(" + jsonText + ")", s ->
                                                {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                                                }
                                        );
                                    });
                                    HashMap<String, Object> updateObject = new HashMap<>();
                                    updateObject.put("date", new Date().getTime());
                                    db.collection(defaultLang).document(word)
                                            .update(updateObject);
                                }


                            } else {

                                new Thread(() -> {
                                    URL url;
                                    StringBuilder stringBuilder = new StringBuilder();
                                    try {
                                        url = new URL(dictionaryEntries(word, "definitions"));
                                        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                                        urlConnection.setRequestProperty("Accept", "application/json");
                                        urlConnection.setRequestProperty("app_id", app_id);
                                        urlConnection.setRequestProperty("app_key", app_key);

                                        // read the output from the server
                                        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));


                                        String line = null;
                                        while ((line = reader.readLine()) != null) {
                                            stringBuilder.append(line + "\n");
                                        }
                                        Map<Object, Object> parse = (Map<Object, Object>) JSONValue.parse(stringBuilder.toString());

                                        ArrayList<String> definitions = new ArrayList<>();
                                        ArrayList<String> lexicalCategory = new ArrayList<>();

                                        ArrayList<Map<Object, Object>> d = new ArrayList<>();
                                        d.addAll((Collection<? extends Map<Object, Object>>) parse.get("results"));

                                        if (parse.get("results") != null) {
                                            for (int a = 0; a < d.size(); a++) {
                                                ArrayList<Object> lexicalEntries = (ArrayList<Object>) d.get(a).get("lexicalEntries");

                                                Map<Object, Object> o = (Map<Object, Object>) lexicalEntries.get(0);
                                                ArrayList<Object> entries = (ArrayList<Object>) o.get("entries");


                                                if (o.get("entries") != null) {
                                                    Map<Object, Object> lexicalCat = (Map<Object, Object>) o.get("lexicalCategory");
                                                    Map<Object, Object> o1 = (Map<Object, Object>) entries.get(0);
                                                    if (o1.get("senses") != null) {
                                                        ArrayList<Map<Object, Object>> senses = (ArrayList<Map<Object, Object>>) o1.get("senses");
                                                        for (Map<Object, Object> objects : senses) {
                                                            if (objects.get("definitions") != null) {
                                                                ArrayList<Object> definitions1 = (ArrayList<Object>) objects.get("definitions");
                                                                for (Object ob : definitions1) {
                                                                    definitions.add(ob.toString());
                                                                    lexicalCategory.add((String) lexicalCat.get("id"));
                                                                }
                                                            }


                                                            ArrayList<Map<Object, Object>> subsenses = (ArrayList<Map<Object, Object>>) objects.get("subsenses");

                                                            if (subsenses != null) {
                                                                for (Object obj : subsenses) {
                                                                    Map<Object, Object> obj1 = (Map<Object, Object>) obj;
                                                                    if (obj1.get("definitions") == null) {
                                                                        ArrayList<Object> definitions2 = (ArrayList<Object>) obj1.get("definitions");
                                                                        for (Object e : definitions2) {
                                                                            definitions.add(e.toString());
                                                                            lexicalCategory.add((String) lexicalCat.get("id"));
                                                                        }
                                                                    }

                                                                }
                                                            }


                                                        }
                                                    }
                                                }


                                            }
                                        }


                                        ArrayList<Object> arrayList = new ArrayList<>();
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("id", word);
                                        data.put("date", new Date().getTime());
                                        data.put("view", 1);
                                        data.put("definitions", definitions);
                                        data.put("categories", lexicalCategory);
//                                    System.out.println(definitions.get(0));
                                        if (definitions.size() > 0) {
                                            databaseAccess.setHistory(new Trending(word, definitions.get(0)));
                                        } else {
                                            definitions.add("Please, we are sorry to let you know that we did not get the definition of your searched word from our database, but other functionality may be available.");
                                        }

                                        arrayList.add(data);

                                        activity.runOnUiThread(() -> {
                                            String jsonText = JSONValue.toJSONString(arrayList);
                                            webView.evaluateJavascript("javascript:getData(" + jsonText + ")", s ->
                                                    {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                                                    }
                                            );
                                        });


                                        db.collection(defaultLang).document(word)
                                                .set(data);


//

                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                        activity.runOnUiThread(() -> {
                                            webView.evaluateJavascript("javascript:toastError()", s ->
                                                    {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                                                    }
                                            );
                                        });
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                        activity.runOnUiThread(() -> {
                                            webView.evaluateJavascript("javascript:toastError()", s ->
                                                    {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                                                    }
                                            );
                                        });
                                    } catch (IOException e) {
                                        e.printStackTrace();


                                        activity.runOnUiThread(() -> {
                                            webView.evaluateJavascript("javascript:toastError()", s ->
                                                    {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                                                    }
                                            );
                                        });

                                    }
                                }).start();

                            }
                        }
                    });
        } else {
            //offline code
            if(migration < 3){
                final Definition definition = databaseAccess.getDefinition(word);
                final Category category = databaseAccess.getCategory(word);
                ArrayList<String> parseDefinition = new ArrayList<>();
                ArrayList<String> parseCategory = new ArrayList<>();
                if (definition != null && category != null) {
                    parseDefinition.addAll((ArrayList<String>) JSONValue.parse(definition.getDefinition()));
                    parseCategory.addAll((ArrayList<String>) JSONValue.parse(category.getCategory()));
                }

                if (parseDefinition != null && parseDefinition.size() > 0) {
                    databaseAccess.setHistory(new Trending(word, parseDefinition.get(0)));
                } else {
                    parseDefinition.add("Please, we are sorry to let you know that we did not get the definition of your searched word from your local database, please consider switching to online mode");
                }

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("definitions", parseDefinition);
                hashMap.put("id", word);
                hashMap.put("date", new Date().getTime());
                hashMap.put("categories", parseCategory);

                ArrayList<HashMap<String, Object>> arrayList = new ArrayList<>();
                arrayList.add(hashMap);

                activity.runOnUiThread(() -> {
                    String jsonText = JSONValue.toJSONString(arrayList);
                    webView.evaluateJavascript("javascript:getData(" + jsonText + ")", s ->
                            {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                            }
                    );
                });
            }else{
                Toast.makeText(context, "Not ready for offline use", Toast.LENGTH_LONG).show();
            }


        }

    }





    @JavascriptInterface
    public void getPronunciation(String word) {
        if (isNetworkAvailable(context) && migration<30) {
            db.collection("pronunciation").document(word.trim().toLowerCase())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {

                                Map<String, Object> data = new HashMap<>();
                                ArrayList<Object> arrayList = new ArrayList<>();
                                DocumentSnapshot result = task.getResult();
                                data.put("id", result.get("id"));
                                data.put("phonetics", result.get("phonetics"));
                                data.put("phoneticSpelling", result.get("phoneticSpelling"));
                                arrayList.add(data);
                                String jsonText = JSONValue.toJSONString(arrayList);
                                webView.evaluateJavascript("javascript:pronounce(" + jsonText + ")", s ->
                                        {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                                        }
                                );

                            } else {
                                new Thread(() -> {

                                    URL url;
                                    StringBuilder stringBuilder = new StringBuilder();
                                    try {
                                        url = new URL(dictionaryEntriesPrimary(word));
                                        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                                        urlConnection.setRequestProperty("Accept", "application/json");

                                        // read the output from the server
                                        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));


                                        String line = null;
                                        while ((line = reader.readLine()) != null) {
                                            stringBuilder.append(line + "\n");
                                        }

                                        ArrayList<Object> audios = new ArrayList<>();
                                        ArrayList<Object> phonetics = new ArrayList<>();
                                        ArrayList<Map<Object, Object>> parse = (ArrayList<Map<Object, Object>>) JSONValue.parse(stringBuilder.toString());
                                        final ArrayList<Object> meanings = (ArrayList<Object>) parse.get(0).get("phonetics");
                                        String phoneticSpelling = "";
                                        for (Object ob : meanings) {
                                            Map<Object, Object> means = (Map<Object, Object>) ob;
                                            final String phonetic = String.valueOf(means.get("text"));
                                            final String audio = String.valueOf(means.get("audio"));

                                            if (audio.length() > 5) {
                                                audios.add(audio);
                                            }

                                            if (phonetic != null) {
                                                phonetics.add(phonetic);
                                                phoneticSpelling = phonetic;
                                            }
                                        }

                                        Map<String, Object> data = new HashMap<>();
                                        data.put("id", word);
                                        data.put("audioFile", audios);
                                        data.put("phonetics", phonetics);
                                        data.put("phoneticSpelling", phoneticSpelling);
                                        ArrayList<Object> arrayList7 = new ArrayList<>();
                                        arrayList7.add(data);
                                        activity.runOnUiThread(() -> {
                                            String jsonText = JSONValue.toJSONString(arrayList7);
                                            webView.evaluateJavascript("javascript:pronounce(" + jsonText + ")", s ->
                                                    {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                                                    }
                                            );
                                        });

                                        db.collection("pronunciation").document(word)
                                                .set(data);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }).start();

                            }
                        }
                    });
        } else {
            if(migration < 3){
                Pronunciation pronunciation = databaseAccess.getPronunciation(word);
                Map<String, Object> data = new HashMap<>();
                ArrayList<Object> arrayList = new ArrayList<>();
                if (pronunciation != null) {
                    String[] phonics = pronunciation.getPhonics().split("_");
                    ArrayList<String> arrayList1 = new ArrayList<>(Arrays.asList(phonics));
                    data.put("id", pronunciation.getWord());
                    data.put("phonetics", arrayList1);
                    data.put("phoneticSpelling", phonics.length > 0 ? phonics[0] : null);
                } else {
                    data.put("id", word);
                    data.put("phonetics", null);
                    data.put("phoneticSpelling", null);
                }
                arrayList.add(data);
                String jsonText = JSONValue.toJSONString(arrayList);
                activity.runOnUiThread(() -> webView.evaluateJavascript("javascript:pronounce(" + jsonText + ")", s ->
                        {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                        }
                ));
            }else{
                Toast.makeText(context, "Not ready for offline use", Toast.LENGTH_LONG).show();
            }
            //offline code

        }

    }



    @JavascriptInterface
    public void getDefinitionMerged(String word) {
        if (isNetworkAvailable(context) && migration<30) {

            db.collection(defaultLang).document(word.trim().toLowerCase())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {

                                new Thread(() -> {
                                    URL url;
                                    StringBuilder stringBuilder = new StringBuilder();
                                    try {
                                        url = new URL(dictionaryEntries(word, "definitions"));
                                        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                                        urlConnection.setRequestProperty("Accept", "application/json");
                                        urlConnection.setRequestProperty("app_id", app_id);
                                        urlConnection.setRequestProperty("app_key", app_key);

                                        // read the output from the server
                                        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));


                                        String line = null;
                                        while ((line = reader.readLine()) != null) {
                                            stringBuilder.append(line + "\n");
                                        }
                                        Map<Object, Object> parse = (Map<Object, Object>) JSONValue.parse(stringBuilder.toString());

                                        ArrayList<String> definitions = new ArrayList<>();
                                        ArrayList<String> lexicalCategory = new ArrayList<>();

                                        ArrayList<Map<Object, Object>> d = new ArrayList<>();
                                        d.addAll((Collection<? extends Map<Object, Object>>) parse.get("results"));

                                        if (parse.get("results") != null) {
                                            for (int a = 0; a < d.size(); a++) {
                                                ArrayList<Object> lexicalEntries = (ArrayList<Object>) d.get(a).get("lexicalEntries");

                                                Map<Object, Object> o = (Map<Object, Object>) lexicalEntries.get(0);
                                                ArrayList<Object> entries = (ArrayList<Object>) o.get("entries");


                                                if (o.get("entries") != null) {
                                                    Map<Object, Object> lexicalCat = (Map<Object, Object>) o.get("lexicalCategory");
                                                    Map<Object, Object> o1 = (Map<Object, Object>) entries.get(0);
                                                    if (o1.get("senses") != null) {
                                                        ArrayList<Map<Object, Object>> senses = (ArrayList<Map<Object, Object>>) o1.get("senses");
                                                        for (Map<Object, Object> objects : senses) {
                                                            if (objects.get("definitions") != null) {
                                                                ArrayList<Object> definitions1 = (ArrayList<Object>) objects.get("definitions");
                                                                for (Object ob : definitions1) {
                                                                    definitions.add(ob.toString());
                                                                    lexicalCategory.add((String) lexicalCat.get("id"));
                                                                }
                                                            }


                                                            ArrayList<Map<Object, Object>> subsenses = (ArrayList<Map<Object, Object>>) objects.get("subsenses");

                                                            if (subsenses != null) {
                                                                for (Object obj : subsenses) {
                                                                    Map<Object, Object> obj1 = (Map<Object, Object>) obj;
                                                                    if (obj1.get("definitions") == null) {
                                                                        ArrayList<Object> definitions2 = (ArrayList<Object>) obj1.get("definitions");
                                                                        for (Object e : definitions2) {
                                                                            definitions.add(e.toString());
                                                                            lexicalCategory.add((String) lexicalCat.get("id"));
                                                                        }
                                                                    }

                                                                }
                                                            }


                                                        }
                                                    }
                                                }


                                            }
                                        }

                                        ArrayList<String> stringArrayList = new ArrayList<>();
                                        ArrayList<String> stringArrayList1 = new ArrayList<>();

                                        if (task.getResult().get("definitions") != null && task.getResult().get("categories") != null) {
                                            stringArrayList.addAll((ArrayList<String>) task.getResult().get("definitions"));
                                            stringArrayList1.addAll((ArrayList<String>) task.getResult().get("categories"));
                                        }

                                        for (int i = 0; i < lexicalCategory.size(); i++) {
                                            if (!stringArrayList.contains(definitions.get(i))) {
                                                stringArrayList.add(definitions.get(i));
                                                stringArrayList1.add(lexicalCategory.get(i));
                                            }
                                        }


                                        HashMap<String, Object> objectHashMap = new HashMap<>();
                                        objectHashMap.put("definitions", stringArrayList);
                                        objectHashMap.put("date", new Date().getTime());
                                        objectHashMap.put("id", word);
                                        objectHashMap.put("categories", stringArrayList1);

                                        if (!stringArrayList1.isEmpty()) {
                                            ArrayList<String> a = (ArrayList<String>) task.getResult().get("definitions");
                                            databaseAccess.setHistory(new Trending(word, a.get(0)));

                                            getExample(word, objectHashMap);
                                        }

                                        db.collection(defaultLang).document(word)
                                                .update(objectHashMap);


//

                                    } catch (Exception e) {

                                        if (task.getResult().get("definitions") != null) {


                                            HashMap<String, Object> objectHashMap = new HashMap<>();
                                            objectHashMap.put("definitions", task.getResult().get("definitions"));
                                            objectHashMap.put("date", new Date().getTime());
                                            objectHashMap.put("id", word);
                                            objectHashMap.put("categories", task.getResult().get("categories"));
                                            getExample(word, objectHashMap);
                                            ArrayList<String> a = (ArrayList<String>) task.getResult().get("definitions");
                                            databaseAccess.setHistory(new Trending(word, a.get(0)));
                                            db.collection(defaultLang).document(word)
                                                    .update(objectHashMap);
                                        } else {
                                            activity.runOnUiThread(() -> {
                                                webView.evaluateJavascript("javascript:toastError()", s ->
                                                        {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                                                        }
                                                );
                                            });
                                        }
//
                                    }
                                }).start();


                            } else {
                                activity.runOnUiThread(() -> webView.evaluateJavascript("javascript:toastError()", s ->
                                        {
//                                                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                                        }
                                ));

                            }
                        }
                    });

        } else {

            if(migration < 3){
                final Definition definition = databaseAccess.getDefinition(word);
                final Category category = databaseAccess.getCategory(word);
                ArrayList<String> parseDefinition = (ArrayList<String>) JSONValue.parse(definition.getDefinition());
                ArrayList<String> parseCategory = (ArrayList<String>) JSONValue.parse(category.getCategory());

                HashMap<String, Object> objectHashMap = new HashMap<>();
                objectHashMap.put("definitions", parseDefinition);
                objectHashMap.put("date", new Date().getTime());
                objectHashMap.put("id", word);
                objectHashMap.put("categories", parseCategory);

                if (!parseCategory.isEmpty()) {
                    databaseAccess.setHistory(new Trending(word, parseDefinition.get(0)));
                    getExample(word, objectHashMap);
                }
            }else{
                Toast.makeText(context, "Not ready for offline use", Toast.LENGTH_LONG).show();
            }


        }
    }



    @JavascriptInterface
    public void NoNetWork() {
        //Setting message manually and performing action on button click
        builder.setMessage("Please, You don't have internet access.")
                .setCancelable(false)
                .setPositiveButton("GO OFFLINE", (dialog, id) -> dialog.cancel())
                .setNegativeButton("CHECK NETWORK", (dialog, id) -> {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setComponent(new ComponentName("com.android.settings",
                            "com.android.settings.Settings$DataUsageSummaryActivity"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);

                    dialog.cancel();
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Internet Access");
        alert.show();
    }
    private String dictionaryEntriesPrimary(String word) {
        final String word_id = word.toLowerCase();
        return "https://api.dictionaryapi.dev/api/v2/entries/en/"+word_id;
    }
    private String dictionaryEntries(String word, String fields) {
        final String language = activeLange;
        final String strictMatch = "false";
        final String word_id = word.toLowerCase();
        return "https://od-api.oxforddictionaries.com:443/api/v2/entries/" + language + "/" + word_id + "?" + "fields=" + fields + "&strictMatch=" + strictMatch;
    }

    private String dictionarySentence(String word) {
        final String word_id = word.toLowerCase();
        return "https://od-api.oxforddictionaries.com/api/v2/sentences/en/" + word_id + "?strictMatch=false";
    }

    private String dictionaryInflections(String word) {
        final String language = activeLange;
        final String word_id = word.toLowerCase();
        return "https://od-api.oxforddictionaries.com/api/v2/inflections/" + language + "/" + word_id + "?strictMatch=false";
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
