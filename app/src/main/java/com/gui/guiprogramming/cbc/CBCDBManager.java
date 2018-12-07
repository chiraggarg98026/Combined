package com.gui.guiprogramming.cbc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gui.guiprogramming.cbc.cbc_model.CBCNewsStory;

import java.util.ArrayList;

/**
 * <p>CBCDBManager deals with all local database operations.</p>
 */
public class CBCDBManager {
    //current object of a class
    private DBOpenHelper dbOpenHelper;
    //context required to work with SQLite
    private Context context;

    private SQLiteDatabase database;

    /**
     * @param context - context of a current activity,
     *                used to establish SQLite database connection
     */
    public CBCDBManager(Context context) {
        this.context = context;
    }

    /**
     * This method helps you to establish connection SQLite database, and
     * to store/update current data
     */
    public void open() throws SQLException {
        dbOpenHelper = new DBOpenHelper(context);
        database = dbOpenHelper.getWritableDatabase();
    }

    /**
     * This method closes the connection to SQLite database
     */
    public void close() {
        dbOpenHelper.close();
    }

    class DBOpenHelper extends SQLiteOpenHelper {

        //Database name
        static final String DATABASE_NAME = "GUI_NEWS_DB";
        //table name, which will be created later
        static final String TABLE_NEWS = "NEWS";

        //Declaration of column names of NEWS table
        static final String NEWS_TITLE = "newsTitle";
        static final String NEWS_DESC = "newsDesc";
        static final String NEWS_LINK = "newsLink";
        static final String NEWS_PUB_DATE = "newsPubDate";
        static final String NEWS_CATEGORY = "newsCategory";
        static final String NEWS_AUTHOR = "newsAuthor";
        //Declaration of column names of NEWS table ends here

        /**
         * SQLiteDatabase open helper class constructor
         *
         * @param context required to handle SQLite database connection
         */
        DBOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, 1);
        }

        /**
         * onCreate() will be called once
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            String SQL = "CREATE TABLE " + TABLE_NEWS + " (" + NEWS_TITLE + " text primary key, " +
                    NEWS_DESC + " text," +
                    NEWS_LINK + " text," +
                    NEWS_PUB_DATE + " text," +
                    NEWS_CATEGORY + " text," +
                    NEWS_AUTHOR + " text"
                    + ")";
            db.execSQL(SQL);
        }

        /**
         * onUpgrade() will be called if we increment
         * the current database version number
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEWS);
            onCreate(db);
        }
    }

    /**
     * <p>Checks whether the article exists or not in DB.</p>
     *
     * @param title Title of a news article to check, it exists or not
     * @return true if and only if a record with a #title is stored in DB,
     * otherwise false
     */
    public boolean isExists(String title) {
        String[] columns = new String[]{DBOpenHelper.NEWS_TITLE};
        Cursor cursor = database.query(DBOpenHelper.TABLE_NEWS, columns,
                DBOpenHelper.NEWS_TITLE + " = ?",
                new String[]{title}, null, null, null);
        boolean isExists = cursor.moveToFirst();
        cursor.close();
        return isExists;
    }

    /**
     * <p>Inserts a row in the database</p>
     * @param data instance of CBCNewsStory class, which has all the info for an article
     * @return row ID of a record which just inserted successfully, otherwise -1
     * */
    public long saveArticle(CBCNewsStory data) {
        //Map all the parameters with it's appropriate column name
        ContentValues cv = new ContentValues();
        cv.put(DBOpenHelper.NEWS_TITLE, data.getTitle());
        cv.put(DBOpenHelper.NEWS_DESC, data.getDescription());
        cv.put(DBOpenHelper.NEWS_CATEGORY, data.getCategory());
        cv.put(DBOpenHelper.NEWS_LINK, data.getLink());
        cv.put(DBOpenHelper.NEWS_PUB_DATE, data.getPubDate());
        cv.put(DBOpenHelper.NEWS_AUTHOR, data.getAuthor());
        return database.insert(DBOpenHelper.TABLE_NEWS, null, cv);
    }

    /**
     * <p>To get all the news article details from the DB</p>
     * @return all the records from NEWS table in ArrayList<CBCNewsStory> form
     * */
    public ArrayList<CBCNewsStory> getAllArticles() {
        ArrayList<CBCNewsStory> data = new ArrayList<>();
        //Column names to select
        String columns[] = {DBOpenHelper.NEWS_TITLE, DBOpenHelper.NEWS_DESC,
                DBOpenHelper.NEWS_LINK, DBOpenHelper.NEWS_AUTHOR,
                DBOpenHelper.NEWS_CATEGORY, DBOpenHelper.NEWS_PUB_DATE};
        //Performing a SELECT query
        Cursor cursor = database.query(DBOpenHelper.TABLE_NEWS, columns, null,
                null, null, null, null);
        //Checking If query result has more than one records
        if (cursor.moveToFirst()) {
            do {
                //reading all the data for NEWS table
                String title = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NEWS_TITLE));
                String desc = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NEWS_DESC));
                String link = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NEWS_LINK));
                String author = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NEWS_AUTHOR));
                String pubDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NEWS_PUB_DATE));
                String category = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NEWS_CATEGORY));
                //Storing each row record into a CBCNewsStory object
                CBCNewsStory model = new CBCNewsStory();
                model.setTitle(title);
                model.setDescription(desc);
                model.setLink(link);
                model.setAuthor(author);
                model.setPubDate(pubDate);
                model.setCategory(category);
                //adding each object to a List
                data.add(model);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return data;
    }


    /**
     * <p>It deletes the record(s) where the #title matches with the table column value</p>
     * @param title Title of an article to be removed from a database
     * @return true if and onl if the record is deleted successfully
     * */
    public boolean removeArticle(String title) {
        return database.delete(DBOpenHelper.TABLE_NEWS, DBOpenHelper.NEWS_TITLE + "=?"
                , new String[]{title}) > 0;
    }

    /**
     * <p>To get the # of data stored in NEWS table</p>
     * @return # of records in a NEWS table, if no records are inserted then it returns 0
     * */
    public int getCount() {
        int count = 0;
        Cursor cursor = database.rawQuery("SELECT count(*) FROM " + DBOpenHelper.TABLE_NEWS,
                null);
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    /**
     * <p>Each word count occurs when ' '[whitespace] is found.</p>
     * <h3>WORD count process</h3>
     * <li>Getting length of each description record</li>
     * <li>Replacing whitespaces ' ' with empty char '' and count the description length
     * - whitespaces will be ignored in getting length</li>
     * <li>Performing minus operation for total words - replacements</li>
     * <li>Adding 1 to record, that will be total # of words</li>
     *
     * <p>Minimum of word count will be a result of {@link CBCDBManager#getMinWords()}</p>
     * @return minimum article words
     * */
    public int getMinWords() {
        int minWords = 0;
        Cursor cursor = database.rawQuery("SELECT min(length(" + DBOpenHelper.NEWS_DESC + ") - " +
                        "length(replace(" + DBOpenHelper.NEWS_DESC + ", ' ', '')) + 1) FROM "
                        + DBOpenHelper.TABLE_NEWS,
                null);
        if (cursor.moveToFirst()) {
            minWords = cursor.getInt(0);
        }
        cursor.close();
        return minWords;
    }

    /**
     * <p>Each word count occurs when ' '[whitespace] is found.</p>
     * <h3>WORD count process</h3>
     * <li>Getting length of each description record</li>
     * <li>Replacing whitespaces ' ' with empty char '' and count the description length
     * - whitespaces will be ignored in getting length</li>
     * <li>Performing minus operation for total words - replacements</li>
     * <li>Adding 1 to record, that will be total # of words</li>
     *
     * <p>Maximum of word count will be a result of {@link CBCDBManager#getMaxWords()}</p>
     * @return maximum article words
     * */
    public int getMaxWords() {
        int maxWords = 0;
        Cursor cursor = database.rawQuery("SELECT max(length(" + DBOpenHelper.NEWS_DESC + ") - " +
                        "length(replace(" + DBOpenHelper.NEWS_DESC + ", ' ', '')) + 1) FROM "
                        + DBOpenHelper.TABLE_NEWS,
                null);
        if (cursor.moveToFirst()) {
            maxWords = cursor.getInt(0);
        }
        cursor.close();
        return maxWords;
    }

    /**
     * <p>Each word count occurs when ' '[whitespace] is found.</p>
     * <h3>WORD count process</h3>
     * <li>Getting length of each description record</li>
     * <li>Replacing whitespaces ' ' with empty char '' and count the description length
     * - whitespaces will be ignored in getting length</li>
     * <li>Performing minus operation for total words - replacements</li>
     * <li>Adding 1 to record, that will be total # of words</li>
     *
     * <p>Average of word count will be a result of {@link CBCDBManager#getAvgWords()} </p>
     * @return Average of article words
     * */
    public double getAvgWords() {
        double maxWords = 0;
        Cursor cursor = database.rawQuery("SELECT avg(length(" + DBOpenHelper.NEWS_DESC + ") - " +
                        "length(replace(" + DBOpenHelper.NEWS_DESC + ", ' ', '')) + 1) FROM "
                        + DBOpenHelper.TABLE_NEWS,
                null);
        if (cursor.moveToFirst()) {
            maxWords = cursor.getDouble(0);
        }
        cursor.close();
        return maxWords;
    }
}
