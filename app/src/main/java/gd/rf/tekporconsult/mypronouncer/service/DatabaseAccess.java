package gd.rf.tekporconsult.mypronouncer.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;

import gd.rf.tekporconsult.mypronouncer.model.Category;
import gd.rf.tekporconsult.mypronouncer.model.Definition;
import gd.rf.tekporconsult.mypronouncer.model.Example;
import gd.rf.tekporconsult.mypronouncer.model.MigrationHistory;
import gd.rf.tekporconsult.mypronouncer.model.Notification;
import gd.rf.tekporconsult.mypronouncer.model.Pronunciation;
import gd.rf.tekporconsult.mypronouncer.model.Transcribe;
import gd.rf.tekporconsult.mypronouncer.model.Trending;
import gd.rf.tekporconsult.mypronouncer.model.Word;


public class DatabaseAccess {
    private static DatabaseAccess instance;
    private final SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;

    /**
     * Private constructor to aboid object creation from outside classes.
     *
     * @param context
     */
    private DatabaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    /**
     * Return a singleton instance of DatabaseAccess.
     *
     * @param context the Context
     * @return the instance of DabaseAccess
     */
    public static DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    /**
     * Open the database connection.
     */
    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    /**
     * Close the database connection.
     */
    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    /**
     * Read all quotes from the database.
     *
     * @return a List of quotes
     */
//get history
    public void setHistory(Trending database1) {
        ContentValues contentValues;
        try {
            contentValues = new ContentValues();
            contentValues.put("word", database1.getWord());
            contentValues.put("definition", database1.getDefinition());

            database.insert("history", null, contentValues);
        } catch (SQLiteConstraintException e) {
            // e.getMessage();
        }

    }

    // set word
    public void setWord(Word word) {
        ContentValues contentValues;
        try {
            contentValues = new ContentValues();
            contentValues.put("word", word.getWord());
            database.insert("words", null, contentValues);
        } catch (SQLiteConstraintException e) {
            // e.getMessage();
        }
    }

    // set category
    public void setCategory(Category category) {
        ContentValues contentValues;
        try {
            contentValues = new ContentValues();
            contentValues.put("word", category.getWord());
            contentValues.put("category", category.getCategory());
            database.insert("categories", null, contentValues);
        } catch (SQLiteConstraintException e) {
             e.getMessage();
        }
    }

    public void setPronunciation(Pronunciation pronunciation) {
        ContentValues contentValues;
        try {
            contentValues = new ContentValues();
            contentValues.put("word", pronunciation.getWord());
            contentValues.put("phonic", pronunciation.getPhonics());
            database.insert("phonics", null, contentValues);
        } catch (SQLiteConstraintException e) {
             e.getMessage();
        }
    }

    public void setExample(Example example) {
        ContentValues contentValues;
        try {
            contentValues = new ContentValues();
            contentValues.put("word", example.getWord());
            contentValues.put("example", example.getExample());
            database.insert("examples", null, contentValues);
        } catch (SQLiteConstraintException e) {
            e.getMessage();
        }
    }

    public void setNotification(int i) {
        ContentValues contentValues;
        try {
            contentValues = new ContentValues();
            contentValues.put("data", new Date().getTime());
            contentValues.put("remember_me", i);
            database.insert("notification", null, contentValues);
        } catch (SQLiteConstraintException e) {
            e.getMessage();
        }
    }

    public void setMigrationHistory(MigrationHistory migrationHistory) {
        ContentValues contentValues;
        try {
            contentValues = new ContentValues();
            contentValues.put("date", migrationHistory.getDate());
            contentValues.put("pat", migrationHistory.getAt());
            contentValues.put("pto", migrationHistory.getTo());
            contentValues.put("type", migrationHistory.getType());
            contentValues.put("url", migrationHistory.getUrl());
            database.insert("migrationHistory", null, contentValues);
        } catch (SQLiteConstraintException e) {
            e.getMessage();
        }
    }


    public void updateMigrationHistory(MigrationHistory migrationHistory) {
        String where = "url=?";
        String[] whereArgs = new String[] {String.valueOf(migrationHistory.getUrl())};
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("date", migrationHistory.getDate());
            contentValues.put("pat", migrationHistory.getAt());
            database.update("migrationHistory", contentValues,where,whereArgs);
        } catch (SQLiteConstraintException e) {
            e.getMessage();
        }
    }



    // set definition
    public void setDefinition(Definition definition) {
        ContentValues contentValues;
        try {

            contentValues = new ContentValues();
            contentValues.put("word", definition.getWord());
            contentValues.put("definition", definition.getDefinition());
            database.insert("definitions", null, contentValues);
        } catch (SQLiteConstraintException e) {
            // e.getMessage();
        }
    }


    public ArrayList<Word> getWords(String word) {
        ArrayList<Word> category1 = null;
        String quarry = "SELECT * FROM words WHERE word LIKE '%"+word+"%' LIMIT 10";
        try {
            Cursor cursor = database.rawQuery(quarry, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                category1.add(new Word(cursor.getString(1)));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return category1;
    }




    public ArrayList<MigrationHistory> getAllMigrationHistory() {
        ArrayList<MigrationHistory> migrationHistories = new ArrayList<>();
        String quarry = "SELECT * FROM migrationHistory WHERE  1";
        try {
            Cursor cursor = database.rawQuery(quarry, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                migrationHistories.add(new MigrationHistory(cursor.getString(1),cursor.getInt(2),cursor.getInt(3),cursor.getLong(4),cursor.getString(5)));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return migrationHistories;
    }

    // get pronunciation
    public Notification getNotification() {
        Notification notification = null;
        String quarry = "SELECT * FROM notification  WHERE 1  ORDER BY id DESC limit 1";
        try {
            Cursor cursor = database.rawQuery(quarry, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                notification = new Notification(cursor.getInt(1),cursor.getInt(2));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return notification;
    }

    // get pronunciation
    public Pronunciation getPronunciation(String word,String table) {
        Pronunciation pronunciation = null;
        String quarry = "SELECT * FROM "+table+" WHERE word = "+word;
        try {
            Cursor cursor = database.rawQuery(quarry, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                pronunciation = new Pronunciation(cursor.getString(1),cursor.getString(2));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return pronunciation;
    }

    // get pronunciation
    public Example getExample(String word) {
        Example example = null;
        String quarry = "SELECT * FROM examples WHERE word = "+word;
        try {
            Cursor cursor = database.rawQuery(quarry, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                example = new Example(cursor.getString(1),cursor.getString(2));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return example;
    }


    public int isOfflineReady(){
        String quarry = "SELECT name  FROM migrations";
        Cursor cursor = database.rawQuery(quarry, null);
       return cursor.getCount();
    }

    public void bookmark(Trending database1) {
        ContentValues contentValues;
        try {

            contentValues = new ContentValues();
            contentValues.put("word", database1.getWord());
            contentValues.put("definition", database1.getDefinition());

            database.insert("bookmarks", null, contentValues);
        } catch (SQLiteConstraintException e) {
            // e.getMessage();
        }

    }

    public void transcribe(Transcribe transcribe) {
        try {
            ContentValues contentValues;
            contentValues = new ContentValues();
            contentValues.put("language", transcribe.getFromLang());
            contentValues.put("key", transcribe.getFromKey());
            contentValues.put("text", transcribe.getMessage());
            contentValues.put("tolanguage", transcribe.getToLang());
            contentValues.put("tokey", transcribe.getToKey());
            database.insert("transcribe", null, contentValues);
        } catch (SQLiteConstraintException e) {
            // e.getMessage();
        }

    }


    public ArrayList<Transcribe> getTranscribe() {
        ArrayList<Transcribe> trending = new ArrayList<>();
        String quarry = "SELECT * FROM transcribe WHERE 1 ORDER BY id DESC";

        try {
            Cursor cursor = database.rawQuery(quarry, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                trending.add(new Transcribe(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5)));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return trending;
    }


    public ArrayList<Trending> getHistory() {
        ArrayList<Trending> trending = new ArrayList<>();
        String quarry = "SELECT * FROM history WHERE 1 ORDER BY id DESC";

        try {
            Cursor cursor = database.rawQuery(quarry, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                trending.add(new Trending(cursor.getString(1),cursor.getString(2)));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return trending;
    }

    public void DeleteTranscribe() {
        String quarry = "DELETE FROM transcribe WHERE 1";

        try {
            database.execSQL(quarry);

        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }


    public void DeleteBookMark() {
        String quarry = "DELETE FROM bookmarks WHERE 1";

        try {
            database.execSQL(quarry);

        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }


    public void DeleteHistory() {
        String quarry = "DELETE FROM history WHERE 1";

        try {
            database.execSQL(quarry);

        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    public ArrayList<Trending> getBookmark() {
        ArrayList<Trending> trending = new ArrayList<>();
        String quarry = "SELECT * FROM bookmarks WHERE 1 ORDER BY bookmarks DESC";

        try {
            Cursor cursor = database.rawQuery(quarry, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                trending.add(new Trending(cursor.getString(1),cursor.getString(2)));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return trending;
    }


}