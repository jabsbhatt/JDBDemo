package com.jdbdemo.sqlite;

/**
 * Created by ci-and-06 on 26/7/17.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jdbdemo.pojo.MoviesContainer;
import com.jdbdemo.pojo.User;

import java.util.ArrayList;
import java.util.List;


public class MoviesDBHelper extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "MovieManager.db";

    // Contacts table name
    private static final String TABLE_MOVIE = "Movie";
    private static final String TABLE_GENRE = "Genre";

    // Contacts Table Columns names
    public static final String KEY_MOVIE_ID = "id";
    public static final String KEY_TITLE = "mtitle";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_RATTING = "rating";
    public static final String KEY_RELEASEYEAR = "releaseYear";

    String CREATE_MOVIE_TABLE = "CREATE TABLE " + TABLE_MOVIE + "("
            + KEY_MOVIE_ID + " INTEGER PRIMARY KEY," + KEY_TITLE + " TEXT,"
            + KEY_IMAGE + " TEXT," + KEY_RATTING + " TEXT," + KEY_RELEASEYEAR + " TEXT " + ")";

    public static final String KEY_GENRE_ID = "id";
    public static final String KEY_GENRE_TITLE = "title";
    public static final String KEY_GENRE_MOVIE_ID = "movie_id";

    String CREATE_GENRE_TABLE = "CREATE TABLE " + TABLE_GENRE + "("
            + KEY_GENRE_ID + " INTEGER PRIMARY KEY," + KEY_GENRE_TITLE + " TEXT,"
            + KEY_GENRE_MOVIE_ID + " TEXT)";
    private String TAG = getClass().getName();


    public MoviesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MOVIE_TABLE);
        db.execSQL(CREATE_GENRE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVIE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GENRE);

        // Create tables again
        onCreate(db);
    }


    // Adding new contact
    public void addMovie(MoviesContainer moviesContainer) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, moviesContainer.getTitle());
        values.put(KEY_IMAGE, moviesContainer.getImage());
        values.put(KEY_RATTING, moviesContainer.getRating());
        values.put(KEY_RELEASEYEAR, moviesContainer.getReleaseYear());

        // Inserting Row
        long id = db.insert(TABLE_MOVIE, null, values);

        for (int i = 0; i < moviesContainer.getGenre().size(); i++) {
            ContentValues values1 = new ContentValues();
            values1.put(KEY_GENRE_TITLE, moviesContainer.genre.get(i));
            values1.put(KEY_GENRE_MOVIE_ID, id);

            db.insert(TABLE_GENRE, null, values1);
        }

        db.close(); // Closing database connection
    }


    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from Movie where id=" + id + "", null);
//        Cursor res = db.rawQuery("SELECT m." + KEY_TITLE + ", m." + KEY_IMAGE + ", m. " + KEY_RATTING + " , m." + KEY_RELEASEYEAR + ", g." + KEY_GENRE_TITLE + " FROM Movie m, Genre g where m.id = g." + KEY_GENRE_MOVIE_ID + " and m.id = '" + id + "'", null);
//        Cursor res = db.rawQuery("SELECT m.mtitle, m.image, m.rating, m.releaseYear, g.title FROM Movie m, Genre g where m.id = g.movie_id and m.id = '" + id + "'", null);
        return res;
    }

    // Getting All Contacts
    public List<MoviesContainer> getAllMovieList() {
        List<MoviesContainer> contactList = new ArrayList<MoviesContainer>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_MOVIE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                MoviesContainer moviesContainer = new MoviesContainer();
                moviesContainer.setId((cursor.getString(0)));
                moviesContainer.setTitle(cursor.getString(1));
                moviesContainer.setImage(cursor.getString(2));
                moviesContainer.setRating(Double.valueOf(cursor.getString(3)));
                moviesContainer.setReleaseYear(Integer.valueOf(cursor.getString(4)));
                // Adding contact to list
                contactList.add(moviesContainer);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

    // Updating single contact

//    public int updateContact(MoviesContainer moviesContainer) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(KEY_TITLE, moviesContainer.getTitle());
//        values.put(KEY_IMAGE, moviesContainer.getImage());
//        values.put(KEY_RELEASEYEAR, moviesContainer.getReleaseYear());
//
//        // updating row
//        return db.update(TABLE_MOVIE, values, KEY_MOVIE_ID + " = ?",
//                new String[]{String.valueOf(moviesContainer.getId())});
//    }

    public boolean updateContact(Integer id, String image, String title, String releaseyr) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_TITLE, title);
        contentValues.put(KEY_IMAGE, image);
        contentValues.put(KEY_RELEASEYEAR, releaseyr);
        db.update(TABLE_MOVIE, contentValues, KEY_MOVIE_ID + " = ?", new String[]{Integer.toString(id)});
        return true;
    }

    // Deleting single contact
    public void deleteMovie(MoviesContainer moviesContainer) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MOVIE, KEY_MOVIE_ID + " = ?",
                new String[]{String.valueOf(moviesContainer.getId())});
        db.delete(TABLE_GENRE, KEY_GENRE_ID + " = ?",
                new String[]{String.valueOf(moviesContainer.getId())});
        db.close();
    }


    // Getting contacts Count
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_MOVIE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

}
