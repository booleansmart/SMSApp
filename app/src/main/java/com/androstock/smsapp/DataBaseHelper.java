package com.androstock.smsapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "kunci.db";
    private static final String TABLE_NAME = "key_collection";
    private static final String COLUMN_NUM = "column_phone";
    private static final String PrKEY = "prkeys";
    private static final String PuKEY = "pukeys";
    private static final String TempPuKEY = "temppukeys";
    private static final String InitPrKEY = "initprkeys";
    private static final String InitPuKEY = "initpukeys";

    SQLiteDatabase db;

    private static final String TABLE_CREATE = "create table key_collection(column_phone text not null, prkeys text not null, pukeys text not null, temppukeys not null, initprkeys text not null, initpukeys text not null)";

    public DataBaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        this.db = db;
    }



    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS "+TABLE_NAME;
        db.execSQL(query);
        this.onCreate(db);
    }

    public void storeKey(String number, String prpass, String pupass){
        db = this.getWritableDatabase();

        String uhu = null;
        if(number.substring(0,1).equals("0")) uhu = number.substring(1,number.length());
        else if (number.substring(0,3).equals("+62")) uhu = number.substring(3,number.length());
        else uhu = number;

        ContentValues values = new ContentValues();
        values.put(COLUMN_NUM, uhu);
        values.put(PrKEY, prpass);
        values.put(PuKEY,pupass);
        values.put(TempPuKEY,pupass);
        values.put(InitPrKEY, prpass);
        values.put(InitPuKEY, pupass);
        db.insert(TABLE_NAME, null, values);
    }

    public void updatePuKey(String newKey, String phone){
        db = this.getWritableDatabase();

        String uhu = null;
        if(phone.substring(0,1).equals("0")) uhu = phone.substring(1,phone.length());
        else if (phone.substring(0,3).equals("+62")) uhu = phone.substring(3,phone.length());
        else uhu = phone;

        ContentValues values = new ContentValues();
        values.put(PuKEY,newKey);
        db.update(TABLE_NAME, values, COLUMN_NUM+"="+uhu,null);
    }

    public void updateTempPuKey(String newKey, String phone){
        db = this.getWritableDatabase();

        String uhu = null;
        if(phone.substring(0,1).equals("0")) uhu = phone.substring(1,phone.length());
        else if (phone.substring(0,3).equals("+62")) uhu = phone.substring(3,phone.length());
        else uhu = phone;

        ContentValues values = new ContentValues();
        values.put(TempPuKEY,newKey);
        db.update(TABLE_NAME, values, COLUMN_NUM+"="+uhu,null);
    }


    public void updatePrKey(String newKey, String phone){
        db = this.getWritableDatabase();

        String uhu = null;
        if(phone.substring(0,1).equals("0")) uhu = phone.substring(1,phone.length());
        else if (phone.substring(0,3).equals("+62")) uhu = phone.substring(3,phone.length());
        else uhu = phone;

        ContentValues values = new ContentValues();
        values.put(PrKEY,newKey);
        db.update(TABLE_NAME, values, COLUMN_NUM+"="+uhu,null);
    }

    public String callPuKey(String number){
        db = this.getReadableDatabase();

        String uhu = null;
        if(number.substring(0,1).equals("0")) uhu = number.substring(1,number.length());
        else if (number.substring(0,3).equals("+62")) uhu = number.substring(3,number.length());
        else uhu = number;


        String query = "select "+PuKEY+" from "+TABLE_NAME+" where "+COLUMN_NUM+" = "+uhu;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null){
            cursor.moveToFirst();
        }
        return cursor.getString(0);
    }


    public String callTempPuKey(String number){
        db = this.getReadableDatabase();
        String uhu = null;
        if(number.substring(0,1).equals("0")) uhu = number.substring(1,number.length());
        else if (number.substring(0,3).equals("+62")) uhu = number.substring(3,number.length());
        else uhu = number;

        String query = "select "+TempPuKEY+" from "+TABLE_NAME+" where "+COLUMN_NUM+" = "+uhu;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null){
            cursor.moveToFirst();
        }
        return cursor.getString(0);
    }

    public String callPrKey(String number){
        db = this.getReadableDatabase();

        String uhu = null;
        if(number.substring(0,1).equals("0")) uhu = number.substring(1,number.length());
        else if (number.substring(0,3).equals("+62")) uhu = number.substring(3,number.length());
        else uhu = number;


        String query = "select "+PrKEY+" from "+TABLE_NAME+" where "+COLUMN_NUM+" = "+uhu;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null){
            cursor.moveToFirst();
        }
        return cursor.getString(0);
    }

    public String callInitPrKey(String number){
        db = this.getReadableDatabase();

        String uhu = null;
        if(number.substring(0,1).equals("0")) uhu = number.substring(1,number.length());
        else if (number.substring(0,3).equals("+62")) uhu = number.substring(3,number.length());
        else uhu = number;


        String query = "select "+InitPrKEY+" from "+TABLE_NAME+" where "+COLUMN_NUM+" = "+uhu;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null){
            cursor.moveToFirst();
        }
        return cursor.getString(0);
    }

    public String callInitPuKey(String number){
        db = this.getReadableDatabase();

        String uhu = null;
        if(number.substring(0,1).equals("0")) uhu = number.substring(1,number.length());
        else if (number.substring(0,3).equals("+62")) uhu = number.substring(3,number.length());
        else uhu = number;


        String query = "select "+InitPuKEY+" from "+TABLE_NAME+" where "+COLUMN_NUM+" = "+uhu;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null){
            cursor.moveToFirst();
        }
        return cursor.getString(0);
    }

    public boolean checkKunci(String number){
        db = this.getReadableDatabase();
        String query = "select "+PuKEY+" from "+TABLE_NAME+" where "+COLUMN_NUM+" = "+number;
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
        }
    }
