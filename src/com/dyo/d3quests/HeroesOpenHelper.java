/**
 * 
 */
package com.dyo.d3quests;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author yinglong
 *
 */
public class HeroesOpenHelper extends SQLiteOpenHelper {
	
	private static final int DATABASE_VERSION = 1;
	public static final String HEROES_TABLE_NAME = "heroes";
	private static final String HEROES_DATABASE_NAME ="heroes.db";
	public static final String COLUMN_ID = "_id";
	public static final String HERO_NAME = "hero_name";
	public static final String HERO_ID = "hero_id";
	public static final String HERO_LEVEL = "hero_level";
	public static final String HERO_CLASS = "hero_class";
	public static final String BATTLETAG_FULL = "battletag_full";
	public static final String REGION = "region";

	private static final String DATABASE_CREATE = "create table "
		      + HEROES_TABLE_NAME + "(" + COLUMN_ID
		      + " integer primary key autoincrement, " 
		      + HERO_ID + " text not null, "
		      + HERO_NAME + " text not null, "
		      + HERO_LEVEL + " integer, "
		      + HERO_CLASS + " text not null, "
		      + BATTLETAG_FULL + " text not null, "
		      + REGION + " text not null);";
	
	/**
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 */
	public HeroesOpenHelper(Context context) {
		super(context, HEROES_DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 * @param errorHandler
	 */
	public HeroesOpenHelper(Context context, String name,
			CursorFactory factory, int version,
			DatabaseErrorHandler errorHandler) {
		super(context, name, factory, version, errorHandler);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);

	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(HeroesOpenHelper.class.getName(),
	            "Upgrading database from version " + oldVersion + " to "
	                + newVersion + ", which will destroy all old data");
	        db.execSQL("DROP TABLE IF EXISTS " + HEROES_TABLE_NAME);
	        onCreate(db);

	}

}
