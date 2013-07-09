package com.happyReading.dao;

import java.util.ArrayList;
import java.util.List;

import com.happyReading.application.MyApplication;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ShelfDao {
	private static final String path = "data/data/com.happyReading/files/happyReading.db";
	/**
	 * ��ȡ�����С˵����
	 * 
	 * @return �����С˵����
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
	 * �ж�С˵�Ƿ��������
	 * @param novelId С˵��Id
	 * @return �����������򷵻�1�����򷵻�0
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
	 * ��������Ƴ�һ��С˵
	 * @param courseId  С˵���
	 */
	public static void deleteShelfNovelByNovelId(int novelId) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		db.execSQL("delete from shelf where novelId=?",new String[]{novelId+""});
		db.execSQL("update novel set inShelf=0 where _id=?",new String[]{novelId+""});
		db.close();
	}
	/**
	 * ������ϼ���һ��С˵
	 * @param courseId	С˵���
	 */
	public static void insertShelfNovel(int novelId) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		db.execSQL("insert into shelf (novelId) values (?)",new String[]{novelId+""});
		db.execSQL("update novel set inShelf=1 where _id=?",new String[]{novelId+""});
		db.close();
	}
	/**
	 * ��ȡ��������е�С˵��ID�������Ⱥ��Ķ���˳���ŷ�
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
	 * ��С˵��ID���룬�����ⱾС˵Ϊ����Ķ����Զ����ú�����readOrderNum
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
