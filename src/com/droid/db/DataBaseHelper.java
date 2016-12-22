package com.droid.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {

	private static final int VERSION = 1;
	String sql = "create table if not exists selectedApps"
			+ "( position integer primary key,packageName string ,appName String,appIcon Blob)";

	// String sql = "create table if not exists TestUsers"+
	// "(id int primary key,name varchar,sex varchar)";

	public DataBaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	public DataBaseHelper(Context context, String name, int version) {
		this(context, name, null, version);
	}

	public DataBaseHelper(Context context, String name) {
		this(context, name, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
