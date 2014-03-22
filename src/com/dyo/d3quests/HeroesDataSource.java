/**
 * 
 */
package com.dyo.d3quests;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author yinglong
 * 
 */
public class HeroesDataSource {

	// Database fields
	private SQLiteDatabase database;
	private HeroesOpenHelper dbHelper;
	private String[] allColumns = { HeroesOpenHelper.COLUMN_ID,
			HeroesOpenHelper.HERO_ID, HeroesOpenHelper.HERO_NAME,
			HeroesOpenHelper.HERO_LEVEL, HeroesOpenHelper.HERO_CLASS,
			HeroesOpenHelper.BATTLETAG_FULL, HeroesOpenHelper.REGION };

	/**
	 * 
	 */
	public HeroesDataSource(Context context) {
		dbHelper = new HeroesOpenHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public SavedHero createSavedHero(SavedHero hero) {
		ContentValues values = new ContentValues();
		values.put(HeroesOpenHelper.HERO_ID, hero.getHeroId());
		values.put(HeroesOpenHelper.HERO_NAME, hero.getHeroName());
		values.put(HeroesOpenHelper.HERO_LEVEL, hero.getHeroLevel());
		values.put(HeroesOpenHelper.HERO_CLASS, hero.getHeroClass());
		values.put(HeroesOpenHelper.BATTLETAG_FULL, hero.getBattleTagFull());
		values.put(HeroesOpenHelper.REGION, hero.getRegion());
		long insertId = database.insert(HeroesOpenHelper.HEROES_TABLE_NAME,
				null, values);
		Cursor cursor = database.query(HeroesOpenHelper.HEROES_TABLE_NAME,
				allColumns, HeroesOpenHelper.COLUMN_ID + " = " + insertId,
				null, null, null, null);
		cursor.moveToFirst();
		SavedHero newHero = cursorToSavedHero(cursor);
		cursor.close();
		return newHero;
	}

	public List<SavedHero> getAllHeroes() {
		List<SavedHero> heroes = new ArrayList<SavedHero>();

		Cursor cursor = database.query(HeroesOpenHelper.HEROES_TABLE_NAME,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			SavedHero hero = cursorToSavedHero(cursor);
			heroes.add(hero);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return heroes;
	}

	private SavedHero cursorToSavedHero(Cursor cursor) {
		SavedHero savedHero = new SavedHero();
		savedHero.setId(cursor.getLong(0));
		savedHero.setHeroId(cursor.getString(1));
		savedHero.setHeroName(cursor.getString(2));
		savedHero.setHeroLevel(cursor.getInt(3));
		savedHero.setHeroClass(cursor.getString(4));
		savedHero.setBattleTagFull(cursor.getString(5));
		savedHero.setRegion(cursor.getString(6));
		return savedHero;
	}

}
