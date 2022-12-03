package com.ritssupreme.phlurtyz002.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ritssupreme.phlurtyz002.model.GridItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by kibrom on 6/6/17.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 4;

    // Database Name
    private static final String DATABASE_NAME = "emoji";

    // Table Names
    private static final String TABLE_FAVORITE = "favorite";

    private static final String TABLE_RECENT = "recent";

    private static final String TABLE_RECENT_API = "recent_api";

    //Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_ASSET_PATH = "asset_path";
    private static final String KEY_ASSET_NAME = "asset_name";


    private static final int RECENT_EMOJI_LIMIT = 12;
    private static final int RECENT_API_EMOJI_LIMIT = 12;


      //////////////////////////////////////
     //     Table Create Statements      //
    //////////////////////////////////////

    //Favorite table create statement
    private static final String CREATE_TABLE_FAVORITE = "CREATE TABLE "
            + TABLE_FAVORITE + "(" + KEY_ID + " INTEGER PRIMARY KEY, " + KEY_ASSET_NAME
            + " TEXT, " + KEY_ASSET_PATH + " TEXT" + ")";

    //Recent table create statement
    private static final String CREATE_TABLE_RECENT = "CREATE TABLE "
            + TABLE_RECENT + "(" + KEY_ID + " INTEGER PRIMARY KEY, " + KEY_ASSET_NAME
            + " TEXT, " + KEY_ASSET_PATH + " TEXT" + ")";

    //Recent API table create statement
    private static final String CREATE_TABLE_RECENT_API = "CREATE TABLE "
            + TABLE_RECENT_API + "(" + KEY_ID + " INTEGER PRIMARY KEY, " + KEY_ASSET_NAME
            + " TEXT, " + KEY_ASSET_PATH + " TEXT" + ")";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_FAVORITE);

        db.execSQL(CREATE_TABLE_RECENT);

        db.execSQL(CREATE_TABLE_RECENT_API);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITE);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECENT);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECENT_API);

        // create new tables
        onCreate(db);

    }

      /////////////////////////////
     //     Favorite CRUD       //
    /////////////////////////////

    public long addFavorite(GridItem gridItem){

        if(!isFavoriteItemExists(gridItem.getAssetPath())){

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put(KEY_ASSET_NAME,gridItem.getAssetName());

            values.put(KEY_ASSET_PATH,gridItem.getAssetPath());

            return db.insert(TABLE_FAVORITE,null,values);

        }

        return 0;

    }

    public void addFavoriteInBulk(List<GridItem> items){

        for(GridItem item : items){

            addFavorite(item);
        }
    }

    public Boolean isFavoriteItemExists(String itemPath){

        String query = "SELECT * FROM " + TABLE_FAVORITE + " WHERE "
                + KEY_ASSET_PATH + " = '" + itemPath +"'";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query,null);

        return cursor.moveToNext();

    }

    public List<GridItem> getFavoriteList(){

        List<GridItem> gridItems = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_FAVORITE;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst()){

            do{
                GridItem gridItem = new GridItem();

                gridItem.setAssetName(cursor.getString(cursor.getColumnIndex(KEY_ASSET_NAME)));

                gridItem.setAssetPath(cursor.getString(cursor.getColumnIndex(KEY_ASSET_PATH)));

                gridItems.add(gridItem);

            }while (cursor.moveToNext());
        }

        return gridItems;

    }


      /////////////////////////////
     //       Recent CRUD       //
    // ///////////////////////////

    public Boolean IsRecentItemExists(String itemPath){


        String query = "SELECT * FROM " + TABLE_RECENT + " WHERE "
                + KEY_ASSET_PATH + " = '" + itemPath +"'";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query,null);

        return cursor.moveToNext();

    }

    public Boolean IsRecentApiItemExists(String itemPath){


        String query = "SELECT * FROM " + TABLE_RECENT_API + " WHERE "
                + KEY_ASSET_PATH + " = '" + itemPath +"'";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query,null);

        return cursor.moveToNext();

    }


    public long addRecent(GridItem gridItem){

        if(!IsRecentItemExists(gridItem.getAssetPath())){

            Stack<GridItem> items = getRecentList();

            if(items.size() > RECENT_EMOJI_LIMIT){

            }

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put(KEY_ASSET_NAME,gridItem.getAssetName());

            values.put(KEY_ASSET_PATH,gridItem.getAssetPath());

            return db.insert(TABLE_RECENT,null,values);

        }

        return 0;

    }

    public long addRecentApi(GridItem gridItem){

        if(!IsRecentApiItemExists(gridItem.getAssetPath())){

            Stack<GridItem> items = getRecentApiList();

            if(items.size() > RECENT_API_EMOJI_LIMIT){

            }

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put(KEY_ASSET_NAME,gridItem.getAssetName());

            values.put(KEY_ASSET_PATH,gridItem.getAssetPath());

            return db.insert(TABLE_RECENT_API,null,values);

        }

        return 0;

    }

    public Stack<GridItem> getRecentList(){

        Stack<GridItem> gridItems = new Stack<>();

        String query = "SELECT * FROM " + TABLE_RECENT;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst()){

            do{
                GridItem gridItem = new GridItem();

                gridItem.setAssetName(cursor.getString(cursor.getColumnIndex(KEY_ASSET_NAME)));

                gridItem.setAssetPath(cursor.getString(cursor.getColumnIndex(KEY_ASSET_PATH)));

                gridItems.push(gridItem);

            }while (cursor.moveToNext());
        }

        return gridItems;

    }

    public Stack<GridItem> getRecentApiList(){

        Stack<GridItem> gridItems = new Stack<>();

        String query = "SELECT * FROM " + TABLE_RECENT_API;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst()){

            do{
                GridItem gridItem = new GridItem();

                gridItem.setAssetName(cursor.getString(cursor.getColumnIndex(KEY_ASSET_NAME)));

                gridItem.setAssetPath(cursor.getString(cursor.getColumnIndex(KEY_ASSET_PATH)));

                gridItems.push(gridItem);

            }while (cursor.moveToNext());
        }

        return gridItems;

    }

}
