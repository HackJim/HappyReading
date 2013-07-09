package com.happyReading.bean;

public class DetailedNovelNoListInfo extends BaseNovelInfo {
	private String novelAuther;
	private String novelSummary;
	private String novelLastChapterUrl;
	private String novelLastChapterName;

	public String getNovelAuther() {
		return novelAuther;
	}

	public void setNovelAuther(String novelAuther) {
		this.novelAuther = novelAuther;
	}

	public String getNovelSummary() {
		return novelSummary;
	}

	public void setNovelSummary(String novelSummary) {
		this.novelSummary = novelSummary;
	}

	public String getNovelLastChapterUrl() {
		return novelLastChapterUrl;
	}

	public void setNovelLastChapterUrl(String novelLastChapterUrl) {
		this.novelLastChapterUrl = novelLastChapterUrl;
	}

	public String getNovelLastChapterName() {
		return novelLastChapterName;
	}

	public void setNovelLastChapterName(String novelLastChapterName) {
		this.novelLastChapterName = novelLastChapterName;
	}
}
