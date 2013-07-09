package com.happyReading.bean;

/**
 * 当点击第一次检索结果的获得更多，得出以下信息
 * 
 * @author pc
 * 
 */
public class SimpleNovelInfo extends BaseNovelInfo {
	private int orderNum;

	private String novelLastChapterName;
	private String novelComeFrom;

	public int getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(int orderNum) {
		this.orderNum = orderNum;
	}

	public String getNovelLastChapterName() {
		return novelLastChapterName;
	}

	public void setNovelLastChapterName(String novelLastChapterName) {
		this.novelLastChapterName = novelLastChapterName;
	}

	public String getNovelComeFrom() {
		return novelComeFrom;
	}

	public void setNovelComeFrom(String novelComeFrom) {
		this.novelComeFrom = novelComeFrom;
	}
}
