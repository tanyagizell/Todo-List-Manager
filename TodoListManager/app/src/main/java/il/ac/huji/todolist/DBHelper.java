package il.ac.huji.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Tanyagizell on 28/04/2015.
 */
public class DBHelper  extends SQLiteOpenHelper{


    private static final int VERSION = 1;
    //table strings
    public static final String TABLE_NAME = "todo_table";
    public static final String KEY_ID_STR = "_id";
    public static final String TODO_TITLE_STR = "title";
    public static final String DUE_DATE_STR = "due";

    /*indexes of columns*/
    private static final int KEY_ID_IDX = 0;
    private static final int TODO_TITLE_IDX = 1;
    private static final int DUE_DATE_IDX = 2;

    private static final String CREATE_TABLE_TODO = "CREATE TABLE "
            + TABLE_NAME + "(" + KEY_ID_STR + " INTEGER PRIMARY KEY,"
            + TODO_TITLE_STR + " TEXT,"
            + DUE_DATE_STR + " INTEGER" + ")";

    private static final String DROP_TABLE_TODO = "DROP TABLE IF EXISTS" + TABLE_NAME;

    public DBHelper(Context context){
        super(context, "todo_db",null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){

        db.execSQL(CREATE_TABLE_TODO);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //on upgrade, drop the table and then recreate it
        db.execSQL(DROP_TABLE_TODO);
        onCreate(db);
    }

    /*
    * Insert the given item into the database
    */
    public void insertItem(HashMap<String,String> entryValues){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        //populate the content
        values.put(TODO_TITLE_STR,entryValues.get(TODO_TITLE_STR));
        values.put(DUE_DATE_STR,entryValues.get(DUE_DATE_STR));

        db.insert(TABLE_NAME,null,values);
        db.close();

    }

    /*
     * Updates the item indicated by the id located in entryValues
     */
    public void updateItem(HashMap<String,String> entryValues){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        //populate the content
        values.put(TODO_TITLE_STR,entryValues.get(TODO_TITLE_STR));
        values.put(DUE_DATE_STR,entryValues.get(DUE_DATE_STR));

        String where = KEY_ID_STR + " = ? ";

        db.update(TABLE_NAME, values, where, new String[]{entryValues.get(KEY_ID_STR)});
        db.close();

    }

    /*
    * Delete the entry with the given ID from the database
    */
    public void deleteItem(String id){

        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + TABLE_NAME + " WHERE " + KEY_ID_STR  + "='" + id + "'";

        db.execSQL(deleteQuery);
        db.close();
    }

    /*
    * Get all the entries in the database
    */
    public ArrayList<HashMap<String,String>> getAllEntries(){

        //init list
        ArrayList<HashMap<String,String>> allEntries = new ArrayList<HashMap<String, String>>();

        //select all rows via query
        String selectAllQuery = "SELECT * FROM " + TABLE_NAME;

        //open database for read
        SQLiteDatabase db = this.getReadableDatabase();

        //get a cursor
        Cursor cursor = db.rawQuery(selectAllQuery,null);
        //if we have entries
        if (cursor.moveToFirst()){

            do{

                HashMap<String,String> entryMap = new HashMap<String,String>();
                entryMap.put(KEY_ID_STR,cursor.getString(KEY_ID_IDX));
                entryMap.put(TODO_TITLE_STR,cursor.getString(TODO_TITLE_IDX));
                entryMap.put(DUE_DATE_STR,cursor.getString(DUE_DATE_IDX));

                allEntries.add(entryMap);

            //while there are more entries
            }while (cursor.moveToNext());
        }

        //return the filled list
        return allEntries;

    }

    /*
    * Get the entry with the requested id from the Database
    */
    public HashMap<String,String> getEntryById(String id){

        //init a map to hold the return entry
        HashMap<String,String> entryMap = new HashMap<String,String>();

        //select all rows via query
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE "+ KEY_ID_STR + "='" + id+ "'";

        //open database for read
        SQLiteDatabase db = this.getReadableDatabase();

        //get a cursor
        Cursor cursor = db.rawQuery(selectQuery,null);
        //if we have entries
        if (cursor.moveToFirst()){

            do{

                entryMap.put(KEY_ID_STR,cursor.getString(KEY_ID_IDX));
                entryMap.put(TODO_TITLE_STR,cursor.getString(TODO_TITLE_IDX));
                entryMap.put(DUE_DATE_STR,cursor.getString(DUE_DATE_IDX));

                //while there are more entries
            }while (cursor.moveToNext());
        }

        //return the filled list
        return entryMap;
    }

}
