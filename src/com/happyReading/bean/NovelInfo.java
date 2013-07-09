package com.happyReading.bean;

/**
 * 第一次检索时得出的Novel信息
 * 
 * @author pc
 * 
 */
public class NovelInfo extends BaseNovelInfo {
	private String novelLastChapterUrl;
	private String novelLastChapterName;
	private String novelAbstract;
	private String novelMoreUrl;

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

	public String getNovelAbstract() {
		return novelAbstract;
	}

	public void setNovelAbstract(String novelAbstract) {
		this.novelAbstract = novelAbstract;
	}

	public String getNovelMoreUrl() {
		return novelMoreUrl;
	}

	public void setNovelMoreUrl(String novelMoreUrl) {
		this.novelMoreUrl = novelMoreUrl;
	}

}
