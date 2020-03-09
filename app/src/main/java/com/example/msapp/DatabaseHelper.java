package com.example.msapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Movies.db";
    private static final String TABLE_NAME = "movies_table";
    private static final String COL_1 = "TITLE";
    private static final String COL_2 = "IMAGE";
    private static final String COL_3 = "RATING";
    private static final String COL_4 = "RELEASE_YEAR";
    private static final String COL_5 = "GENRE";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + TABLE_NAME + " (" + COL_1 + " TEXT PRIMARY KEY," +
                        COL_2 + " TEXT," + COL_3 + " DOUBLE," + COL_4 + " INTEGER," +
                        COL_5 + " TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String title, String image, double rating, int releaseYear, String genre){
        SQLiteDatabase db = this.getWritableDatabase();
        boolean isExists = Exists(db, title);

        long result = -1;
        if(!isExists) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_1, title);
            contentValues.put(COL_2, image);
            contentValues.put(COL_3, rating);
            contentValues.put(COL_4, releaseYear);
            contentValues.put(COL_5, genre);

            result = db.insertWithOnConflict(TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
        }
        else return isExists;
        if(result == -1) return false;
        else return true;
    }

    public boolean Exists(SQLiteDatabase db, String title) {
        String query = "SELECT COUNT(*) FROM "+ TABLE_NAME + " WHERE title =? ;";
        long count = DatabaseUtils.longForQuery(db,query, new String[] {title});
        return count > 0;
    }

    public Cursor getAllDataNewToOld(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL_4 +" DESC;";
        Cursor result = db.rawQuery(query, null);
        return result;
    }

    public Cursor getMovieDetails(String title){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM movies_table WHERE TITLE = ?", new String[] {title});
        res.moveToNext();
        return res;
    }

}
