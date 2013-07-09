package com.happyReading.bean;

public class BaseNovelInfo {
	private String novelChapterUrl;
	private String novelAuther;
	public String getNovelAuther() {
		return novelAuther;
	}
	public void setNovelAuther(String novelAuther) {
		this.novelAuther = novelAuther;
	}
	private String novelTitle;
	public String getNovelChapterUrl() {
		return novelChapterUrl;
	}
	public void setNovelChapterUrl(String novelChapterUrl) {
		this.novelChapterUrl = novelChapterUrl;
	}
	public String getNovelTitle() {
		return novelTitle;
	}
	public void setNovelTitle(String novelTitle) {
		this.novelTitle = novelTitle;
	}
	
}
