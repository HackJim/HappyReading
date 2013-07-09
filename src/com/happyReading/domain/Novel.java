package com.happyReading.domain;

public class Novel {
	private int _id;
	private String name;
	private String auther;
	private String url;
	private String summary;
	private int lastChapterIndex;
	private String lastChapterName;
	private String lastChapterUrl;
	private int inShelf;
	private int loading;
	private int waitLoad;
	private int loaded;
	private String loadProgress;
	private int online;
	private int updateNotification;
	private String txtName;
	private int loadedChapterNum;
	private int readBegin;
	private int readEnd;
	private int txtLen;
	private int readedChapterIndex;

	public int getReadedChapterIndex() {
		return readedChapterIndex;
	}

	public void setReadedChapterIndex(int readedChapterIndex) {
		this.readedChapterIndex = readedChapterIndex;
	}

	public int getReadBegin() {
		return readBegin;
	}

	public void setReadBegin(int readBegin) {
		this.readBegin = readBegin;
	}

	public int getReadEnd() {
		return readEnd;
	}

	public void setReadEnd(int readEnd) {
		this.readEnd = readEnd;
	}

	public int getTxtLen() {
		return txtLen;
	}

	public void setTxtLen(int txtLen) {
		this.txtLen = txtLen;
	}

	public int getLoadedChapterNum() {
		return loadedChapterNum;
	}

	public void setLoadedChapterNum(int loadedChapterNum) {
		this.loadedChapterNum = loadedChapterNum;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuther() {
		return auther;
	}

	public void setAuther(String auther) {
		this.auther = auther;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public int getLastChapterIndex() {
		return lastChapterIndex;
	}

	public void setLastChapterIndex(int lastChapterIndex) {
		this.lastChapterIndex = lastChapterIndex;
	}

	public String getLastChapterName() {
		return lastChapterName;
	}

	public void setLastChapterName(String lastChapterName) {
		this.lastChapterName = lastChapterName;
	}

	public String getLastChapterUrl() {
		return lastChapterUrl;
	}

	public void setLastChapterUrl(String lastChapterUrl) {
		this.lastChapterUrl = lastChapterUrl;
	}

	public int getInShelf() {
		return inShelf;
	}

	public void setInShelf(int inShelf) {
		this.inShelf = inShelf;
	}

	public int getLoading() {
		return loading;
	}

	public void setLoading(int loading) {
		this.loading = loading;
	}

	public int getWaitLoad() {
		return waitLoad;
	}

	public void setWaitLoad(int waitLoad) {
		this.waitLoad = waitLoad;
	}

	public int getLoaded() {
		return loaded;
	}

	public void setLoaded(int loaded) {
		this.loaded = loaded;
	}

	public String getLoadProgress() {
		return loadProgress;
	}

	public void setLoadProgress(String loadProgress) {
		this.loadProgress = loadProgress;
	}

	public int getOnline() {
		return online;
	}

	public void setOnline(int online) {
		this.online = online;
	}

	public int getUpdateNotification() {
		return updateNotification;
	}

	public void setUpdateNotification(int updateNotification) {
		this.updateNotification = updateNotification;
	}

	public String getTxtName() {
		return txtName;
	}

	public void setTxtName(String txtName) {
		this.txtName = txtName;
	}

}
