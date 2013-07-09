package com.happyReading.domain;


import java.util.LinkedList;
import java.util.List;

import com.happyReading.dao.NovelDao;
import com.happyReading.dao.ShelfDao;

public class Shelf {
	private static Shelf shelf = null;
	/**
	 * 书架上存放的小说的序号，按照加入书架的先后顺序排列
	 */
	public static List<Integer> novelIds = new LinkedList<Integer>();

	private Shelf() {
		// Exists only to defeat instantiation.
	}

    public static Shelf getInstance() {
       if (shelf == null) {
    	   shelf = new Shelf();
       }
       //获取书架上所有课程的信息
       novelIds = ShelfDao.getNovelIdsInShelf();
       return shelf;
    }
    /**
     * 在书架上加入一本小说
     * @param novelId 小说Id
     * @return	如果小说已经存在在书架中则返回为0，加入成功返回为1
     */
    public static int addNovel(int novelId){
    	if(novelIds.contains(novelId)){
    		//已经加入书架
    		return 0;
    	}else{
    		ShelfDao.insertShelfNovel(novelId);
    		novelIds.add(novelId);
    		return 1;
    	}
    }
    /**
     * 在书架上移除一本小说
     * @param novelId 小说的序号
     * @return 如果小说本来不在书架中返回为0，成功删除则返回1
     */
    public static int deleteNovel(int novelId){
    	if(novelIds.contains(novelId)){
    		ShelfDao.deleteShelfNovelByNovelId(novelId);
    		int rowNum = novelIds.indexOf(novelIds);
    		novelIds.remove(rowNum);
    		return 1;
    	}else{
    		//不在书架上
    		return 0;
    	}
    }
    /**
     * 彻底删除一本小说
     * @param novelId
     * @return 删除成功返回1，否则返回0
     */
    public static int deleteNovelTotally(int novelId){
    	if(novelIds.contains(novelId)){
    		NovelDao.deleteNovelByNovelId(novelId);
    		int rowNum = novelIds.indexOf(novelIds);
    		novelIds.remove(rowNum);
    		return 1;
    	}else{
    		//不在书架上
    		return 0;
    	}
    }
    /**
     * 将一本小说设置为最后阅读
     * @param novelId	小说的Id
     * @return	如果设置成功返回1，否则返回为0
     */
    public static int setNovelLastRead(int novelId){
    	if(novelIds.contains(novelId)){
    		ShelfDao.setReadOrderNum(novelId);
    		
    		int rowNum = novelIds.indexOf(novelIds);
    		novelIds.remove(rowNum);
    		novelIds.add(0, novelId);
    		return 1;
    	}else{
    		//不在书架上
    		return 0;
    	}
    }
    /**
     * 获取书架上课程的数目
     * @return
     */
    public static int novelCount(){
    	return ShelfDao.getShelfNovelCount();
    }
}
