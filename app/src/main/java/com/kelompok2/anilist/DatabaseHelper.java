package com.kelompok2.anilist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "watchlist.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_WATCHLIST = "watchlist";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ANIME_ID = "anime_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_IMAGE_URL = "image_url";
    public static final String COLUMN_STATUS = "status";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_WATCHLIST + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_ANIME_ID + " INTEGER, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_IMAGE_URL + " TEXT, " +
                    COLUMN_STATUS + " TEXT DEFAULT 'Plan to Watch');";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WATCHLIST);
        onCreate(db);
    }

    public long addToWatchlist(int animeId, String title, String imageUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ANIME_ID, animeId);
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_IMAGE_URL, imageUrl);
        values.put(COLUMN_STATUS, "Plan to Watch");
        long id = db.insert(TABLE_WATCHLIST, null, values);
        db.close();
        return id;
    }

    public boolean isAnimeInWatchlist(int malId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_WATCHLIST, new String[]{COLUMN_ID}, 
                COLUMN_ANIME_ID + " = ?", new String[]{String.valueOf(malId)}, 
                null, null, null);
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        db.close();
        return exists;
    }

    public List<Anime> getWatchlist() {
        List<Anime> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_WATCHLIST, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Anime anime = new Anime();
                anime.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                anime.setMalId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ANIME_ID)));
                anime.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
                anime.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL)));
                anime.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));
                list.add(anime);
            } while (cursor.moveToNext());
        }
        if (cursor != null) cursor.close();
        db.close();
        return list;
    }

    public void updateStatus(int id, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, status);
        db.update(TABLE_WATCHLIST, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteAnime(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WATCHLIST, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
}
