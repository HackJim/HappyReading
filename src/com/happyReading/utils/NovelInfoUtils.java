package com.happyReading.utils;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.text.TextUtils;

import com.happyReading.bean.BaseNovelInfo;
import com.happyReading.bean.ChapterInfo;
import com.happyReading.bean.DetailedNovelInfo;
import com.happyReading.bean.DetailedNovelNoListInfo;
import com.happyReading.bean.NovelInfo;
import com.happyReading.bean.SearchNetIds;
import com.happyReading.bean.SimpleNovelInfo;

/**
 * 获取novelInfo信息的帮助类
 * 
 * @author pc
 * 
 */
public class NovelInfoUtils {
	/**
	 * 根据输入的小说名字，获取小说相关的信息
	 * 
	 * @param novelInfoName
	 *            小说名字
	 * @return 小说相关信息
	 * @throws Exception
	 */
	public static NovelInfo getNovelInfo(String novelName) {
		AndroidHttpClient client = null;
		try {

			String navalName = novelName.trim();
			if (navalName == null || navalName.equals("")) {
				return null;
			}

			URL url = new URL(
					"http://m.baidu.com/from=844b/bd_page_type=1/ssid=0/uid=6E651108EB5EB31F3D9AE39DC9385691/pu=sz%401320_1003%2Cusm%402%2Cta%40utouch_2_4.0_1_/s?ref=www_utouch&lid=10302360602742180088&word="
							+ novelName + "&rn=10&mobile_se=1&wpo=base");
			client = AndroidHttpClient
					.newInstance("user_agent__my_mobile_browser");
			HttpGet httpGet = new HttpGet(url.toString());
			HttpResponse response = client.execute(httpGet);
			// DefaultHttpClient client = new DefaultHttpClient();
			// HttpGet httpGet = new HttpGet(url.toString());
			// HttpResponse response = client.execute(httpGet);
			InputStream is = response.getEntity().getContent();
			Source source = new Source(is);
			List<Element> lis = source.getAllElements("div");
			NovelInfo novelInfo = null;
			String novelInfoChapterUrl = new String("");
			String novelInfoTitle = new String("");
			String novelInfoAuther = new String("");
			String novelInfoLastChapterUrl = new String("");
			String novelInfoLastChapterName = new String("");
			String novelInfoAbstract = new String("");
			String novelInfoMoreUrl = new String("");
			for (Element li : lis) {
				if ("ala_novel_title".equals(li.getAttributeValue("class"))) {
					novelInfo = new NovelInfo();
					List<Element> childrenElements = li.getChildElements();
					if ("a".equals(childrenElements.get(0).getName())) {
						novelInfoChapterUrl = childrenElements.get(0)
								.getAttributeValue("href");
						novelInfo.setNovelChapterUrl(novelInfoChapterUrl);
						Element nextChildrenElement = childrenElements.get(0)
								.getChildElements().get(0);
						Element nextNextChildrenElement = nextChildrenElement
								.getChildElements().get(0);
						novelInfoTitle = nextNextChildrenElement
								.getTextExtractor().toString();
						novelInfo.setNovelTitle(novelInfoTitle);
					}
				}
				if ("ala_novel_author".equals(li.getAttributeValue("class"))) {
					Element nextChildrenElement = li.getChildElements().get(1);
					novelInfoAuther = nextChildrenElement.getTextExtractor()
							.toString();
					novelInfo.setNovelAuther(novelInfoAuther);
				}
				if ("ala_novel_lastchapter".equals(li
						.getAttributeValue("class"))) {
					Element nextChildrenElement = li.getChildElements().get(1);
					novelInfoLastChapterUrl = nextChildrenElement
							.getAttributeValue("href");
					novelInfo.setNovelLastChapterUrl(novelInfoLastChapterUrl);
					novelInfoLastChapterName = nextChildrenElement
							.getTextExtractor().toString();
					novelInfo.setNovelLastChapterName(novelInfoLastChapterName);

				}
				if ("ala_novel_abstract".equals(li.getAttributeValue("class"))) {
					novelInfoAbstract = li.getTextExtractor().toString();
					novelInfo.setNovelAbstract(novelInfoAbstract);
				}
				if ("ala_novel_writer_readmore".equals(li
						.getAttributeValue("class"))) {
					Element nextChildrenElement = li.getChildElements().get(0);
					novelInfoMoreUrl = nextChildrenElement
							.getAttributeValue("href");
					novelInfo.setNovelMoreUrl(novelInfoMoreUrl);
				}
			}
			client.close();
			return novelInfo;
		} catch (Exception e) {
			if (client != null) {
				client.close();
			}
			return null;
		}
	}

	/**
	 * 输入小说名字，和需要检索的页码，得出小说相关的简单信息
	 * 
	 * @param novelName
	 *            小说名字
	 * @param pageNum
	 *            页码
	 * @return 检索到的相关小说的简单信息
	 * @throws Exception
	 */
	public static List<SimpleNovelInfo> getMoreNovelInfo(Context context,
			String novelName, int pageNum) {
		AndroidHttpClient client = null;
		try {

			String navalName = novelName.trim();
			if (navalName == null || navalName.equals("")) {
				return null;
			}
			if (pageNum > 10) {
				return null;
			}
			int startIndex = (pageNum - 1) * 10;
			URL url = new URL(
					"http://m.baidu.com/ssid=0/pu=sz%401320_1001%2Cusm%402%2Cta%40utouch_2_2.3_3_/from=844b/bd_page_type=1/s?prest=111001&ref=www_iphone&wpo=base&word="
							+ novelName
							+ "&pn="
							+ startIndex
							+ "&rn="
							+ 10
							+ "&prest=11n041&st=11n081&tn=xsa&query_type=query_type_bookname&query_source=0");

			client = AndroidHttpClient
					.newInstance("user_agent__my_mobile_browser");
			HttpGet httpGet = new HttpGet(url.toString());
			HttpResponse response = client.execute(httpGet);
			InputStream is = response.getEntity().getContent();
			Source source = new Source(is);
			List<Element> lis = source.getAllElements("a");
			List<Element> divs = source.getAllElements("div");
			String[] novelComeFroms = new String[10];
			int novelComeFromsIndex = 0;
			for (Element li : divs) {
				if ("site".equals(li.getAttributeValue("class"))) {
					String comeFromUrl = li.getTextExtractor().toString();
					String[] comeFromUrlInArray = comeFromUrl.split(" ");
					String novelComeFrom = comeFromUrlInArray[0];
					novelComeFroms[novelComeFromsIndex] = novelComeFrom;
					novelComeFromsIndex++;
				}
			}
			int num = 0;
			List<SimpleNovelInfo> simpleNovelInfos = null;
			for (Element li : lis) {
				if ("novel_a".equals(li.getAttributeValue("class"))) {
					if (simpleNovelInfos == null) {
						simpleNovelInfos = new ArrayList<SimpleNovelInfo>();
					}
					SimpleNovelInfo info = new SimpleNovelInfo();
					String novelChapterUrl = "http://m.baidu.com/ssid=0/from=844b/bd_page_type=1/uid=30BC406402C4B039DED8535FDF713F58/pu=sz%401320_1001%2Cusm%400%2Cta%40utouch_2_2.3_3_/"
							+ li.getAttributeValue("href");
					info.setNovelChapterUrl(novelChapterUrl);
					Element firstChildElement = li.getChildElements().get(0);
					List<Element> childElements = firstChildElement
							.getChildElements();
					int lastChildIndex = childElements.size() - 1;
					String titleAndAutherInfo = firstChildElement
							.getChildElements().get(lastChildIndex)
							.getTextExtractor().toString();
					String[] titleAndAutherInfoInArray = titleAndAutherInfo
							.split("_");
					String title = titleAndAutherInfoInArray[0];
					String auther;
					if (titleAndAutherInfoInArray.length < 2) {
						auther = "未知";
						info.setNovelAuther(auther);
					} else {

						auther = titleAndAutherInfoInArray[1];
						info.setNovelAuther(new String(auther
								.getBytes("ISO-8859-1"), "UTF-8"));
					}
					info.setNovelTitle(new String(title.getBytes("ISO-8859-1"),
							"UTF-8"));
					Element secondChildElement = li.getChildElements().get(1);
					String novelLastChapterName = secondChildElement
							.getTextExtractor().toString();
					info.setNovelLastChapterName(new String(
							novelLastChapterName.getBytes("ISO-8859-1"),
							"UTF-8"));
					new String(novelComeFroms[num].getBytes("ISO-8859-1"),
							"UTF-8");
					info.setNovelComeFrom(new String(novelComeFroms[num]
							.getBytes("ISO-8859-1"), "UTF-8"));
					simpleNovelInfos.add(info);
					num++;
					int orderNum = startIndex + num;
					info.setOrderNum(orderNum);
					if (num == 10) {
						break;
					}
				}
			}
			client.close();
			return simpleNovelInfos;
		} catch (Exception e) {
			e.printStackTrace();
			if (client != null) {
				client.close();
			}
			return null;
		}
	}

	/**
	 * 根据传入的BasenovelInfo对象获取小说的详细信息（名称，作者，主界面链接，详细简介，最新章节名称，最新章节链接，章节详细信息）
	 * 
	 * @param basenovelInfo
	 *            传入的BasenovelInfo对象
	 * @return 记录小说详细信息的类
	 * @throws Exception
	 */
	public static DetailedNovelInfo getDetailedNovelInfo(
			BaseNovelInfo basenovelInfo) {
		AndroidHttpClient client = null;
		try {
			String novelChapterUrl = basenovelInfo.getNovelChapterUrl();
			DetailedNovelInfo detailedNovelInfo = new DetailedNovelInfo();
			detailedNovelInfo.setNovelChapterUrl(novelChapterUrl);
			String novelChapterJSONUrl = NovelInfoUtils
					.getChapterJsonUrl(basenovelInfo);
			URL url = new URL(novelChapterJSONUrl);
			client = AndroidHttpClient
					.newInstance("user_agent__my_mobile_browser");
			HttpGet httpGet = new HttpGet(url.toString());
			HttpResponse response = client.execute(httpGet);
			// DefaultHttpClient client = new DefaultHttpClient();
			// HttpGet httpGet = new HttpGet(url.toString());
			// HttpResponse response = client.execute(httpGet);
			InputStream is = response.getEntity().getContent();
			Source source = new Source(is);
			String result1 = source.toString();
			// 转码
			String result = new String(result1.getBytes("ISO-8859-1"), "UTF-8");
			JSONObject jsonObj = new JSONObject(result);
			JSONObject dataObj = jsonObj.getJSONObject("data");
			String title = dataObj.getString("title");
			if ("".equals(title)) {
				String parentName = basenovelInfo.getNovelTitle();
				if ("".equals(parentName)) {

					title = "未知";
				} else {
					title = parentName;
				}
			}
			detailedNovelInfo.setNovelTitle(title);
			String author = dataObj.getString("author");
			if (TextUtils.isEmpty(author) || "false".equals(author)) {
				String parentAuther = basenovelInfo.getNovelAuther();
				if ("".equals(parentAuther)) {

					author = "未知";
				} else {
					author = parentAuther;
				}
			}
			detailedNovelInfo.setNovelAuther(author);
			String summary;
			try {
				summary = dataObj.getString("summary");
			} catch (Exception e) {
				summary = "暂时未有简介";
				e.printStackTrace();
			}
			detailedNovelInfo.setNovelSummary(summary);
			JSONObject latestChapterObj = dataObj
					.getJSONObject("latestChapter");
			String latestChapterHref = latestChapterObj.getString("href");
			detailedNovelInfo.setNovelLastChapterUrl(latestChapterHref);
			String latestChapterText = latestChapterObj.getString("text");
			detailedNovelInfo.setNovelLastChapterName(latestChapterText);
			JSONArray groupArray = dataObj.getJSONArray("group");
			List<ChapterInfo> infos = new ArrayList<ChapterInfo>();
			for (int i = 0; i < groupArray.length(); i++) {
				ChapterInfo info = new ChapterInfo();
				JSONObject chapterInfo = groupArray.getJSONObject(i);
				String chapterText = chapterInfo.getString("text");
				String chapterHref = chapterInfo.getString("href");
				if(chapterHref==null){
					continue;
				}
				info.setChapterIndex(i + 1);
				info.setChapterName(chapterText);
				info.setChapterUrl(chapterHref);
				infos.add(info);
			}
			detailedNovelInfo.setInfos(infos);
			client.close();
			return detailedNovelInfo;
		} catch (Exception e) {
			if (client != null) {
				client.close();
			}
			return null;
		}
	}

	/**
	 * 根据传入的小说名称和小说主界面Url获取小说的详细信息（名称，作者，主界面链接，详细简介，最新章节名称，最新章节链接，章节详细信息）
	 * 
	 * @param novelName
	 *            小说名称
	 * @param novelChapterUrl
	 *            小说主界面Url
	 * @return 包含小说详细信息的类
	 * @throws Exception
	 */
	public static DetailedNovelInfo getDetailedNovelInfo(String novelName,
			String novelChapterUrl) {
		AndroidHttpClient client = null;
		try {
			DetailedNovelInfo detailedNovelInfo = new DetailedNovelInfo();
			detailedNovelInfo.setNovelChapterUrl(novelChapterUrl);
			String novelChapterJSONUrl = NovelInfoUtils.getChapterJsonUrl(
					novelName, novelChapterUrl);
			URL url = new URL(novelChapterJSONUrl);
			client = AndroidHttpClient
					.newInstance("user_agent__my_mobile_browser");
			HttpGet httpGet = new HttpGet(url.toString());
			HttpResponse response = client.execute(httpGet);
			// DefaultHttpClient client = new DefaultHttpClient();
			// HttpGet httpGet = new HttpGet(url.toString());
			// HttpResponse response = client.execute(httpGet);
			InputStream is = response.getEntity().getContent();
			Source source = new Source(is);
			String result1 = source.toString();
			String result = new String(result1.getBytes("ISO-8859-1"), "UTF-8");
			JSONObject jsonObj = new JSONObject(result);
			JSONObject dataObj = jsonObj.getJSONObject("data");
			String title = dataObj.getString("title");
			if ("".equals(title)) {
				String parentName = novelName;
				if ("".equals(parentName)) {

					title = "未知";
				} else {
					title = parentName;
				}
			}
			detailedNovelInfo.setNovelTitle(title);
			String author = dataObj.getString("author");
			if (TextUtils.isEmpty(author) || "false".equals(author)) {
				String parentAuther = novelName;
				if ("".equals(parentAuther)) {

					author = "未知";
				} else {
					author = parentAuther;
				}
			}
			detailedNovelInfo.setNovelAuther(author);
			String summary;
			try {
				summary = dataObj.getString("summary");
			} catch (Exception e) {
				summary = "暂时未有简介";
				e.printStackTrace();
			}
			detailedNovelInfo.setNovelSummary(summary);
			JSONObject latestChapterObj = dataObj
					.getJSONObject("latestChapter");
			String latestChapterHref = latestChapterObj.getString("href");
			detailedNovelInfo.setNovelLastChapterUrl(latestChapterHref);
			String latestChapterText = latestChapterObj.getString("text");
			detailedNovelInfo.setNovelLastChapterName(latestChapterText);
			JSONArray groupArray = dataObj.getJSONArray("group");
			List<ChapterInfo> infos = new ArrayList<ChapterInfo>();
			for (int i = 0; i < groupArray.length(); i++) {
				ChapterInfo info = new ChapterInfo();
				JSONObject chapterInfo = groupArray.getJSONObject(i);
				String chapterText = chapterInfo.getString("text");
				String chapterHref = chapterInfo.getString("href");
				if(chapterHref==null){
					continue;
				}
				info.setChapterIndex(i + 1);
				info.setChapterName(chapterText);
				info.setChapterUrl(chapterHref);
				infos.add(info);
			}
			detailedNovelInfo.setInfos(infos);
			client.close();
			return detailedNovelInfo;
		} catch (Exception e) {
			if (client != null) {
				client.close();
			}
			return null;
		}
	}

	/**
	 * 根据传入的小说名称和小说主界面Url获取小说的详细信息（不包含章节详细列表）。
	 * 
	 * @param novelName
	 *            小说名称
	 * @param novelChapterUrl
	 *            主界面Url
	 * @return
	 * @throws Exception
	 */
	public static DetailedNovelNoListInfo getDetailedNovelNolistInfo(
			BaseNovelInfo basenovelInfo) {
		AndroidHttpClient client = null;
		try {
			String novelChapterUrl = basenovelInfo.getNovelChapterUrl();
			String novelName = basenovelInfo.getNovelTitle();
			DetailedNovelNoListInfo detailedNovelNoListInfo = new DetailedNovelNoListInfo();
			detailedNovelNoListInfo.setNovelChapterUrl(novelChapterUrl);
			String novelChapterJSONUrl = NovelInfoUtils.getChapterJsonUrl(
					novelName, novelChapterUrl);
			URL url = new URL(novelChapterJSONUrl);
			client = AndroidHttpClient
					.newInstance("user_agent__my_mobile_browser");
			HttpGet httpGet = new HttpGet(url.toString());
			HttpResponse response = client.execute(httpGet);
			// DefaultHttpClient client = new DefaultHttpClient();
			// HttpGet httpGet = new HttpGet(url.toString());
			// HttpResponse response = client.execute(httpGet);
			InputStream is = response.getEntity().getContent();
			Source source = new Source(is);
			String result1 = source.toString();
			String result = new String(result1.getBytes("ISO-8859-1"), "UTF-8");
			JSONObject jsonObj = new JSONObject(result);
			JSONObject dataObj = jsonObj.getJSONObject("data");
			String title = dataObj.getString("title");
			if ("".equals(title)) {
				String parentName = basenovelInfo.getNovelTitle();
				if ("".equals(parentName)) {

					title = "未知";
				} else {
					title = parentName;
				}
			}
			detailedNovelNoListInfo.setNovelTitle(title);
			String author = dataObj.getString("author");
			if (TextUtils.isEmpty(author) || "false".equals(author)) {
				String parentAuther = basenovelInfo.getNovelAuther();
				if ("".equals(parentAuther)) {

					author = "未知";
				} else {
					author = parentAuther;
				}
			}
			detailedNovelNoListInfo.setNovelAuther(author);
			String summary;
			try {
				summary = dataObj.getString("summary");
			} catch (Exception e) {
				summary = "暂时未有简介";
				e.printStackTrace();
			}
			detailedNovelNoListInfo.setNovelSummary(summary);
			JSONObject latestChapterObj = dataObj
					.getJSONObject("latestChapter");
			String latestChapterHref = latestChapterObj.getString("href");
			detailedNovelNoListInfo.setNovelLastChapterUrl(latestChapterHref);
			String latestChapterText = latestChapterObj.getString("text");
			detailedNovelNoListInfo.setNovelLastChapterName(latestChapterText);
			client.close();
			return detailedNovelNoListInfo;
		} catch (Exception e) {
			if (client != null) {
				client.close();
			}
			return null;
		}
	}

	/**
	 * 根据传入的小说名称和小说主界面Url获取小说的详细信息（不包含章节详细列表）。
	 * 
	 * @param novelName
	 *            小说名称
	 * @param novelChapterUrl
	 *            主界面Url
	 * @return
	 * @throws Exception
	 */
	public static DetailedNovelNoListInfo getDetailedNovelNolistInfo(
			String novelName, String novelChapterUrl) throws Exception {
		AndroidHttpClient client = null;
		try {
			DetailedNovelNoListInfo detailedNovelNoListInfo = new DetailedNovelNoListInfo();
			detailedNovelNoListInfo.setNovelChapterUrl(novelChapterUrl);
			String novelChapterJSONUrl = NovelInfoUtils.getChapterJsonUrl(
					novelName, novelChapterUrl);
			URL url = new URL(novelChapterJSONUrl);
			client = AndroidHttpClient
					.newInstance("user_agent__my_mobile_browser");
			HttpGet httpGet = new HttpGet(url.toString());
			HttpResponse response = client.execute(httpGet);
			// DefaultHttpClient client = new DefaultHttpClient();
			// HttpGet httpGet = new HttpGet(url.toString());
			// HttpResponse response = client.execute(httpGet);
			InputStream is = response.getEntity().getContent();
			Source source = new Source(is);
			String result1 = source.toString();
			String result = new String(result1.getBytes("ISO-8859-1"), "UTF-8");
			JSONObject jsonObj = new JSONObject(result);
			JSONObject dataObj = jsonObj.getJSONObject("data");
			String title = dataObj.getString("title");
			if ("".equals(title)) {
				title = "未知";
			}
			detailedNovelNoListInfo.setNovelTitle(title);
			String author = dataObj.getString("author");
			detailedNovelNoListInfo.setNovelAuther(author);
			String summary;
			try {
				summary = dataObj.getString("summary");
			} catch (Exception e) {
				summary = "暂时未有简介";
				e.printStackTrace();
			}
			detailedNovelNoListInfo.setNovelSummary(summary);
			JSONObject latestChapterObj = dataObj
					.getJSONObject("latestChapter");
			String latestChapterHref = latestChapterObj.getString("href");
			detailedNovelNoListInfo.setNovelLastChapterUrl(latestChapterHref);
			String latestChapterText = latestChapterObj.getString("text");
			detailedNovelNoListInfo.setNovelLastChapterName(latestChapterText);
			client.close();
			return detailedNovelNoListInfo;
		} catch (Exception e) {
			if (client != null) {
				client.close();
			}
			return null;
		}
	}

	/**
	 * 传入小说的基础类得到获取小说章节信息的JSON地址
	 * 
	 * @param basenovelInfo
	 * @return
	 * @throws Exception
	 */
	public static String getChapterJsonUrl(BaseNovelInfo basenovelInfo)
			throws Exception {
		SearchNetIds searchNetIds = NovelInfoUtils
				.getSearchNetIds(basenovelInfo);

		String s1 = "http://m.baidu.com/from=844b/bd_page_type=1/ssid=0/uid=30BC406402C4B039DED8535FDF713F58/pu=sz%401320_2001%2Cusm%400%2Cta%40iphone___3_537/w=0_10_";
		String s2 = "/t=iphone/tc?srd=1&appui=alaxs&ajax=1&alalog=1&gid=";
		String s3 = "baiduid=";
		String s4 = "&ref=novel_iphone&lid=";
		String s5 = "&order=1&tj=xsa_normal_1_0_10&srct=dir&sec=";
		String s6 = "&di=";
		String s7 = "&src=";
		String path = s1 + searchNetIds.getNovelName() + s2
				+ searchNetIds.getGid() + s3 + searchNetIds.getBaiduid() + s4
				+ searchNetIds.getLid() + s5 + searchNetIds.getSec() + s6
				+ searchNetIds.getDi() + s7 + searchNetIds.getSrc();
		return path;
	}

	/**
	 * 传入小说的主界面Url得到获取小说章节信息的JSON地址
	 * 
	 * @param basenovelInfo
	 * @return
	 * @throws Exception
	 */
	public static String getChapterJsonUrl(String novelName,
			String novelChapterUrl) throws Exception {
		SearchNetIds searchNetIds = NovelInfoUtils.getSearchNetIds(novelName,
				novelChapterUrl);

		String s1 = "http://m.baidu.com/from=844b/bd_page_type=1/ssid=0/uid=30BC406402C4B039DED8535FDF713F58/pu=sz%401320_2001%2Cusm%400%2Cta%40iphone___3_537/w=0_10_";
		String s2 = "/t=iphone/tc?srd=1&appui=alaxs&ajax=1&alalog=1&gid=";
		String s3 = "baiduid=";
		String s4 = "&ref=novel_iphone&lid=";
		String s5 = "&order=1&tj=xsa_normal_1_0_10&srct=dir&sec=";
		String s6 = "&di=";
		String s7 = "&src=";
		String path = s1 + searchNetIds.getNovelName() + s2
				+ searchNetIds.getGid() + s3 + searchNetIds.getBaiduid() + s4
				+ searchNetIds.getLid() + s5 + searchNetIds.getSec() + s6
				+ searchNetIds.getDi() + s7 + searchNetIds.getSrc();
		return path;
	}

	/**
	 * 根据小说的基础类获取小说的相关检索信息
	 * 
	 * @param basenovelInfo
	 * @return
	 * @throws Exception
	 */
	public static SearchNetIds getSearchNetIds(BaseNovelInfo basenovelInfo) {
		AndroidHttpClient client = null;
		try {
			// TODO 网站不对，导致这里出现错误，从而不能相应的DetailedNovelInfo
			String novelName = basenovelInfo.getNovelTitle();
			String novelChapterUrl = basenovelInfo.getNovelChapterUrl();
			URL url = new URL(novelChapterUrl);
			client = AndroidHttpClient
					.newInstance("user_agent__my_mobile_browser");
			HttpGet httpGet = new HttpGet(url.toString());
			HttpResponse response = client.execute(httpGet);
			// DefaultHttpClient client = new DefaultHttpClient();
			// HttpGet httpGet = new HttpGet(url.toString());
			// HttpResponse response = client.execute(httpGet);
			InputStream is = response.getEntity().getContent();
			Source source = new Source(is);
			List<Element> allElements = source.getAllElements("script");
			String novelInfo = allElements.get(0).toString();
			String[] infos = novelInfo.split("\"");
			String gid = infos[1];
			String src = infos[3];
			String baiduid = infos[11];
			String lid = infos[19];
			String sec = infos[51];
			String di = infos[53];
			SearchNetIds searchNetIds = new SearchNetIds();
			searchNetIds.setBaiduid(baiduid);
			searchNetIds.setDi(di);
			searchNetIds.setGid(gid);
			searchNetIds.setLid(lid);
			searchNetIds.setSec(sec);
			searchNetIds.setSrc(src);
			searchNetIds.setNovelName(novelName);
			client.close();
			return searchNetIds;
		} catch (Exception e) {
			if (client != null) {
				client.close();
			}
			return null;
		}
	}

	/**
	 * 根据小说的名称和小说的主界面Url检索NetIds信息
	 * 
	 * @param novelName
	 * @param novelChapterUrl
	 * @return
	 * @throws Exception
	 */
	public static SearchNetIds getSearchNetIds(String novelName,
			String novelChapterUrl) throws Exception {
		AndroidHttpClient client = null;
		try {
			URL url = new URL(novelChapterUrl);
			client = AndroidHttpClient
					.newInstance("user_agent__my_mobile_browser");
			HttpGet httpGet = new HttpGet(url.toString());
			HttpResponse response = client.execute(httpGet);
			// DefaultHttpClient client = new DefaultHttpClient();
			// HttpGet httpGet = new HttpGet(url.toString());
			// HttpResponse response = client.execute(httpGet);
			InputStream is = response.getEntity().getContent();
			Source source = new Source(is);
			List<Element> allElements = source.getAllElements("script");
			String novelInfo = allElements.get(0).toString();
			String[] infos = novelInfo.split("\"");
			String gid = infos[1];
			String src = infos[3];
			String baiduid = infos[11];
			String lid = infos[19];
			String sec = infos[51];
			String di = infos[53];
			SearchNetIds searchNetIds = new SearchNetIds();
			searchNetIds.setBaiduid(baiduid);
			searchNetIds.setDi(di);
			searchNetIds.setGid(gid);
			searchNetIds.setLid(lid);
			searchNetIds.setSec(sec);
			searchNetIds.setSrc(src);
			searchNetIds.setNovelName(novelName);
			client.close();
			return searchNetIds;
		} catch (Exception e) {
			if (client != null) {
				client.close();
			}
			return null;
		}
	}

	public static String getChapterContentJsonUrl(SearchNetIds ids,
			String chapterUrl) throws Exception {
		String novelName = ids.getNovelName();
		String gid = ids.getGid();
		String s1 = "http://m.baidu.com/from=844b/bd_page_type=1/ssid=0/uid=30BC406402C4B039DED8535FDF713F58/pu=sz%401320_2001%2Cusm%402%2Cta%40iphone____/w=0_10_";
		String s2 = "/t=iphone/tc?srd=1&appui=alaxs&ajax=1&alalog=1&gid=";
		String s3 = "&pageType=router&src=";
		String path = s1 + novelName + s2 + gid + s3 + chapterUrl;
		return path;
	}

	public static String getChapterContentWebUrl(String novelChapterUrl,
			String chapterUrl) throws Exception {
		String chapterContentWebUrl = novelChapterUrl + "#!/zw/" + chapterUrl;
		return chapterContentWebUrl;
	}

	public static String getChapterContent(SearchNetIds ids, ChapterInfo info) {
		AndroidHttpClient client = null;
		try {
			String chapterUrl = info.getChapterUrl();
			String chapterName = info.getChapterName();
			String chapterContentJsonUrl = NovelInfoUtils
					.getChapterContentJsonUrl(ids, chapterUrl);
			URL url = new URL(chapterContentJsonUrl);
			client = AndroidHttpClient
					.newInstance("user_agent__my_mobile_browser");
			HttpGet httpGet = new HttpGet(url.toString());
			HttpResponse response = client.execute(httpGet);
			// DefaultHttpClient client = new DefaultHttpClient();
			// HttpGet httpGet = new HttpGet(url.toString());
			// HttpResponse response = client.execute(httpGet);
			InputStream is = response.getEntity().getContent();
			Source source = new Source(is);
			String result1 = source.toString();
			String result = new String(result1.getBytes("ISO-8859-1"), "UTF-8");
			JSONObject jsonObj = new JSONObject(result);
			String content;
			try {
				JSONObject dataObj = jsonObj.getJSONObject("data");
				content = dataObj.getString("content");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return chapterName;
			}
			String newLine = "\r\n";
			String content1 = content.replaceAll("<br/>", newLine);
			String content2 = content1.replaceAll("</p>", newLine + "    ");
			char[] deleteNull = new char[4];
			for (int i = 0; i < 4; i++) {
				deleteNull[i] = '\u00A0';
			}
			String deleteNullString = new String(deleteNull);
			String content3 = content2.replaceAll(deleteNullString, "    ");
			String content4 = content3.replaceAll("</a>", "");
			// 去除里面超链接
			String deleteA = "<[^\u4e00-\u9fa5]*>";
			String content5 = content4.replaceAll(deleteA, "");
			// 去除里面网站地址
			String deleteB = "[wW][wW][wW][^\u4e00-\u9fa5]*[mMnN]";
			String content6 = content5.replaceAll(deleteB, "");
			String deleteC = "&quot;";
			String content7 = content6.replace(deleteC, "\"");
			String deleteD = "&apos;";
			String content8 = content7.replace(deleteD, "‘");
			client.close();
			return "    " + chapterName + "\r\n" + content8 + "\r\n";
		} catch (Exception e) {
			if (client != null) {
				client.close();
			}
			e.printStackTrace();
			return null;
		}
	}
}
