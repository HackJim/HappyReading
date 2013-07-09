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
	 * 根据小说的Id获取小说的信息
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
	 * 往数据库中存入小说
	 * 
	 * @param detailedNovelNoListInfo
	 *            不包含章节信息的小说详细信息类
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
	 * 获取一本小说在数据库中的序号
	 * 
	 * @param info
	 * @return 如果获取成功则返回序号，失败返回为0
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
	 * 获取一本在线小说，在数据库中所拥有的总章节数
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
	 * 获取一本小说在数据库中的下载进度
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
	 * 获取一本在线小说在数据库中的已经读到的章节
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
	 * 往数据库中存入在线小说，在线小说默认放在书架上
	 * 
	 * @param detailedNovelNoListInfo
	 *            不包含章节信息的小说详细信息类
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
	 * 往数据库中存入正在下载的小说
	 * 
	 * @param detailedNovelNoListInfo
	 *            不包含章节信息的小说详细信息类
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
	 * 往数据库中存入等待下载的小说
	 * 
	 * @param detailedNovelNoListInfo
	 *            不包含章节信息的小说详细信息类
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
	 * 将小说设置为不在书架
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
	 * 设置小说下次读的起始位置
	 * 
	 * @param novelId
	 *            小说的序号
	 * @param readBegin
	 *            向上翻页时的起始
	 * @param readEnd
	 *            向下翻页的起始
	 */
	public static void setNovelReadAfter(int novelId, int readBegin, int readEnd) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		db.execSQL("update novel set readBegin=?,readEnd=? where _id=?",
				new String[] { readBegin + "", readEnd + "", novelId + "" });
		db.close();
	}

	/**
	 * 设置小说的总长度
	 * 
	 * @param novelId
	 *            小说的序号
	 * @param txtLen
	 *            小说的总长度
	 */
	public static void setNovelTxtLen(int novelId, int txtLen) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		db.execSQL("update novel set txtLen=? where _id=?", new String[] {
				txtLen + "", novelId + "" });
		db.close();
	}

	/**
	 * 将在线小说的所有章节数存入数据库中，为更新提醒而设置的
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
	 * 设置小说的更新提醒状态
	 * 
	 * @param novelId
	 *            小说的序号
	 * @param state
	 *            状态0表示不设置更新提醒，状态1表示设置了更新提醒，状态2表示当前小说有更新
	 */
	public static void setUpdateNotification(int novelId, int state) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		db.execSQL("update novel set updateNotification=? where _id=?",
				new String[] { state + "", novelId + "" });
		db.close();
	}

	/**
	 * 将小说设置为在书架
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
	 * 将小说从正在下载转为已经下载
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
	 * 设置小说的下载进度
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
	 * 将小说从等待下载转为正在下载
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
	 * 设置小说存入的的txt名称到数据库
	 * 
	 * @param novelId
	 *            小说的ID
	 * @param txtName
	 *            小说存入的的txt名称
	 */
	public static void setNovelTxtName(int novelId, String txtName) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		db.execSQL("update novel set txtName=? where _id=?", new String[] {
				txtName, novelId + "" });
		db.close();
	}

	/**
	 * 设置小说已经下载的章节数目
	 * 
	 * @param novelId
	 *            小说的ID
	 * @param txtName
	 *            小说已经下载的章节数目
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
	 * 将小说从正在下载转为等待下载
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
	 * 将小说设置为在线
	 * 
	 * @param detailedNovelNoListInfo
	 *            不包含章节信息的小说详细信息类
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
	 * 将小说设置为在线，并且加入书架
	 * 
	 * @param detailedNovelNoListInfo
	 *            不包含章节信息的小说详细信息类
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
	 * 将小说设置为在线
	 * 
	 * @param detailedNovelNoListInfo
	 *            不包含章节信息的小说详细信息类
	 */
	public static void setNovelOnline(int novelId) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		db.execSQL("update novel set inShelf=1,online=1 where _id=?",
				new String[] { novelId + "" });
		db.close();
	}

	/**
	 * 当小说已经下载时，取消小说在线观看
	 * 
	 * @param detailedNovelNoListInfo
	 *            不包含章节信息的小说详细信息类
	 */
	public static void setNovelNotOnline(int novelId) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		db.execSQL("update novel set online=0 where _id=?",
				new String[] { novelId + "" });
		db.close();
	}

	/**
	 * 设置小说最后的阅读章节
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
	 * 将小说设置为正在下载
	 * 
	 * @param detailedNovelNoListInfo
	 *            不包含章节信息的小说详细信息类
	 */
	public static void setNovelLoading(int novelId) {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		db.execSQL("update novel set loading=1 where _id=?",
				new String[] { novelId + "" });
		db.close();
	}

	/**
	 * 将小说在数据库中的下载记录清空
	 * 
	 * @param novelId
	 *            小说的ID
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
	 * 从移除一本小说
	 * 
	 * @param courseId
	 *            小说序号
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
	 * 获取一本小说存在SD卡中的文件名
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
	 * 获取某列属性为1的所有小说
	 * 
	 * @param columnName
	 *            列名
	 * @return 符合条件小说的信息
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
	 * 获取在书架上的小说
	 * 
	 * @return
	 */
	public static List<Novel> getNovelsInShelf() {
		return getNovelsByColumnName("inShelf");
	}

	/**
	 * 获取正在下载的小说
	 * 
	 * @return
	 */
	public static List<Novel> getNovelsLoading() {
		return getNovelsByColumnName("loading");
	}

	/**
	 * 获取已经下载的小说
	 * 
	 * @return
	 */
	public static List<Novel> getNovelsLoaded() {
		return getNovelsByColumnName("loaded");
	}

	/**
	 * 获取等待下载的小说
	 * 
	 * @return
	 */
	public static List<Novel> getNovelsWaitLoad() {
		return getNovelsByColumnName("waitLoad");
	}

	/**
	 * 获取某列属性为true的小说的个数
	 * 
	 * @param columnName
	 *            列名
	 * @return 总个数
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
	 * 获取在书架上的小说的个数
	 * 
	 * @return
	 */
	public static int getNovelsInShelfCount() {

		return getNovelsCountByColumnName("inShelf");
	}

	/**
	 * 获取正在下载的小说的个数
	 * 
	 * @return
	 */
	public static int getNovelsLoadingCount() {

		return getNovelsCountByColumnName("loading");
	}

	/**
	 * 获取正在下载的小说的个数
	 * 
	 * @return
	 */
	public static int getNovelsLoadedCount() {

		return getNovelsCountByColumnName("loaded");
	}

	/**
	 * 获取等待下载的小说的个数
	 * 
	 * @return
	 */
	public static int getNovelsWaitLoadCount() {

		return getNovelsCountByColumnName("waitLoad");
	}

	/**
	 * 判断小说是否存在数据库中，根据书名判断，书名相同则认为存在
	 * 
	 * @param baseNovelInfo
	 *            小说的基类
	 * @return 返回1则说明存在，0则说明不存在
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
	 * 判断小说是否正在下载
	 * 
	 * @param novelId
	 *            小说的_id
	 * @return 在下载返回1，不在下载返回0；
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
	 * 判断小说是否正在等待下载
	 * 
	 * @param novelId
	 *            小说的_id
	 * @return 在下载返回1，不在下载返回0；
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
	 * 判断小说是否已经下载
	 * 
	 * @param novelId
	 *            小说的_id
	 * @return 在下载返回1，不在下载返回0；
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
	 * 判断同名小说是否正在下载
	 * 
	 * @param novelName
	 *            小说的name
	 * @return 在下载返回1，不在下载返回0；
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
	 * 判断同名小说是否正在等待下载
	 * 
	 * @param novelName
	 *            小说的name
	 * @return 在下载返回1，不在下载返回0；
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
	 * 判断同名小说是否已经下载
	 * 
	 * @param novelName
	 *            小说的name
	 * @return 在下载返回1，不在下载返回0；
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
	 * 判断同名小说是否已经在线
	 * 
	 * @param novelName
	 *            小说的name
	 * @return 在下载返回1，不在下载返回0；
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
	 * 判断同名小说是否已经在线
	 * 
	 * @param novelId
	 *            小说的id
	 * @return 在下载返回1，不在下载返回0；
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
	 * 获取小说上次读到一页的开始位置
	 * 
	 * @param novelId
	 *            小说的id
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
	 * 获取小说上次读到一页的结束位置
	 * 
	 * @param novelId
	 *            小说的id
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
	 * 获取小说的下载文件的长度
	 * 
	 * @param novelId
	 *            小说的id
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
