package com.happyReading.bean;

/**
 * �������һ�μ�������Ļ�ø��࣬�ó�������Ϣ
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
