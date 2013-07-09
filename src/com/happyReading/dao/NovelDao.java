package com.happyReading.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.happyReading.bean.BaseNovelInfo;
import com.happyReading.bean.DetailedNovelNoListInfo;
import com.happyReading.domain.Novel;

public class NovelDao {
	private static final String path = "data/data/com.happyReading/files/happyReading.db";

	/**
	 * ����С˵��Id��ȡС˵����Ϣ
	 * 
	 * @param novelId
	 * @return
	 */
	public static Novel getNovel(int novelId) {
		Novel novel = new Novel();
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select * from novel where _id=?",
				new String[] { novelId + "" });
		if (cursor.moveToNext()) {
			novel.set_id(cursor.getInt(0));
			novel.setName(cursor.getString(1));
			novel.setAuther(cursor.getString(2));
			novel.setUrl(cursor.getString(3));
			novel.setSummary(cursor.getString(4));
			novel.setLastChapterIndex(cursor.getInt(5));
			novel.setLastChapterName(cursor.getString(6));
			novel.setLastChapterUrl(cursor.getString(7));
			novel.setInShelf(cursor.getInt(8));
			novel.setLoading(cursor.getInt(9));
			novel.setWaitLoad(cursor.getInt(10));
			novel.setLoaded(cursor.getInt(11));
			novel.setLoadProgress(cursor.getString(12));
			novel.setOnline(cursor.getInt(13));
			novel.setUpdateNotification(cursor.getInt(14));
			novel.setTxtName(cursor.getString(15));
			novel.setLoadedChapterNum(cursor.getInt(16));
			novel.setReadBegin(cursor.getInt(17));
			novel.setReadEnd(cursor.getInt(18));
			novel.setTxtLen(cursor.getInt(19));
			novel.setReadedChapterIndex(cursor.getInt(20));
		}
		cursor.close();
		db.close();
		return novel;
	}

	/**
	 * �����ݿ��д���С˵
	 * 
	 * @param detailedNovelNoListInfo
	 *            �������½���Ϣ��С˵��ϸ��Ϣ��
	 */
	public static void insertNovel(
			DetailedNovelNoListInfo detailedNovelNoListInfo) {
		String name = detailedNovelNoListInfo.getNovelTitle();
		String auther = detailedNovelNoListInfo.getNovelAuther();
		String url = detailedNovelNoListInfo.getNovelChapterUrl();
		String summary = detailedNovelNoListInfo.getNovelSummary();
		String lastChapterName = detailedNovelNoListInfo
				.getNovelLastChapterName();
		String lastChapterUrl = detailedNovelNoListInfo
				.getNovelLastChapterUrl();
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		db.execSQL(
				"insert into novel (name,auther,url,summary,lastChapterName,lastChapterUrl) values (?,?,?,?,?,?)",
				new String[] { name, auther, url, summary, lastChapterName,
						lastChapterUrl });
		db.close();
	}

	/**
	 * ��ȡһ��С˵�����ݿ��е����
	 * 
	 * @param info
	 * @return �����ȡ�ɹ��򷵻���ţ�ʧ�ܷ���Ϊ0
	 */
	public static int getNovelId(BaseNovelInfo info) {
		int result = 0;
		String url = info.getNovelChapterUrl();
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select _id from novel where url=?",
				new String[] { url });
		if (cursor.moveToNext()) {
			int _id = cursor.getInt(0);
			result = _id;
		}
		cursor.close();
		db.close();
		return result;
	}

	/**
	 * ��ȡһ������С˵�������ݿ�����ӵ�е����½���
	 * 
	 * @param novelId
	 * @return
	 */
	public static int getNovelLastChapterIndex(int novelId) {
		int result = 0;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery(
				"select lastChapterIndex from novel where _id=?",
				new String[] { novelId + "" });
		if (cursor.moveToNext()) {
			result = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return result;
	}

	/**
	 * ��ȡһ��С˵�����ݿ��е����ؽ���
	 * 
	 * @param novelId
	 * @return
	 */
	public static int getNovelLoadedChapterNum(int novelId) {
		int result = 0;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery(
				"select loadedChapterNum from novel where _id=?",
				new String[] { novelId + "" });
		if (cursor.moveToNext()) {
			result = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return result;
	}
	
	/**
	 * ��ȡһ������С˵�����ݿ��е��Ѿ��������½�
	 * 
	 * @param novelId
	 * @return
	 */
	public static int getNovelReadedChapterIndex(int novelId) {
		int result = 0;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery(
				"select readedChapterIndex from novel where _id=?",
				new String[] { novelId + "" });
		if (cursor.moveToNext()) {
			result = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return result;
	}

	/**
	 * �����ݿ��д�������С˵������С˵Ĭ�Ϸ��������
	 * 
	 * @param detailedNovelNoListInfo
	 *            �������½���Ϣ��С˵��ϸ��Ϣ��
	 */
	public static void insertOnlineNovel(
			DetailedNovelNoListInfo detailedNovelNoListInfo) {
		String name = detailedNovelNoListInfo.getNovelTitle();
		String auther = detailedNovelNoListInfo.getNovelAuther();
		String url = detailedNovelNoListInfo.getNovelChapterUrl();
		String summary = detailedNovelNoListInfo.getNovelSummary();
		String lastChapterName = detailedNovelNoListInfo
				.getNovelLastChapterName();
		String lastChapterUrl = detailedNovelNoListInfo
				.getNovelLastChapterUrl();
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		db.execSQL(
				"insert into novel (name,auther,url,summary,lastChapterName,lastChapterUrl,inShelf,online) values (?,?,?,?,?,?,?,?)",
				new String[] { name, auther, url, summary, lastChapterName,
						lastChapterUrl, 1 + "", 1 + "" });
		int novelId = getNovelId(detailedNovelNoListInfo);
		db.execSQL("insert into shelf (novelId) values (?)",
				new String[] { novelId + "" });
		db.close();
	}

	/**
	 * �����ݿ��д����������ص�С˵
	 * 
	 * @param detailedNovelNoListInfo
	 *            �������½���Ϣ��С˵��ϸ��Ϣ��
	 */
	public static void insertLoadingNovel(
			DetailedNovelNoListInfo detailedNovelNoListInfo) {
		String name = detailedNovelNoListInfo.getNovelTitle();
		String auther = detailedNovelNoListInfo.getNovelAuther();
		String url = detailedNovelNoListInfo.getNovelChapterUrl();
		String summary = detailedNovelNoListInfo.getNovelSummary();
		String lastChapterName = detailedNovelNoListInfo
				.getNovelLastChapterName();
		String lastChapterUrl = detailedNovelNoListInfo
				.getNovelLastChapterUrl();
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		db.execSQL(
				"insert into novel (name,auther,url,summary,lastChapterName,lastChapterUrl,loading) values (?,?,?,?,?,?,?)",
				new String[] { name, auther, url, summary, lastChapterName,
						lastChapterUrl, 1 + "" });
		db.close();
	}

	/**
	 * �����ݿ��д���ȴ����ص�С˵
	 * 
	 * @param detailedNovelNoListInfo
	 *            �������½���Ϣ��С˵��ϸ��Ϣ��
	 */
	public static void insertWaitLoadNovel(
			DetailedNovelNoListInfo detailedNovelNoListInfo) {
		String name = detailedNovelNoListInfo.getNovelTitle();
		String auther = detailedNovelNoListInfo.getNovelAuther();
		String url = detailedNovelNoListInfo.getNovelChapterUrl();
		String summary = detailedNovelNoListInfo.getNovelSummary();
		String lastChapterName = detailedNovelNoListInfo
				.getNovelLastChapterName();
		String lastChapterUrl = detailedNovelNoListInfo
				.getNovelLastChapterUrl();
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		db.execSQL(
				"insert into novel (name,auther,url,summary,lastChapterName,lastChapterUrl,waitLoad) values (?,?,?,?,?,?,?)",
				new String[] { name, auther, url, summary, lastChapterName,
						lastChapterUrl, 1 + "" });
		db.close();
	}

	/**
	 * ��С˵����Ϊ�������
	 * 
	 * @param novelId
	 */
	public static void setNovelNotInshelf(int novelId) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		db.execSQL("update novel set inShelf=0 where _id=?",
				new String[] { novelId + "" });
		db.close();
	}

	/**
	 * ����С˵�´ζ�����ʼλ��
	 * 
	 * @param novelId
	 *            С˵�����
	 * @param readBegin
	 *            ���Ϸ�ҳʱ����ʼ
	 * @param readEnd
	 *            ���·�ҳ����ʼ
	 */
	public static void setNovelReadAfter(int novelId, int readBegin, int readEnd) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		db.execSQL("update novel set readBegin=?,readEnd=? where _id=?",
				new String[] { readBegin + "", readEnd + "", novelId + "" });
		db.close();
	}

	/**
	 * ����С˵���ܳ���
	 * 
	 * @param novelId
	 *            С˵�����
	 * @param txtLen
	 *            С˵���ܳ���
	 */
	public static void setNovelTxtLen(int novelId, int txtLen) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		db.execSQL("update novel set txtLen=? where _id=?", new String[] {
				txtLen + "", novelId + "" });
		db.close();
	}

	/**
	 * ������С˵�������½����������ݿ��У�Ϊ�������Ѷ����õ�
	 * 
	 * @param novelId
	 * @param index
	 */
	public static void setNovelLastChapterIndex(int novelId, int index) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		db.execSQL("update novel set lastChapterIndex=? where _id=?",
				new String[] { index + "", novelId + "" });
		db.close();
	}

	/**
	 * ����С˵�ĸ�������״̬
	 * 
	 * @param novelId
	 *            С˵�����
	 * @param state
	 *            ״̬0��ʾ�����ø������ѣ�״̬1��ʾ�����˸������ѣ�״̬2��ʾ��ǰС˵�и���
	 */
	public static void setUpdateNotification(int novelId, int state) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		db.execSQL("update novel set updateNotification=? where _id=?",
				new String[] { state + "", novelId + "" });
		db.close();
	}

	/**
	 * ��С˵����Ϊ�����
	 * 
	 * @param novelId
	 */
	public static void setNovelInshelf(int novelId) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		db.execSQL("update novel set inShelf=1 where _id=?",
				new String[] { novelId + "" });
		db.close();
	}

	/**
	 * ��С˵����������תΪ�Ѿ�����
	 * 
	 * @param novelId
	 */
	public static void setLoadingToLoaded(int novelId) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		db.execSQL("update novel set loading=0,loaded=1 where _id=?",
				new String[] { novelId + "" });
		db.close();
	}

	/**
	 * ����С˵�����ؽ���
	 * 
	 * @param novelId
	 */
	public static void setLoadProgress(int novelId, String loadProgress) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		db.execSQL("update novel set loadProgress=? where _id=?", new String[] {
				loadProgress, novelId + "" });
		db.close();
	}

	/**
	 * ��С˵�ӵȴ�����תΪ��������
	 * 
	 * @param novelId
	 */
	public static void setWaitLoadToLoading(int novelId) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		db.execSQL("update novel set loading=1,waitLoad=0 where _id=?",
				new String[] { novelId + "" });
		db.close();
	}

	/**
	 * ����С˵����ĵ�txt���Ƶ����ݿ�
	 * 
	 * @param novelId
	 *            С˵��ID
	 * @param txtName
	 *            С˵����ĵ�txt����
	 */
	public static void setNovelTxtName(int novelId, String txtName) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		db.execSQL("update novel set txtName=? where _id=?", new String[] {
				txtName, novelId + "" });
		db.close();
	}

	/**
	 * ����С˵�Ѿ����ص��½���Ŀ
	 * 
	 * @param novelId
	 *            С˵��ID
	 * @param txtName
	 *            С˵�Ѿ����ص��½���Ŀ
	 */
	public static void setNovelLoadedChapterNum(int novelId,
			int loadedChapterNum) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		db.execSQL("update novel set loadedChapterNum=? where _id=?",
				new String[] { loadedChapterNum + "", novelId + "" });
		db.close();
	}

	/**
	 * ��С˵����������תΪ�ȴ�����
	 * 
	 * @param novelId
	 */
	public static void setLoadingToWaitLoad(int novelId) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		db.execSQL("update novel set loading=0,waitLoad=1 where _id=?",
				new String[] { novelId + "" });
		db.close();
	}

	/**
	 * ��С˵����Ϊ����
	 * 
	 * @param detailedNovelNoListInfo
	 *            �������½���Ϣ��С˵��ϸ��Ϣ��
	 */
	public static void setNovelOnlineAndInShelf(
			DetailedNovelNoListInfo detailedNovelNoListInfo) {
		String url = detailedNovelNoListInfo.getNovelChapterUrl();
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		db.execSQL("update novel set inShelf=1 where url=?", new String[] { url
				+ "" });
		int novelId = getNovelId(detailedNovelNoListInfo);
		db.execSQL("insert into shelf (novelId) values (?)",
				new String[] { novelId + "" });
		db.close();
	}

	/**
	 * ��С˵����Ϊ���ߣ����Ҽ������
	 * 
	 * @param detailedNovelNoListInfo
	 *            �������½���Ϣ��С˵��ϸ��Ϣ��
	 */
	public static void setNovelOnlineAndInShelf(int novelId) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		db.execSQL("update novel set inShelf=1,online=1 where _id=?",
				new String[] { novelId + "" });
		db.execSQL("insert into shelf (novelId) values (?)",
				new String[] { novelId + "" });
		db.close();
	}

	/**
	 * ��С˵����Ϊ����
	 * 
	 * @param detailedNovelNoListInfo
	 *            �������½���Ϣ��С˵��ϸ��Ϣ��
	 */
	public static void setNovelOnline(int novelId) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		db.execSQL("update novel set inShelf=1,online=1 where _id=?",
				new String[] { novelId + "" });
		db.close();
	}

	/**
	 * ��С˵�Ѿ�����ʱ��ȡ��С˵���߹ۿ�
	 * 
	 * @param detailedNovelNoListInfo
	 *            �������½���Ϣ��С˵��ϸ��Ϣ��
	 */
	public static void setNovelNotOnline(int novelId) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		db.execSQL("update novel set online=0 where _id=?",
				new String[] { novelId + "" });
		db.close();
	}

	/**
	 * ����С˵�����Ķ��½�
	 * @param novelId
	 * @param readedChapterIndex
	 */
	public static void setNovelReadedChapterIndex(int novelId,
			int readedChapterIndex) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		db.execSQL("update novel set readedChapterIndex=? where _id=?",
				new String[] { readedChapterIndex + "", novelId + "" });
		db.close();
	}

	/**
	 * ��С˵����Ϊ��������
	 * 
	 * @param detailedNovelNoListInfo
	 *            �������½���Ϣ��С˵��ϸ��Ϣ��
	 */
	public static void setNovelLoading(int novelId) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		db.execSQL("update novel set loading=1 where _id=?",
				new String[] { novelId + "" });
		db.close();
	}

	/**
	 * ��С˵�����ݿ��е����ؼ�¼���
	 * 
	 * @param novelId
	 *            С˵��ID
	 */
	public static void setNovelToNoLoad(int novelId) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		db.execSQL(
				"update novel set loading=0,waitLoad=0,loaded=0,txtName=?,loadProgress=?,loadedChapterNum=0 where _id=?",
				new String[] { "", "", novelId + "" });
		db.close();
	}

	/**
	 * ���Ƴ�һ��С˵
	 * 
	 * @param courseId
	 *            С˵���
	 */
	public static void deleteNovelByNovelId(int novelId) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		db.execSQL("delete from shelf where novelId=?", new String[] { novelId
				+ "" });
		db.execSQL("delete from novel where _id=?",
				new String[] { novelId + "" });
		db.close();
	}

	/**
	 * ��ȡһ��С˵����SD���е��ļ���
	 * 
	 * @param novelId
	 * @return
	 */
	public static String getNovelTxtName(int novelId) {
		String result = "";
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select txtName from novel where _id=?",
				new String[] { novelId + "" });
		if (cursor.moveToNext()) {
			result = cursor.getString(0);
		}
		cursor.close();
		db.close();
		return result;
	}

	/**
	 * ��ȡĳ������Ϊ1������С˵
	 * 
	 * @param columnName
	 *            ����
	 * @return ��������С˵����Ϣ
	 */
	private static List<Novel> getNovelsByColumnName(String columnName) {
		List<Novel> novels = new ArrayList<Novel>();
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select * from novel where " + columnName
				+ "=1", null);
		while (cursor.moveToNext()) {
			Novel novel = new Novel();
			novel.set_id(cursor.getInt(0));
			novel.setName(cursor.getString(1));
			novel.setAuther(cursor.getString(2));
			novel.setUrl(cursor.getString(3));
			novel.setSummary(cursor.getString(4));
			novel.setLastChapterIndex(cursor.getInt(5));
			novel.setLastChapterName(cursor.getString(6));
			novel.setLastChapterUrl(cursor.getString(7));
			novel.setInShelf(cursor.getInt(8));
			novel.setLoading(cursor.getInt(9));
			novel.setWaitLoad(cursor.getInt(10));
			novel.setLoaded(cursor.getInt(11));
			novel.setLoadProgress(cursor.getString(12));
			novel.setOnline(cursor.getInt(13));
			novel.setUpdateNotification(cursor.getInt(14));
			novel.setTxtName(cursor.getString(15));
			novel.setLoadedChapterNum(cursor.getInt(16));
			novel.setReadBegin(cursor.getInt(17));
			novel.setReadEnd(cursor.getInt(18));
			novel.setTxtLen(cursor.getInt(19));
			novel.setReadedChapterIndex(cursor.getInt(20));
			novels.add(novel);
		}
		cursor.close();
		db.close();
		return novels;
	}

	/**
	 * ��ȡ������ϵ�С˵
	 * 
	 * @return
	 */
	public static List<Novel> getNovelsInShelf() {
		return getNovelsByColumnName("inShelf");
	}

	/**
	 * ��ȡ�������ص�С˵
	 * 
	 * @return
	 */
	public static List<Novel> getNovelsLoading() {
		return getNovelsByColumnName("loading");
	}

	/**
	 * ��ȡ�Ѿ����ص�С˵
	 * 
	 * @return
	 */
	public static List<Novel> getNovelsLoaded() {
		return getNovelsByColumnName("loaded");
	}

	/**
	 * ��ȡ�ȴ����ص�С˵
	 * 
	 * @return
	 */
	public static List<Novel> getNovelsWaitLoad() {
		return getNovelsByColumnName("waitLoad");
	}

	/**
	 * ��ȡĳ������Ϊtrue��С˵�ĸ���
	 * 
	 * @param columnName
	 *            ����
	 * @return �ܸ���
	 */
	private static int getNovelsCountByColumnName(String columnName) {
		int count = 0;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select * from novel where " + columnName
				+ "=1", null);
		count = cursor.getCount();
		cursor.close();
		db.close();
		return count;
	}

	/**
	 * ��ȡ������ϵ�С˵�ĸ���
	 * 
	 * @return
	 */
	public static int getNovelsInShelfCount() {

		return getNovelsCountByColumnName("inShelf");
	}

	/**
	 * ��ȡ�������ص�С˵�ĸ���
	 * 
	 * @return
	 */
	public static int getNovelsLoadingCount() {

		return getNovelsCountByColumnName("loading");
	}

	/**
	 * ��ȡ�������ص�С˵�ĸ���
	 * 
	 * @return
	 */
	public static int getNovelsLoadedCount() {

		return getNovelsCountByColumnName("loaded");
	}

	/**
	 * ��ȡ�ȴ����ص�С˵�ĸ���
	 * 
	 * @return
	 */
	public static int getNovelsWaitLoadCount() {

		return getNovelsCountByColumnName("waitLoad");
	}

	/**
	 * �ж�С˵�Ƿ�������ݿ��У����������жϣ�������ͬ����Ϊ����
	 * 
	 * @param baseNovelInfo
	 *            С˵�Ļ���
	 * @return ����1��˵�����ڣ�0��˵��������
	 */
	public static int isNovelAlreadyHave(BaseNovelInfo baseNovelInfo) {
		int result = 0;
		String name = baseNovelInfo.getNovelTitle();
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select _id from novel where name=?",
				new String[] { name });
		if (cursor.moveToNext()) {
			result = 1;
		}
		cursor.close();
		db.close();
		return result;
	}

	/**
	 * �ж�С˵�Ƿ���������
	 * 
	 * @param novelId
	 *            С˵��_id
	 * @return �����ط���1���������ط���0��
	 */
	public static int isNovelLoading(int novelId) {
		int result = 0;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select loading from novel where _id=?",
				new String[] { novelId + "" });
		if (cursor.moveToNext()) {
			result = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return result;
	}

	/**
	 * �ж�С˵�Ƿ����ڵȴ�����
	 * 
	 * @param novelId
	 *            С˵��_id
	 * @return �����ط���1���������ط���0��
	 */
	public static int isNovelWaitLoad(int novelId) {
		int result = 0;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select waitLoad from novel where _id=?",
				new String[] { novelId + "" });
		if (cursor.moveToNext()) {
			result = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return result;
	}

	/**
	 * �ж�С˵�Ƿ��Ѿ�����
	 * 
	 * @param novelId
	 *            С˵��_id
	 * @return �����ط���1���������ط���0��
	 */
	public static int isNovelLoaded(int novelId) {
		int result = 0;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select loaded from novel where _id=?",
				new String[] { novelId + "" });
		if (cursor.moveToNext()) {
			result = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return result;
	}

	/**
	 * �ж�ͬ��С˵�Ƿ���������
	 * 
	 * @param novelName
	 *            С˵��name
	 * @return �����ط���1���������ط���0��
	 */
	public static int isNovelLoading(String novelName) {
		int result = 0;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select loading from novel where name=?",
				new String[] { novelName });
		while (cursor.moveToNext()) {
			if (cursor.getInt(0) == 1) {
				result = cursor.getInt(0);
				break;
			}
		}
		cursor.close();
		db.close();
		return result;
	}

	/**
	 * �ж�ͬ��С˵�Ƿ����ڵȴ�����
	 * 
	 * @param novelName
	 *            С˵��name
	 * @return �����ط���1���������ط���0��
	 */
	public static int isNovelWaitLoad(String novelName) {
		int result = 0;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select waitLoad from novel where name=?",
				new String[] { novelName });
		while (cursor.moveToNext()) {
			if (cursor.getInt(0) == 1) {
				result = cursor.getInt(0);
				break;
			}
		}
		cursor.close();
		db.close();
		return result;
	}

	/**
	 * �ж�ͬ��С˵�Ƿ��Ѿ�����
	 * 
	 * @param novelName
	 *            С˵��name
	 * @return �����ط���1���������ط���0��
	 */
	public static int isNovelLoaded(String novelName) {
		int result = 0;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select loaded from novel where name=?",
				new String[] { novelName });
		while (cursor.moveToNext()) {
			if (cursor.getInt(0) == 1) {
				result = cursor.getInt(0);
				break;
			}
		}
		cursor.close();
		db.close();
		return result;
	}

	/**
	 * �ж�ͬ��С˵�Ƿ��Ѿ�����
	 * 
	 * @param novelName
	 *            С˵��name
	 * @return �����ط���1���������ط���0��
	 */
	public static int isNovelOnline(String novelName) {
		int result = 0;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select online from novel where name=?",
				new String[] { novelName });
		while (cursor.moveToNext()) {
			if (cursor.getInt(0) == 1) {
				result = cursor.getInt(0);
				break;
			}
		}
		cursor.close();
		db.close();
		return result;
	}

	/**
	 * �ж�ͬ��С˵�Ƿ��Ѿ�����
	 * 
	 * @param novelId
	 *            С˵��id
	 * @return �����ط���1���������ط���0��
	 */
	public static int isNovelOnline(int novelId) {
		int result = 0;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select online from novel where _id=?",
				new String[] { novelId + "" });
		while (cursor.moveToNext()) {
			if (cursor.getInt(0) == 1) {
				result = cursor.getInt(0);
				break;
			}
		}
		cursor.close();
		db.close();
		return result;
	}

	/**
	 * ��ȡС˵�ϴζ���һҳ�Ŀ�ʼλ��
	 * 
	 * @param novelId
	 *            С˵��id
	 * @return
	 */
	public static int getNovelReadBegin(int novelId) {
		int result = 0;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select readBegin from novel where _id=?",
				new String[] { novelId + "" });
		if (cursor.moveToNext()) {
			result = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return result;
	}

	/**
	 * ��ȡС˵�ϴζ���һҳ�Ľ���λ��
	 * 
	 * @param novelId
	 *            С˵��id
	 * @return
	 */
	public static int getNovelReadEnd(int novelId) {
		int result = 0;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select readEnd from novel where _id=?",
				new String[] { novelId + "" });
		if (cursor.moveToNext()) {
			result = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return result;
	}

	/**
	 * ��ȡС˵�������ļ��ĳ���
	 * 
	 * @param novelId
	 *            С˵��id
	 * @return
	 */
	public static int getNovelTxtLen(int novelId) {
		int result = 0;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select txtLen from novel where _id=?",
				new String[] { novelId + "" });
		if (cursor.moveToNext()) {
			result = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return result;
	}
}
