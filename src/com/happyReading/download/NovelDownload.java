package com.happyReading.download;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.happyReading.bean.ChapterInfo;
import com.happyReading.bean.DetailedNovelInfo;
import com.happyReading.bean.SearchNetIds;
import com.happyReading.dao.NovelDao;
import com.happyReading.utils.NovelInfoUtils;

public class NovelDownload {
	public static int taskNum = 0;
	public static final int TASK_NUM_MAX = 5;
	private DetailedNovelInfo detailedNovelInfo;
	private String dirPath;
	private List<ChapterInfo> infos;
	private SearchNetIds searchNetIds;
	private Handler handler;
	private int totalChapterCount;
	private int loadedChapterCount = 0;
	private boolean pause;
	private int startChapterNum;
	private String txtName;

	public NovelDownload(DetailedNovelInfo detailedNovelInfo) {
		super();
		this.detailedNovelInfo = detailedNovelInfo;
		this.dirPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/happyReading";

		detailedNovelInfo.getNovelChapterUrl();
		handler = null;
		infos = detailedNovelInfo.getInfos();
		totalChapterCount = infos.size();
		pause = false;
		startChapterNum = 0;
	}

	public void download() {

		searchNetIds = NovelInfoUtils.getSearchNetIds(detailedNovelInfo);
		if (searchNetIds == null) {
			Message msg5 = new Message();
			msg5.what = 5;
			handler.sendMessage(msg5);
			return;
		}

		synchronized (NovelDownload.this) { // 多个下载线程之间同步
			NovelDownload.taskNum++;
		}
		File dirFile = new File(dirPath);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		File file;
		if (startChapterNum == 0) {
			String txtName = detailedNovelInfo.getNovelTitle() + "-"
					+ detailedNovelInfo.getNovelAuther()
					+ System.currentTimeMillis() + ".txt";
			file = new File(dirFile, txtName);
			int novelId = NovelDao.getNovelId(detailedNovelInfo);
			NovelDao.setNovelTxtName(novelId, txtName);
		} else {
			file = new File(dirFile, txtName);
		}

		FileWriter writer = null;
		loadedChapterCount = startChapterNum;
		for (int i = startChapterNum; i < infos.size(); i++) {
			if (pause == true) {
				break;
			}
			ChapterInfo info = infos.get(i);
			String chapterContent = null;

			chapterContent = NovelInfoUtils.getChapterContent(searchNetIds,
					info);

			if (chapterContent == null) {
				Message msg4 = new Message();
				msg4.getData().putInt("totalChapterCount", totalChapterCount);
				msg4.getData().putInt("loadedChapterCount", loadedChapterCount);
				msg4.what = 4;
				handler.sendMessage(msg4);
				// 当前章节的内容为空，该如何处理，一种情况是网络链接问题未获得章节信息，还有一种是
				// 所链接到的章节内容真的为空
				break;
			}

			try {
				writer = new FileWriter(file, true);
				writer.write(chapterContent);
				writer.flush();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 设置消息机制
			if (handler != null) {
				Message msg1 = new Message();
				msg1.getData().putInt("totalChapterCount", totalChapterCount);
				msg1.what = 1;
				handler.sendMessage(msg1);
				loadedChapterCount++;
				Message msg2 = new Message();
				msg2.getData().putInt("loadedChapterCount", loadedChapterCount);
				msg2.what = 2;
				handler.sendMessage(msg2);
			}
		}
		try {
			if (writer != null) {
				writer.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		synchronized (NovelDownload.this) { // 多个下载线程之间同步
			NovelDownload.taskNum--;
		}
		// 设置消息机制，完成下载
		if (handler != null && (loadedChapterCount == totalChapterCount)) {
			Message msg3 = new Message();
			msg3.what = 3;
			handler.sendMessage(msg3);
		}
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public int getStartChapterNum() {
		return startChapterNum;
	}

	public void setStartChapterNum(int startChapterNum) {
		this.startChapterNum = startChapterNum;
	}

	public boolean isPause() {
		return pause;
	}

	public void setPause(boolean pause) {
		this.pause = pause;
	}

	public void setTxtName(String txtName) {
		this.txtName = txtName;
	}
}
