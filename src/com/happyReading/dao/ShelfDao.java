package com.happyReading.dao;

import java.util.ArrayList;
import java.util.List;

import com.happyReading.application.MyApplication;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ShelfDao {
	private static final String path = "data/data/com.happyReading/files/happyReading.db";
	/**
	 * 获取书架上小说个数
	 * 
	 * @return 书架上小说个数
	 */
	public static int getShelfNovelCount() {
		int count = 0;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select * from shelf", null);
		count = cursor.getCount();
		cursor.close();
		db.close();
		return count;
	}
	
	/**
	 * 判断小说是否在书架上
	 * @param novelId 小说的Id
	 * @return 如果在书架上则返回1，否则返回0
	 */
	public static int isNovelInShelf(int novelId) {
		int result = 0;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select * from shelf where novelId=?", new String[]{novelId+""});
		if(cursor.moveToNext()){
			result = 1;
		}
		cursor.close();
		db.close();
		return result;
	}
	
	
	/**
	 * 从书架上移除一本小说
	 * @param courseId  小说序号
	 */
	public static void deleteShelfNovelByNovelId(int novelId) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		db.execSQL("delete from shelf where novelId=?",new String[]{novelId+""});
		db.execSQL("update novel set inShelf=0 where _id=?",new String[]{novelId+""});
		db.close();
	}
	/**
	 * 往书架上加入一本小说
	 * @param courseId	小说序号
	 */
	public static void insertShelfNovel(int novelId) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		db.execSQL("insert into shelf (novelId) values (?)",new String[]{novelId+""});
		db.execSQL("update novel set inShelf=1 where _id=?",new String[]{novelId+""});
		db.close();
	}
	/**
	 * 获取书架上所有的小说的ID，按照先后阅读的顺序排放
	 * @return
	 */
	public static List<Integer> getNovelIdsInShelf(){
		List<Integer> novelIds = new ArrayList<Integer>();
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select novelId from shelf order by readOrderNum DESC", null);
		while(cursor.moveToNext()){
			int novelId = cursor.getInt(0);
			novelIds.add(novelId);
		}
		cursor.close();
		db.close();
		return novelIds;
	}
	/**
	 * 把小说的ID传入，设置这本小说为最近阅读，自动设置好它的readOrderNum
	 * @param novelId
	 */
	public static void setReadOrderNum(int novelId){
		MyApplication.lastReadOrderNum++;
		double readOrderNum = MyApplication.lastReadOrderNum;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		db.execSQL("update shelf set readOrderNum=? where novelId=?",new String[]{readOrderNum+"",novelId+""});
		db.close();
	}
}
