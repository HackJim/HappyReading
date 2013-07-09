package com.happyReading.domain;


import java.util.LinkedList;
import java.util.List;

import com.happyReading.dao.NovelDao;
import com.happyReading.dao.ShelfDao;

public class Shelf {
	private static Shelf shelf = null;
	/**
	 * ����ϴ�ŵ�С˵����ţ����ռ�����ܵ��Ⱥ�˳������
	 */
	public static List<Integer> novelIds = new LinkedList<Integer>();

	private Shelf() {
		// Exists only to defeat instantiation.
	}

    public static Shelf getInstance() {
       if (shelf == null) {
    	   shelf = new Shelf();
       }
       //��ȡ��������пγ̵���Ϣ
       novelIds = ShelfDao.getNovelIdsInShelf();
       return shelf;
    }
    /**
     * ������ϼ���һ��С˵
     * @param novelId С˵Id
     * @return	���С˵�Ѿ�������������򷵻�Ϊ0������ɹ�����Ϊ1
     */
    public static int addNovel(int novelId){
    	if(novelIds.contains(novelId)){
    		//�Ѿ��������
    		return 0;
    	}else{
    		ShelfDao.insertShelfNovel(novelId);
    		novelIds.add(novelId);
    		return 1;
    	}
    }
    /**
     * ��������Ƴ�һ��С˵
     * @param novelId С˵�����
     * @return ���С˵������������з���Ϊ0���ɹ�ɾ���򷵻�1
     */
    public static int deleteNovel(int novelId){
    	if(novelIds.contains(novelId)){
    		ShelfDao.deleteShelfNovelByNovelId(novelId);
    		int rowNum = novelIds.indexOf(novelIds);
    		novelIds.remove(rowNum);
    		return 1;
    	}else{
    		//���������
    		return 0;
    	}
    }
    /**
     * ����ɾ��һ��С˵
     * @param novelId
     * @return ɾ���ɹ�����1�����򷵻�0
     */
    public static int deleteNovelTotally(int novelId){
    	if(novelIds.contains(novelId)){
    		NovelDao.deleteNovelByNovelId(novelId);
    		int rowNum = novelIds.indexOf(novelIds);
    		novelIds.remove(rowNum);
    		return 1;
    	}else{
    		//���������
    		return 0;
    	}
    }
    /**
     * ��һ��С˵����Ϊ����Ķ�
     * @param novelId	С˵��Id
     * @return	������óɹ�����1�����򷵻�Ϊ0
     */
    public static int setNovelLastRead(int novelId){
    	if(novelIds.contains(novelId)){
    		ShelfDao.setReadOrderNum(novelId);
    		
    		int rowNum = novelIds.indexOf(novelIds);
    		novelIds.remove(rowNum);
    		novelIds.add(0, novelId);
    		return 1;
    	}else{
    		//���������
    		return 0;
    	}
    }
    /**
     * ��ȡ����Ͽγ̵���Ŀ
     * @return
     */
    public static int novelCount(){
    	return ShelfDao.getShelfNovelCount();
    }
}
