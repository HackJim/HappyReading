package com.happyReading.engine;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import com.happyReading.bean.UpdateInfo;

/**
 * 获取更新信息
 * 
 * @author pc
 * 
 */
public class UpdateInfoProvider {
	/**
	 * 解析XML文件
	 * 
	 * @param is
	 *            XML的输入流
	 * @return UpdateInfo对象，如果解析失败返回null
	 */
	public static UpdateInfo getUpdateInfo(InputStream is) {
		UpdateInfo updateInfo = new UpdateInfo();
		XmlPullParser parse = Xml.newPullParser();
		try {
			parse.setInput(is, "UTF-8");
			int type = parse.getEventType();
			while (type != XmlPullParser.END_DOCUMENT) {
				if (type == XmlPullParser.START_TAG) {
					if ("version".equals(parse.getName())) {
						System.out.println();
						updateInfo.setVersion(parse.nextText());
					} else if ("description".equals(parse.getName())) {
						updateInfo.setDescription(parse.nextText());
					} else if ("path".equals(parse.getName())) {
						updateInfo.setPath(parse.nextText());
					}
				}
				type = parse.next();
			}
			return updateInfo;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
