package gd.rf.tekporconsult.mypronouncer.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;

import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

import gd.rf.tekporconsult.mypronouncer.model.Category;
import gd.rf.tekporconsult.mypronouncer.model.Definition;
import gd.rf.tekporconsult.mypronouncer.model.Example;
import gd.rf.tekporconsult.mypronouncer.model.MigrationHistory;
import gd.rf.tekporconsult.mypronouncer.model.Pronunciation;
import gd.rf.tekporconsult.mypronouncer.model.Word;

public class OfflineService extends Service {
    public OfflineService() {
    }

    public  class MyBinder extends Binder{
     public OfflineService MyNewBinder(){
            return OfflineService.this;
        }
    }




    ArrayList<MigrationHistory> history;
    private MyBinder myBinder =  new MyBinder();
     private int progress = 0;
     public static boolean continueTread = true;
    DatabaseAccess databaseAccess;
    String[] phonics = {"https://firebasestorage.googleapis.com/v0/b/mypronouncer.appspot.com/o/data%2Fphonic1.json?alt=media&token=0de1d6a0-7b20-4eb2-855e-d78a1e43436e",
            "https://firebasestorage.googleapis.com/v0/b/mypronouncer.appspot.com/o/data%2Fphonic2.json?alt=media&token=ae169e8d-a35c-4ccb-ba70-53cedb702221",
            "https://firebasestorage.googleapis.com/v0/b/mypronouncer.appspot.com/o/data%2Fphonic3.json?alt=media&token=890384e3-b78b-4685-8fe4-4e599f45a7c1",
            "https://firebasestorage.googleapis.com/v0/b/mypronouncer.appspot.com/o/data%2Fphonic4.json?alt=media&token=7a3b480d-2432-43e4-91e0-2fbe246f1a22",
            "https://firebasestorage.googleapis.com/v0/b/mypronouncer.appspot.com/o/data%2Fphonic5.json?alt=media&token=b21a01d7-70d4-4431-b817-24a6d20cd2da"};

    String[] data = {"https://firebasestorage.googleapis.com/v0/b/mypronouncer.appspot.com/o/data%2Fdata1.json?alt=media&token=ba06a41c-e71e-4fee-bb46-ea7c5d637f7b",
            "https://firebasestorage.googleapis.com/v0/b/mypronouncer.appspot.com/o/data%2Fdata2.json?alt=media&token=26b49911-08c1-44fe-baa8-145c6960e12d",
    "https://firebasestorage.googleapis.com/v0/b/mypronouncer.appspot.com/o/data%2Fdata3.json?alt=media&token=5451f346-9785-41f7-ab1c-55b1d70fd9a0",
    "https://firebasestorage.googleapis.com/v0/b/mypronouncer.appspot.com/o/data%2Fdata4.json?alt=media&token=cdc9c890-da92-4ab3-895e-a883f742fae2",
    "https://firebasestorage.googleapis.com/v0/b/mypronouncer.appspot.com/o/data%2Fdata5.json?alt=media&token=8ef6106e-e3bc-40e8-8360-0f048fcf92ed",
    "https://firebasestorage.googleapis.com/v0/b/mypronouncer.appspot.com/o/data%2Fdata6.json?alt=media&token=d249e944-3f57-4931-a2d7-4e4ebc575615",
    "https://firebasestorage.googleapis.com/v0/b/mypronouncer.appspot.com/o/data%2Fdata7.json?alt=media&token=1cf97a80-7409-40ad-b48d-4fb32be38012","" +
            "https://firebasestorage.googleapis.com/v0/b/mypronouncer.appspot.com/o/data%2Fdata8.json?alt=media&token=46098671-f7db-472d-b05f-9818f9e9ef33",
    "https://firebasestorage.googleapis.com/v0/b/mypronouncer.appspot.com/o/data%2Fdata9.json?alt=media&token=0891bf97-3c9d-41d8-976b-e47dff5aa50a",
    "https://firebasestorage.googleapis.com/v0/b/mypronouncer.appspot.com/o/data%2Fdata10.json?alt=media&token=789a8624-33c7-4c48-ae4d-41177ec5b850",
    "https://firebasestorage.googleapis.com/v0/b/mypronouncer.appspot.com/o/data%2Fdata11.json?alt=media&token=1e3b5e4d-eb97-4531-b21d-7e40b20d8ef8",
    "https://firebasestorage.googleapis.com/v0/b/mypronouncer.appspot.com/o/data%2Fdata12.json?alt=media&token=396ef71e-43cb-47eb-ac6b-08808aaaabbc",
    "https://firebasestorage.googleapis.com/v0/b/mypronouncer.appspot.com/o/data%2Fdata13.json?alt=media&token=ffe06d2e-0cf2-4358-8697-cdeb1fd92aab"};

    String[] data2 = {"https://firebasestorage.googleapis.com/v0/b/mypronouncer.appspot.com/o/data%2Fdata14.json?alt=media&token=549425d0-e43f-4324-9da8-a60981e2927d",
            "https://firebasestorage.googleapis.com/v0/b/mypronouncer.appspot.com/o/data%2Fdata15.json?alt=media&token=eeaf7dba-51c2-4986-8e22-4cc38acd55de",
            "https://firebasestorage.googleapis.com/v0/b/mypronouncer.appspot.com/o/data%2Fdata16.json?alt=media&token=4aa60b84-130e-4240-972a-e0fefc29f584",
            "https://firebasestorage.googleapis.com/v0/b/mypronouncer.appspot.com/o/data%2Fdata17.json?alt=media&token=38a19b48-3ec8-4a84-8275-dfae57b7b9e9",
            "https://firebasestorage.googleapis.com/v0/b/mypronouncer.appspot.com/o/data%2Fdata18.json?alt=media&token=cf61b69e-32a8-40d6-b328-b06a7d984916",
            "https://firebasestorage.googleapis.com/v0/b/mypronouncer.appspot.com/o/data%2Fdata19.json?alt=media&token=1083d1f4-08d2-4151-afae-e1ebbc971672",
            "https://firebasestorage.googleapis.com/v0/b/mypronouncer.appspot.com/o/data%2Fdata20.json?alt=media&token=c75acee0-13a0-4467-b3d4-ee58168b48d4",
            "https://firebasestorage.googleapis.com/v0/b/mypronouncer.appspot.com/o/data%2Fdata21.json?alt=media&token=3b34eace-16b7-47ea-90dd-aae098a5c08b",
            "https://firebasestorage.googleapis.com/v0/b/mypronouncer.appspot.com/o/data%2Fdata22.json?alt=media&token=e9d253d5-e04f-4fdc-876d-6f55db333178",
            "https://firebasestorage.googleapis.com/v0/b/mypronouncer.appspot.com/o/data%2Fdata23.json?alt=media&token=2b2df1fa-08a4-4154-b9f3-1ecc80cc21eb",
            "https://firebasestorage.googleapis.com/v0/b/mypronouncer.appspot.com/o/data%2Fdata24.json?alt=media&token=096e6446-6810-4690-a64e-d01c1c67175e",
            "https://firebasestorage.googleapis.com/v0/b/mypronouncer.appspot.com/o/data%2Fdata25.json?alt=media&token=5016bd47-2410-4154-92e4-038c19bab5d2",
            "https://firebasestorage.googleapis.com/v0/b/mypronouncer.appspot.com/o/data%2Fdata26.json?alt=media&token=d82bba2d-2085-421e-9bec-2a88d6bb9aaa",
            "https://firebasestorage.googleapis.com/v0/b/mypronouncer.appspot.com/o/data%2Fdata27.json?alt=media&token=37431a67-9279-426a-8419-99d273182ff3"};

    int dict = 0;
    int dict2 = 0;
    int pho = 0;
    int dictLeft = data.length-1;
    int dictLeft2 = data2.length-1;
    int phoLeft = phonics.length-1;

    @Override
    public void onCreate() {
        super.onCreate();
        databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        history = new ArrayList<>();

        history.addAll(databaseAccess.getAllMigrationHistory());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        continueTread = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(!continueTread){
            stopSelf();
        }

       new Thread(() ->
       {

           setDict(dict,true);
//           setDict2(dict2,true);
//           setPho(pho,true);
//
           setDict(dictLeft,false);
//           setDict2(dictLeft2,false);
//           setPho(phoLeft,false);

       }
               ).start();
        return Service.START_REDELIVER_INTENT;
    }

    public void setDict(int dict,Boolean direction) {
        setDefinitionsAndExamples(data[dict],direction);
    }

    public void setDict2(int dict,Boolean direction) {
        setDefinitionsAndExamples1(data2[dict],direction);
    }


    public void setPho(int pho,Boolean direction) {
        setPronunciationsNow(phonics[pho],direction);
        }

    public void setPronunciationsNow(String something,boolean direction){
        new Thread(() -> {
            URL url;
            StringBuilder stringBuilder = new StringBuilder();
            try {
                url = new URL(something);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Accept", "application/json");
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                    if(!continueTread){
                        break;
                    }
                }
                ArrayList<Map<Object, Object>> parse = (ArrayList<Map<Object, Object>>) JSONValue.parse(stringBuilder.toString());

                MigrationHistory migrationHistory = getMigration(something);
               if(migrationHistory != null){
                   for(int i = migrationHistory.getAt(); i < parse.size(); i++){
                       Map<Object, Object> map  =  parse.get(i);
                       String word = String.valueOf(map.get("word")).toLowerCase();
                       String IPAS = (String) map.get("IPA");

                       if(word == null) continue;
                       if(word.contains("/")) continue;
                       if(word.charAt(0) == '-') continue;
                       Pronunciation pronunciation = new Pronunciation(word,IPAS);
                       databaseAccess.setPronunciation(pronunciation);
                       migrationHistory.setAt(i);
                       databaseAccess.updateMigrationHistory(migrationHistory);
                       if(!continueTread){
                           break;
                       }
                   }

               }else{
                   MigrationHistory migrationHistory1 = new MigrationHistory(something,0,parse.size(),new Date().getTime(),"pronunciation");
                   databaseAccess.setMigrationHistory(migrationHistory1);
                   history.add(migrationHistory1);
                   for(int i = 0; i < parse.size(); i++){
                       Map<Object, Object> map  =  parse.get(i);
                       String word = String.valueOf(map.get("word")).toLowerCase();
                       String IPAS = (String) map.get("IPA");

                       if(word == null) continue;
                       if(word.contains("/")) continue;
                       if(word.charAt(0) == '-') continue;
                       Pronunciation pronunciation = new Pronunciation(word,IPAS);
                       databaseAccess.setPronunciation(pronunciation);
                       migrationHistory1.setAt(i);
                       databaseAccess.updateMigrationHistory(migrationHistory1);
                       if(!continueTread){
                           break;
                       }
                   }

               }
               if(continueTread){
                   if(direction){
                       pho++;
                       if(phoLeft > pho){
                           setPho(pho,true);
                       }

                   }else {
                       phoLeft--;
                       if(phoLeft > pho){
                           setPho(phoLeft,false);
                       }

                   }
               }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }





    public void setDefinitionsAndExamples(String something,Boolean direction){
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
                    if(!continueTread){
                        break;
                    }
                }

                Map<String,Object> objectMap = (Map<String, Object>) JSONValue.parse(stringBuilder.toString());
                ArrayList<Map<Object, Object>> parse = (ArrayList<Map<Object, Object>>) objectMap.get("data");

                MigrationHistory migrationHistory = getMigration(something);
                if(migrationHistory != null){
                    for(int i = migrationHistory.getAt(); i < parse.size(); i++){
                        Map<Object, Object> map  =  parse.get(i);
                        String word = String.valueOf(map.get("id")).toLowerCase();
                        ArrayList<String> definitions = (ArrayList<String>) map.get("definitions");
                        ArrayList<String> categories = (ArrayList<String>) map.get("categories");
                        ArrayList<String> examples = (ArrayList<String>) map.get("examples");
                        Word word1 = new Word(word);

                        Category category = new Category(word, categories.toString());
                        Definition definition1 = new Definition(word,definitions.toString());
                        Example example = new Example(word,examples.toString());
//
                    databaseAccess.setWord(word1);
                    databaseAccess.setCategory(category);
                    databaseAccess.setDefinition(definition1);
                    databaseAccess.setExample(example);
                        migrationHistory.setAt(i);
                        databaseAccess.updateMigrationHistory(migrationHistory);
                        if(!continueTread){
                            break;
                        }
                    }
                }else{

                    MigrationHistory migrationHistory1 = new MigrationHistory(something,0,parse.size(),new Date().getTime(),"dictionary");
                    databaseAccess.setMigrationHistory(migrationHistory1);
                    history.add(migrationHistory1);

                    for(int i = 0; i < parse.size(); i++){
                        Map<Object, Object> map  =  parse.get(i);
                        String word = String.valueOf(map.get("id")).toLowerCase();
                        ArrayList<String> definitions = (ArrayList<String>) map.get("definitions");
                        ArrayList<String> categories = (ArrayList<String>) map.get("categories");
                        ArrayList<String> examples = (ArrayList<String>) map.get("examples");
                        Word word1 = new Word(word);

                        Category category = new Category(word, categories.toString());
                        Definition definition1 = new Definition(word,definitions.toString());
                        Example example = new Example(word,examples.toString());
//
                    databaseAccess.setWord(word1);
                    databaseAccess.setCategory(category);
                    databaseAccess.setDefinition(definition1);
                    databaseAccess.setExample(example);
                        migrationHistory1.setAt(i);
                        databaseAccess.updateMigrationHistory(migrationHistory1);
                        if(!continueTread){
                            break;
                        }

                    }
                }
                if(direction){
                    dict++;
                    if(dict < dictLeft){
                        setDict(dict,true);
                    }
                }else {
                    dictLeft--;
                    if(dict < dictLeft){
                        setDict(dictLeft,false);
                    }

                }




            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }


    public void setDefinitionsAndExamples1(String something,Boolean direction){
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
                    if(!continueTread){
                        break;
                    }
                }

                Map<String,Object> objectMap = (Map<String, Object>) JSONValue.parse(stringBuilder.toString());
                ArrayList<Map<Object, Object>> parse = (ArrayList<Map<Object, Object>>) objectMap.get("data");

                MigrationHistory migrationHistory = getMigration(something);
                if(migrationHistory != null){
                    for(int i = migrationHistory.getAt(); i < parse.size(); i++){
                        Map<Object, Object> map  =  parse.get(i);
                        String word = String.valueOf(map.get("id")).toLowerCase();
                        ArrayList<String> definitions = (ArrayList<String>) map.get("definitions");
                        ArrayList<String> categories = (ArrayList<String>) map.get("categories");
                        ArrayList<String> examples = (ArrayList<String>) map.get("examples");
                        Word word1 = new Word(word);

                        Category category = new Category(word, categories.toString());
                        Definition definition1 = new Definition(word,definitions.toString());
                        Example example = new Example(word,examples.toString());
//
                        databaseAccess.setWord(word1);
                        databaseAccess.setCategory(category);
                        databaseAccess.setDefinition(definition1);
                        databaseAccess.setExample(example);
                        migrationHistory.setAt(i);
                        databaseAccess.updateMigrationHistory(migrationHistory);
                        if(!continueTread){
                            break;
                        }
                    }
                }else{

                    MigrationHistory migrationHistory1 = new MigrationHistory(something,0,parse.size(),new Date().getTime(),"dictionary");
                    databaseAccess.setMigrationHistory(migrationHistory1);
                    history.add(migrationHistory1);

                    for(int i = 0; i < parse.size(); i++){
                        Map<Object, Object> map  =  parse.get(i);
                        String word = String.valueOf(map.get("id")).toLowerCase();
                        ArrayList<String> definitions = (ArrayList<String>) map.get("definitions");
                        ArrayList<String> categories = (ArrayList<String>) map.get("categories");
                        ArrayList<String> examples = (ArrayList<String>) map.get("examples");
                        Word word1 = new Word(word);

                        Category category = new Category(word, categories.toString());
                        Definition definition1 = new Definition(word,definitions.toString());
                        Example example = new Example(word,examples.toString());
//
                        databaseAccess.setWord(word1);
                        databaseAccess.setCategory(category);
                        databaseAccess.setDefinition(definition1);
                        databaseAccess.setExample(example);
                        migrationHistory1.setAt(i);
                        databaseAccess.updateMigrationHistory(migrationHistory1);
                        if(!continueTread){
                            break;
                        }

                    }
                }
                if(direction){
                    dict2++;
                    if(dict2 < dictLeft2){
                        setDict2(dict2,true);
                    }
                }else {
                    dictLeft2--;
                    if(dict2 < dictLeft2){
                        setDict2(dictLeft2,false);
                    }

                }




            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }

    private MigrationHistory getMigration(String url){
        MigrationHistory re = null;
        for(MigrationHistory migrationHistory : history){
            if(url.equals(migrationHistory.getUrl())){
                re = migrationHistory;
                break;
            }
        }
        return re;
    }


    public  int getProgress(){
        return  progress;
    };


}