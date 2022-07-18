package gd.rf.tekporconsult.mypronouncer.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import gd.rf.tekporconsult.mypronouncer.model.Category;
import gd.rf.tekporconsult.mypronouncer.model.Definition;
import gd.rf.tekporconsult.mypronouncer.model.Pronunciation;
import gd.rf.tekporconsult.mypronouncer.model.Transcribe;
import gd.rf.tekporconsult.mypronouncer.model.Trending;
import gd.rf.tekporconsult.mypronouncer.model.Word;


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
//get history
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

    // set word
    public void setWord(Word word,String table) {
        try {
            contentValues = new ContentValues();
            contentValues.put("word", word.getWord());
            database.insert(table, null, contentValues);
        } catch (SQLiteConstraintException e) {
            // e.getMessage();
        }
    }

    // set category
    public void setCategory(Category category,String table) {
        try {
            contentValues = new ContentValues();
            contentValues.put("word", category.getWord());
            contentValues.put("category", category.getCategory());
            database.insert(table, null, contentValues);
        } catch (SQLiteConstraintException e) {
            // e.getMessage();
        }
    }

    public void setPronunciation(Pronunciation pronunciation, String table) {
        try {
            contentValues = new ContentValues();
            contentValues.put("word", pronunciation.getWord());
            contentValues.put("phonics", pronunciation.getPhonics());
            database.insert(table, null, contentValues);
        } catch (SQLiteConstraintException e) {
             e.getMessage();
        }
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

    // set definition
    public void setDefinition(Definition definition,String table) {
        try {
            contentValues = new ContentValues();
            contentValues.put("word", definition.getWord());
            contentValues.put("category", definition.getCategory());
            contentValues.put("definition", definition.getDefinition());
            database.insert(table, null, contentValues);
        } catch (SQLiteConstraintException e) {
            // e.getMessage();
        }
    }


    //get word
    public Word getWord(String word,String table) {
        Word word1 = null;
        String quarry = "SELECT * FROM "+table+" WHERE word = "+word;
        try {
            Cursor cursor = database.rawQuery(quarry, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                word1 = new Word(cursor.getString(1));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return word1;
    }

    public ArrayList<Word> getWords(String word,String table) {
        ArrayList<Word> category1 = null;
        String quarry = "SELECT * FROM "+table+" WHERE word LIKE '%"+word+"%' LIMIT 10";
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

    public int isOfflineReady(){
        String quarry = "SELECT name  FROM migrations";
        Cursor cursor = database.rawQuery(quarry, null);
       return cursor.getCount();
    }

    //get getCategories
    public ArrayList<Category> getCategories(String word,String table) {
        ArrayList<Category> category1 = null;
        String quarry = "SELECT * FROM "+table+" WHERE word = "+word;
        try {
            Cursor cursor = database.rawQuery(quarry, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                category1.add(new Category(cursor.getString(1),cursor.getString(2)));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return category1;
    }

    //get word
    public ArrayList<Definition> getDefinitions(String word,String table) {
        ArrayList<Definition> category1 = null;
        String quarry = "SELECT * FROM "+table+" WHERE word = "+word;
        try {
            Cursor cursor = database.rawQuery(quarry, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                category1.add(new Definition(cursor.getString(1),cursor.getString(2),cursor.getString(3)));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return category1;
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