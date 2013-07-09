package com.happyReading.test;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import net.htmlparser.jericho.Source;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import android.net.http.AndroidHttpClient;
import android.os.Environment;
import android.test.AndroidTestCase;

import com.happyReading.bean.DetailedNovelInfo;
import com.happyReading.bean.NovelInfo;
import com.happyReading.bean.SimpleNovelInfo;
import com.happyReading.download.NovelDownload;
import com.happyReading.utils.NovelInfoUtils;

public class Test extends AndroidTestCase {
	public void test1() {
		try {
			NovelInfo novelInfo = NovelInfoUtils.getNovelInfo("莽荒纪");
			DetailedNovelInfo detailedNovelInfo = NovelInfoUtils.getDetailedNovelInfo(novelInfo);
			final NovelDownload novelDownload = new NovelDownload(detailedNovelInfo);
			new Thread(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					super.run();
					novelDownload.download();
				}
				
			}.run();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("诛仙错误");
			e.printStackTrace();
		}
	}

	public void test3() {
		List<SimpleNovelInfo> infos = null;
		try {
			infos = NovelInfoUtils.getMoreNovelInfo(getContext(),"诛仙", 1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (SimpleNovelInfo info : infos) {
			System.out.println(info.getNovelTitle());
			System.out.println(info.getNovelAuther());
			System.out.println(info.getNovelChapterUrl());
			System.out.println(info.getNovelLastChapterName());
			System.out.println(info.getNovelComeFrom());
			System.out.println(info.getOrderNum());
		}
	}

	public void test4() throws Exception {
		List<SimpleNovelInfo> infos = NovelInfoUtils.getMoreNovelInfo(getContext(),"诛仙", 1);
		SimpleNovelInfo info = infos.get(4);
		DetailedNovelInfo detailedNovelInfo = NovelInfoUtils
				.getDetailedNovelInfo(info);
		System.out.println(detailedNovelInfo.getNovelTitle());
		System.out.println(detailedNovelInfo.getNovelAuther());
		System.out.println(detailedNovelInfo.getNovelLastChapterName());
		System.out.println(detailedNovelInfo.getNovelChapterUrl());
		System.out.println(detailedNovelInfo.getNovelSummary());
		System.out.println(detailedNovelInfo.getNovelLastChapterUrl());
	}

	public void test5() throws Exception {
		URL url = new URL("http://m.baidu.com/ssid=0/pu=sz%401320_1001%2Cusm%402%2Cta%40utouch_2_2.3_3_/from=844b/bd_page_type=1/s?prest=111001&ref=www_iphone&wpo=base&word=%E8%AF%9B%E4%BB%99&st=11n041&tn=xsa&query_type=query_type_bookname&query_source=0&waplogo=");
		AndroidHttpClient client = AndroidHttpClient
				.newInstance("user_agent__my_mobile_browser");
		HttpGet httpGet = new HttpGet(url.toString());
		HttpResponse response = client.execute(httpGet);
		InputStream is = response.getEntity().getContent();
		Source source = new Source(is);
		/**
		 * 把获得的网页源文件打印出来
		 */
		String code = source.toString();
		File file=new File(Environment.getExternalStorageDirectory(),"code.html");
		FileWriter writer = new FileWriter(file,false);
		writer.write(code);
		writer.flush();
		writer.close();
		
		/**
		 * 
		 */
	}

}
