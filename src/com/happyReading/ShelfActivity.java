package com.happyReading;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.happyReading.bean.DetailedNovelInfo;
import com.happyReading.dao.NovelDao;
import com.happyReading.dao.ShelfDao;
import com.happyReading.domain.Novel;
import com.happyReading.domain.Shelf;
import com.happyReading.net.NetUtil;
import com.happyReading.readingView.ReadViewActivity;
import com.happyReading.ui.ShelfGridView;
import com.happyReading.ui.ShelfListView;
import com.happyReading.utils.NovelInfoUtils;

public class ShelfActivity extends Activity {
	private ShelfGridView shelf_show_list;
	private ShelfListView shelf_show_divide;
	private List<Integer> novelIds;
	private int gridChildCount;
	private MyGridAdapter gridAdapter;
	private MyDivideAdapter divideAdapter;
	private List<Novel> novelInfos;
	private static final int ONLINE = 1;
	private static final int NOT_ONLINE = 2;
	private int currentNovelId;
	private Novel currentNovel;
	private SharedPreferences sp;
	private int[] topBgResource = { R.drawable.toolbar_bg,
			R.drawable.skin_blue, R.drawable.skin_purple, R.drawable.skin_red };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_shelf);
		super.onCreate(savedInstanceState);
		findView();
		init();
		setAdapter();
		setItemClick();
		getUpdateMessage();
	}

	/**
	 * 用来获取设置好了更新提醒的书籍的更新信息，如果有更新则将updateNotification的值变为2
	 */
	private void getUpdateMessage() {
		// 在线更新的编写，比较和上次进行更新的时间，如果时间大于2小时了，则获取设置了在线更新小说的详细信息，然后比较和
		// 数据库中的大小，如果比数据库中的大则表示有更新，因为需要网络访问所以需要异步进行
		SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
		long lastUpdateTime = sp.getLong("lastUpdateTime", 0);
		long currentTimeMillis = System.currentTimeMillis();
		if (currentTimeMillis - lastUpdateTime < 2 * 60 * 60 * 1000) {
			// 如果距离上次更新不超过2小时，则停止更新,这个数值可以让用户进行设置
			return;
		}
		if (NetUtil.getInstance(ShelfActivity.this).isNetAvailable() == false) {
			Toast.makeText(ShelfActivity.this, "没有网络，未进行更新", 0).show();
			return;
		}
		final List<Novel> updateNovelInfos = new ArrayList<Novel>();
		for (Novel novel : novelInfos) {
			// 为1表示设置好了更新，但是尚未检索到更新信息的
			if (novel.getUpdateNotification() == 1) {
				updateNovelInfos.add(novel);
			}
		}
		if (updateNovelInfos.size() == 0) {
			return;
		}
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {
				Toast.makeText(ShelfActivity.this, "开始检查更新", 0).show();
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(Void result) {
				init();
				gridAdapter.notifyDataSetChanged();
				Toast.makeText(ShelfActivity.this, "检查更新完毕", 0).show();
				super.onPostExecute(result);
			}

			@Override
			protected Void doInBackground(Void... params) {
				for (Novel novel : updateNovelInfos) {
					int novelId = novel.get_id();
					String novelName = novel.getName();
					String novelUrl = novel.getUrl();
					DetailedNovelInfo detailedNovelInfo = NovelInfoUtils
							.getDetailedNovelInfo(novelName, novelUrl);
					int lastChapterIndex = novel.getLastChapterIndex();
					if (detailedNovelInfo.getInfos().size() > lastChapterIndex) {
						NovelDao.setNovelLastChapterIndex(novelId, 2);
					}
				}
				return null;
			}
		}.execute();
		Editor editor = sp.edit();
		editor.putLong("lastUpdateTime", currentTimeMillis);
		editor.commit();
	}

	private void setItemClick() {
		shelf_show_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 将Novel的Id传入阅读界面
				// 将当前小说设置为最近阅读，将它放置在书架的最前面
				// 更新显示界面
				Novel novel = novelInfos.get(position);
				int novelId = novel.get_id();
				ShelfDao.setReadOrderNum(novel.get_id());
				Toast.makeText(ShelfActivity.this, "开始阅读" + novel.getName(), 0)
						.show();
				if (NovelDao.isNovelOnline(novelId) == 0) {
					// 如果小说不是在线阅读，则进入里面直接读取下载好的小说
					Intent intent = new Intent(ShelfActivity.this,
							ReadViewActivity.class);
					intent.putExtra("novelId", novelId);
					startActivity(intent);
				} else {
					// 如果小说在线
					// 1、先判断是否有网络，没有网络直接返回
					if (!NetUtil.getInstance(ShelfActivity.this)
							.isNetAvailable()) {
						Toast.makeText(ShelfActivity.this, "没有网络，不能进行在线阅读！", 0)
								.show();
						return;
					}
					Intent intent = new Intent(ShelfActivity.this,
							ReadViewMenuActivity.class);
					intent.putExtra("novelId", novelId);
					startActivity(intent);

				}
			}
		});
		shelf_show_list
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						// 弹出小说的设置界面，包括移除书架，删除书籍，设置更新提醒等设置
						// 更新显示界面
						Novel novel = novelInfos.get(position);
						String name = novel.getName();
						currentNovel = novel;
						currentNovelId = novel.get_id();
						if (novel.getOnline() == 1) {
							String message = "是否将该书籍移除书架？";
							Intent intent = new Intent(ShelfActivity.this,
									Dialog2Activity.class);
							intent.putExtra("novelName", name);
							intent.putExtra("novelMessage", message);
							intent.putExtra("style", 1);
							startActivityForResult(intent, ONLINE);
						} else {
							String message = "请选择您要进行的操作";
							Intent intent = new Intent(ShelfActivity.this,
									Dialog2Activity.class);
							intent.putExtra("novelName", name);
							intent.putExtra("novelMessage", message);
							intent.putExtra("style", 2);
							startActivityForResult(intent, NOT_ONLINE);
						}
						return true;
					}
				});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ONLINE) {
			if (resultCode == Dialog2Activity.BUTTON1) {
				if (currentNovel.getLoaded() == 1) {
					ShelfDao.deleteShelfNovelByNovelId(currentNovelId);
					NovelDao.setNovelNotOnline(currentNovelId);
				} else {
					NovelDao.deleteNovelByNovelId(currentNovelId);
				}
				init();
				gridAdapter.notifyDataSetChanged();
				Toast.makeText(ShelfActivity.this,
						"已将" + currentNovel.getName() + "移除书架", 0).show();
			}
			if (resultCode == Dialog2Activity.BUTTON2) {

			}
		}
		if (requestCode == NOT_ONLINE) {
			if (resultCode == Dialog2Activity.BUTTON1) {
				// 在线阅读
				NovelDao.setNovelOnline(currentNovelId);
				init();
				gridAdapter.notifyDataSetChanged();
				Toast.makeText(ShelfActivity.this,
						"已将" + currentNovel.getName() + "设为在线阅读", 0).show();
			}
			if (resultCode == Dialog2Activity.BUTTON2) {
				// 移除书架
				ShelfDao.deleteShelfNovelByNovelId(currentNovelId);
				init();
				gridAdapter.notifyDataSetChanged();
				Toast.makeText(ShelfActivity.this,
						"已将" + currentNovel.getName() + "移除书架", 0).show();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		init();
		gridAdapter.notifyDataSetChanged();
		divideAdapter.notifyDataSetChanged();
	}

	private void findView() {
		shelf_show_list = (ShelfGridView) findViewById(R.id.shelf_show_list);
		shelf_show_divide = (ShelfListView) findViewById(R.id.shelf_show_divide);

	}

	private void init() {
		HomeTabActivity.top_title_name.setText("书架");
		sp = getSharedPreferences("config", MODE_PRIVATE);
		int top_bg_style = sp.getInt("top_bg", 1);
		HomeTabActivity.top_title_name.setBackgroundResource(topBgResource[top_bg_style]);
		novelInfos = new LinkedList<Novel>();
		Shelf.getInstance();
		novelIds = Shelf.novelIds;
		for (int i = 0; i < novelIds.size(); i++) {
			Novel info = NovelDao.getNovel(novelIds.get(i));
			novelInfos.add(info);

		}
		gridChildCount = novelIds.size();
	}

	private void setAdapter() {
		gridAdapter = new MyGridAdapter();
		shelf_show_list.setAdapter(gridAdapter);
		divideAdapter = new MyDivideAdapter();
		shelf_show_divide.setAdapter(divideAdapter);

	}

	private class MyGridAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// 查询数据库进行计算
			return gridChildCount;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			Holder holder;
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				holder = (Holder) view.getTag();

			} else {
				view = View.inflate(ShelfActivity.this,
						R.layout.shelf_show_item, null);
				holder = new Holder();
				holder.iv_shelf_novel_online = (ImageView) view
						.findViewById(R.id.iv_shelf_novel_online);
				holder.iv_shelf_novel_update = (ImageView) view
						.findViewById(R.id.iv_shelf_novel_update);
				holder.tv_shelf_novel_name = (TextView) view
						.findViewById(R.id.tv_shelf_novel_name);
				holder.tv_shelf_novel_auther = (TextView) view
						.findViewById(R.id.tv_shelf_novel_auther);
				view.setTag(holder);
			}
			Novel novel = novelInfos.get(position);
			String name = novel.getName();
			String auther = novel.getAuther();
			int online = novel.getOnline();
			// 在线，则在线标签可见
			if (online == 1) {
				holder.iv_shelf_novel_online.setVisibility(View.VISIBLE);
			} else {
				holder.iv_shelf_novel_online.setVisibility(View.INVISIBLE);
			}
			int update = novel.getUpdateNotification();
			if (update == 0 || online == 0) {
				holder.iv_shelf_novel_update.setVisibility(View.INVISIBLE);
			} else if (update == 1) {
				// 设置更新提醒了
				holder.iv_shelf_novel_update.setVisibility(View.VISIBLE);
				holder.iv_shelf_novel_update
						.setBackgroundResource(R.drawable.novel_update_normal);
			} else if (update == 2) {
				// 设置更新提醒，并且有更新了
				holder.iv_shelf_novel_update.setVisibility(View.VISIBLE);
				holder.iv_shelf_novel_update
						.setBackgroundResource(R.drawable.novel_update_on);
			}
			if (name.length() > 5) {
				name = novel.getName().substring(0, 4) + "...";
			}
			if (auther.length() > 6) {
				auther = novel.getAuther().substring(0, 5) + "...";
			}
			holder.tv_shelf_novel_name.setText(name);
			holder.tv_shelf_novel_auther.setText(auther);
			return view;
		}

		private class Holder {
			ImageView iv_shelf_novel_online;
			ImageView iv_shelf_novel_update;
			TextView tv_shelf_novel_name;
			TextView tv_shelf_novel_auther;
		}
	}

	private class MyDivideAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			int divideNum = 3;
			if (gridChildCount > 9) {
				divideNum = (int) Math.ceil((float) gridChildCount / 3);
			}
			return divideNum;
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			if (convertView != null && convertView instanceof LinearLayout) {
				view = convertView;
			}
			view = View.inflate(ShelfActivity.this, R.layout.shelf_show_divide,
					null);
			return view;
		}
	}

}
