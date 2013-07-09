package com.happyReading.bean;

import java.util.List;

public class DetailedNovelInfo extends DetailedNovelNoListInfo{
	private List<ChapterInfo> infos;
	public List<ChapterInfo> getInfos() {
		return infos;
	}
	public void setInfos(List<ChapterInfo> infos) {
		this.infos = infos;
	}
}
