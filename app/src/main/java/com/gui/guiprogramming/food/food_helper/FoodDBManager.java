package com.gui.guiprogramming.food.food_helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gui.guiprogramming.food.food_model.Food;
import com.gui.guiprogramming.food.food_model.Nutrients;

import java.util.ArrayList;


/**
 * FoodDBManager class will help to store/update data to local SQLite database
 * FoodDBManager is a helper class which provides functions/methods to
 * perform certain operation as per requirement
 */
public class FoodDBManager {

    //current object of a class
    private DBOpenHelper dbOpenHelper;
    //context required to work with SQLite
    private Context context;

    private SQLiteDatabase database;

    /**
     * @param context - context of a current activity,
     *                used to establish SQLite database connection
     */
    public FoodDBManager(Context context) {
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
        static final String DATABASE_NAME = "GUI_FOOD_DB";
        //table name, which will be created later
        static final String TABLE_FOOD = "FOOD";

        //Declaration of column names of FOOD table
        static final String FOOD_ID = "foodId";
        static final String FOOD_LABEL = "label";
        static final String FOOD_ENERGY_KCAL = "ENERC_KCAL";
        static final String FOOD_PROTEIN = "PROCNT";
        static final String FOOD_FAT = "FAT";
        static final String FOOD_CARBS = "CHOCDF";
        static final String FOOD_CATEGORY = "category";
        static final String FOOD_CATEGORY_LABEL = "categoryLabel";
        static final String FOOD_TAG = "foodTAG";
        //Declaration of column names of FOOD table ends here

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
            // TODO Auto-generated method stub
            db.execSQL(
                    "CREATE TABLE " + TABLE_FOOD + " (" + FOOD_ID + " text primary key, " +
                            FOOD_LABEL + " text," +
                            FOOD_ENERGY_KCAL + " real," +
                            FOOD_PROTEIN + " real," +
                            FOOD_FAT + " real," +
                            FOOD_CARBS + " real," +
                            FOOD_CATEGORY + " real," +
                            FOOD_CATEGORY_LABEL + " real," +
                            FOOD_TAG + " text"
                            + ")"
            );
        }

        /**
         * onUpgrade() will be called if we increment
         * the current database version number
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOOD);
            onCreate(db);
        }
    }

    /**
     * <p>This method inserts the record into the database</p>
     *
     * @param foodID        unique ID of a food
     * @param foodLabel     label of a food
     * @param energyKcal    calories of a food - unit: (kcal), provided as ENERC_KCAL in the API
     * @param protein       protein of a food - unit: (g), provided as PROCNT in the API
     * @param fat           FAT of a food - unit: (g), provided as FAT in the API
     * @param category      category of a food, provided as category in the API
     * @param categoryLabel category-label of a food, provided as categoryLabel in the API
     * @param tag           entered by user
     * @return row ID if insertion success, otherwise -1
     * @see FoodDBManager#open()
     * @see SQLiteDatabase#insert(String, String, ContentValues)
     */
    public long addToFav(String foodID, String foodLabel,
                         double energyKcal, double protein, double fat, double carbs,
                         String category, String categoryLabel, String tag) {
        //Map all the parameters with it's appropriate column name
        ContentValues cv = new ContentValues();
        cv.put(DBOpenHelper.FOOD_ID, foodID);
        cv.put(DBOpenHelper.FOOD_LABEL, foodLabel);
        cv.put(DBOpenHelper.FOOD_ENERGY_KCAL, energyKcal);
        cv.put(DBOpenHelper.FOOD_PROTEIN, protein);
        cv.put(DBOpenHelper.FOOD_FAT, fat);
        cv.put(DBOpenHelper.FOOD_CARBS, carbs);
        cv.put(DBOpenHelper.FOOD_CATEGORY, category);
        cv.put(DBOpenHelper.FOOD_CATEGORY_LABEL, categoryLabel);
        cv.put(DBOpenHelper.FOOD_TAG, tag);
        return database.insert(DBOpenHelper.TABLE_FOOD, null, cv);
    }

    /**
     * <p>getFavoritesList() retrieves information about ingredient from SQLiteDatabase.
     * SQLiteDatabase has to be opened as readable/writable database to perform.</p>
     *
     * @return ArrayList, retrieves all the Food's description, added to favorites.
     * @see FoodDBManager#open()
     */
    public ArrayList<Food> getFavoritesList() {
        //column names, to retrieve information
        String[] columns = new String[]{DBOpenHelper.FOOD_ID, DBOpenHelper.FOOD_LABEL,
                DBOpenHelper.FOOD_CATEGORY, DBOpenHelper.FOOD_CATEGORY_LABEL,
                DBOpenHelper.FOOD_ENERGY_KCAL, DBOpenHelper.FOOD_PROTEIN, DBOpenHelper.FOOD_FAT,
                DBOpenHelper.FOOD_CARBS, DBOpenHelper.FOOD_TAG};

        Cursor cursor = database.query(DBOpenHelper.TABLE_FOOD, columns, null,
                null, null, null, null);

        //To store multiple Food class objects
        ArrayList<Food> mRowData = new ArrayList<>();
        //check if cursor has the data and it can read it.
        if (cursor.moveToFirst()) {
            do {
                //Reading retrieved row values
                String foodID = cursor.getString(cursor.getColumnIndex(DBOpenHelper.FOOD_ID));
                String foodLabel = cursor.getString(cursor.getColumnIndex(DBOpenHelper.FOOD_LABEL));
                String foodCategory = cursor.getString(cursor.getColumnIndex(DBOpenHelper.FOOD_CATEGORY));
                String foodCategoryLabel = cursor.getString(cursor.getColumnIndex(DBOpenHelper.FOOD_CATEGORY_LABEL));
                double foodEnergy = cursor.getDouble(cursor.getColumnIndex(DBOpenHelper.FOOD_ENERGY_KCAL));
                double foodPro = cursor.getDouble(cursor.getColumnIndex(DBOpenHelper.FOOD_PROTEIN));
                double foodFat = cursor.getDouble(cursor.getColumnIndex(DBOpenHelper.FOOD_FAT));
                double foodCarbs = cursor.getDouble(cursor.getColumnIndex(DBOpenHelper.FOOD_CARBS));
                String foodTag = cursor.getString(cursor.getColumnIndex(DBOpenHelper.FOOD_TAG));
                //Storing each row details into Movie class's object
                Food food = new Food();
                food.setFoodId(foodID);
                food.setLabel(foodLabel);
                food.setCategory(foodCategory);
                food.setCategoryLabel(foodCategoryLabel);
                food.setTag(foodTag);
                Nutrients nutrients = new Nutrients(foodEnergy, foodPro, foodFat, foodCarbs);
                food.setNutrients(nutrients);
                //Add the Food class object to an ArrayList
                mRowData.add(food);
            } while (cursor.moveToNext()); //move current cursor to next row
        }
        cursor.close();
        return mRowData;
    }

    /**
     * <p>isExists() checks whether a particular Food/ingredient exists or not.
     * SQLiteDatabase has to be opened as readable/writable database to perform.
     * It has to be called to display a particular button to user
     * i.e.  if food exists in database, show "Remove from favorites",
     * otherwise show "Add to favorites"
     * </p>
     *
     * @param foodID a unique ID of a Food/ingredient
     * @return true if food/ingredient exists, otherwise false
     * @see FoodDBManager#open()
     */
    public boolean isExists(String foodID) {
        String[] columns = new String[]{DBOpenHelper.FOOD_ID};
        Cursor cursor = database.query(DBOpenHelper.TABLE_FOOD, columns, DBOpenHelper.FOOD_ID + " = ?",
                new String[]{foodID}, null, null, null);
        boolean isExists = cursor.moveToFirst();
        cursor.close();
        return isExists;
    }

    /**
     * <p>removeFavorite() deletes a row from the database.
     * SQLiteDatabase has to be opened as writable database to perform
     * this operation </p>
     *
     * @param foodID a unique ID of a Food, to be deleted
     * @return true if Food deleted, otherwise false
     */
    public boolean removeFavorite(String foodID) {
        return database.delete(DBOpenHelper.TABLE_FOOD, DBOpenHelper.FOOD_ID + "=?", new String[]{foodID}) > 0;
    }

    /**
     * @return no. of records stored in database, returns 0 if no data is stored in DB
     */
    public int getCount() {
        int count = 0;
        Cursor cursor = database.rawQuery("SELECT count(*) FROM " + DBOpenHelper.TABLE_FOOD, null);
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    /**
     * @return SUM of calories
     */
    public double getTotalCalories() {
        double total = 0;
        Cursor cursor = database.rawQuery("SELECT sum(" + DBOpenHelper.FOOD_ENERGY_KCAL + ") FROM " + DBOpenHelper.TABLE_FOOD, null);
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        return total;
    }

    /**
     * @return Minimum calorie value from Food table
     */
    public double getMinCalories() {
        double min = 0;
        Cursor cursor = database.rawQuery("SELECT min(" + DBOpenHelper.FOOD_ENERGY_KCAL + ") FROM " + DBOpenHelper.TABLE_FOOD, null);
        if (cursor.moveToFirst()) {
            min = cursor.getDouble(0);
        }
        cursor.close();
        return min;
    }

    /**
     * @return Average calorie value from Food table
     */
    public double getAvgCalories() {
        double avg = 0;
        Cursor cursor = database.rawQuery("SELECT avg(" + DBOpenHelper.FOOD_ENERGY_KCAL + ") FROM " + DBOpenHelper.TABLE_FOOD, null);
        if (cursor.moveToFirst()) {
            avg = cursor.getDouble(0);
        }
        cursor.close();
        return avg;
    }

    /**
     * @return Maximum calorie value from Food table
     */
    public double getMaxCalories() {
        double max = 0;
        Cursor cursor = database.rawQuery("SELECT max(" + DBOpenHelper.FOOD_ENERGY_KCAL + ") FROM " + DBOpenHelper.TABLE_FOOD, null);
        if (cursor.moveToFirst()) {
            max = cursor.getDouble(0);
        }
        cursor.close();
        return max;
    }
}
