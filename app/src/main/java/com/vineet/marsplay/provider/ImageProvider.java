package com.vineet.marsplay.provider;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.vineet.marsplay.event.ImageModel;

import java.util.ArrayList;

public class ImageProvider extends ContentProvider {

    public static final int readMode = 1;
    public static final int writeMode = 2;
    public final static String dbName = "marsplay.db";
    public static final String tableImageList = ImageConstant.ImageTABLENAME;
    public static final String PROVIDER_NAME = "com.marsplay.provider.image";
    static final int version = 1;
    static final String col_id = ImageConstant.ID;
    static final String col_imageurl = ImageConstant.IMAGE_URL;
    static final UriMatcher uriMatcher;
    static final int IMAGE_ALL = 1;
    static final int IMAGE_SPECFIC = 2;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, tableImageList, IMAGE_ALL);
        uriMatcher.addURI(PROVIDER_NAME, tableImageList + "/#",
                IMAGE_SPECFIC);
    }

    SQLiteDatabase db;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        String table_Name = uri.getPathSegments().get(0);
        switch (uriMatcher.match(uri)) {
            case IMAGE_ALL:
                count = db.delete(table_Name, selection, selectionArgs);
                break;
            case IMAGE_SPECFIC:
                String id = uri.getPathSegments().get(1);
                count = db.delete(tableImageList, col_id
                        + " = "
                        + id
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection
                        + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {

            case IMAGE_ALL:
                return "vnd.android.cursor.dir/vnd.example.marsplay";

            case IMAGE_SPECFIC:
                return "vnd.android.cursor.item/vnd.example.marsplay";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        String table_Name = uri.getPathSegments().get(0);
        long rowID = db.insert(table_Name, "", values);

        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(uri, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        } else {
            Log.e("error in inserting", uri.toString());
            throw new SQLException("Failed to add a record into " + uri);
        }

    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        ImageDataBaseHelper dbHelper = new ImageDataBaseHelper(
                context);
        db = dbHelper.getWritableDatabase();
        return (db == null) ? false : true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        String table_Name = uri.getPathSegments().get(0);
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(table_Name);

        switch (uriMatcher.match(uri)) {
            case IMAGE_ALL:

                break;
            case IMAGE_SPECFIC:
                qb.appendWhere(col_id + "="
                        + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        Cursor c = qb.query(db, projection, selection, selectionArgs, null,
                null, sortOrder);

        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count = 0;
        String table_Name = uri.getPathSegments().get(0);
        switch (uriMatcher.match(uri)) {
            case IMAGE_ALL:
                count = db.update(table_Name, values, selection, selectionArgs);
                break;
            case IMAGE_SPECFIC:
                count = db.update(
                        table_Name,
                        values,
                        col_id
                                + " = "
                                + uri.getPathSegments().get(1)
                                + (!TextUtils.isEmpty(selection) ? " AND ("
                                + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    public class ImageDataBaseHelper extends SQLiteOpenHelper {

        Context ctx;

        public ImageDataBaseHelper(Context context) {
            super(context, dbName, null, version);
            this.ctx = context;
        }

        private static final String DATABASE_CREATE_IMAGE_LIST = "CREATE TABLE "
                + tableImageList
                + " ("
                + col_id
                + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + col_imageurl
                + " TEXT );";

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE_IMAGE_LIST);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + tableImageList);
        }

        public void open(int mode) {
            try {
                if (mode == readMode) {

                    db = this.getReadableDatabase();
                } else if (mode == writeMode) {

                    db = this.getWritableDatabase();

                }
            } catch (Exception e) {

            }
        }

        public void close() {
            if (db != null) {
                db.close();
                db = null;
            }
        }

        public int countImage() {
            Cursor cur = db.rawQuery("Select * from " + tableImageList,
                    null);
            int x = cur.getCount();
            cur.close();
            return x;
        }

        public Uri insertImageDetail(String imageUrl) {

            ContentValues cv = new ContentValues();
            cv.put(col_imageurl, imageUrl);

            String URL = "content://" + PROVIDER_NAME + "/"
                    + tableImageList;
            Uri CONTENT_URI = Uri.parse(URL);
            return ctx.getContentResolver().insert(CONTENT_URI, cv);
        }


        public ArrayList<ImageModel> selectImageDetailList() {
            String URL = "content://" + PROVIDER_NAME + "/" + tableImageList;
            Uri contact = Uri.parse(URL);
            Cursor cursor = ctx.getContentResolver().query(contact, null,
                    null, null,
                    null);
            ArrayList<ImageModel> ImageModelArrayList = new ArrayList<ImageModel>();
            if (cursor.getCount() != 0) {
                if (ImageModelArrayList != null) {
                    ImageModelArrayList.clear();
                }
                cursor.moveToFirst();
                do {
                    ImageModel ImageModel = new ImageModel();
                    ImageModel.setId(cursor.getString(cursor
                            .getColumnIndex(ImageConstant.ID)));
                    ImageModel.setImageUrl(cursor
                            .getString(cursor
                                    .getColumnIndex(ImageConstant.IMAGE_URL)));
                    ImageModelArrayList.add(ImageModel);

                } while (cursor.moveToNext());
            }
            return ImageModelArrayList;
        }
    }

    public class ImageConstant {

        public static final String ImageTABLENAME = "tvimage";
        public static final String ID = "_id";
        public static final String IMAGE_URL = "imageUrl";
    }

}