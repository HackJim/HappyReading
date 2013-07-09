package com.happyReading.bean;

public class ChapterInfo {
	private int chapterIndex;
	private String chapterName;
	/**
	 * 章节从1开始
	 */
	private String chapterUrl;
	public int getChapterIndex() {
		return chapterIndex;
	}
	public void setChapterIndex(int chapterIndex) {
		this.chapterIndex = chapterIndex;
	}
	public String getChapterName() {
		return chapterName;
	}
	public void setChapterName(String chapterName) {
		this.chapterName = chapterName;
	}
	public String getChapterUrl() {
		return chapterUrl;
	}
	public void setChapterUrl(String chapterUrl) {
		this.chapterUrl = chapterUrl;
	}
	@Override
	public String toString() {
		return "ChapterInfo [chapterIndex=" + chapterIndex + ", chapterName="
				+ chapterName + ", chapterUrl=" + chapterUrl + "]";
	}
	
}
