package gd.rf.tekporconsult.mypronouncer.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import gd.rf.tekporconsult.mypronouncer.model.Transcribe;
import gd.rf.tekporconsult.mypronouncer.model.Trending;


public class DatabaseAccess {
    private static DatabaseAccess instance;
    private final SQLiteOpenHelper openHelper;
    ContentValues contentValues;
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
//get user
    public void setHistory(Trending database1) {
        try {
            contentValues = new ContentValues();
            contentValues.put("word", database1.getWord());
            contentValues.put("definition", database1.getDefinition());

            database.insert("history", null, contentValues);
        } catch (SQLiteConstraintException e) {
            // e.getMessage();
        }

    }


    public void bookmark(Trending database1) {
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