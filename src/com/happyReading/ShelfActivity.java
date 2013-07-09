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
	 * ������ȡ���ú��˸������ѵ��鼮�ĸ�����Ϣ������и�����updateNotification��ֵ��Ϊ2
	 */
	private void getUpdateMessage() {
		// ���߸��µı�д���ȽϺ��ϴν��и��µ�ʱ�䣬���ʱ�����2Сʱ�ˣ����ȡ���������߸���С˵����ϸ��Ϣ��Ȼ��ȽϺ�
		// ���ݿ��еĴ�С����������ݿ��еĴ����ʾ�и��£���Ϊ��Ҫ�������������Ҫ�첽����
		SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
		long lastUpdateTime = sp.getLong("lastUpdateTime", 0);
		long currentTimeMillis = System.currentTimeMillis();
		if (currentTimeMillis - lastUpdateTime < 2 * 60 * 60 * 1000) {
			// ��������ϴθ��²�����2Сʱ����ֹͣ����,�����ֵ�������û���������
			return;
		}
		if (NetUtil.getInstance(ShelfActivity.this).isNetAvailable() == false) {
			Toast.makeText(ShelfActivity.this, "û�����磬δ���и���", 0).show();
			return;
		}
		final List<Novel> updateNovelInfos = new ArrayList<Novel>();
		for (Novel novel : novelInfos) {
			// Ϊ1��ʾ���ú��˸��£�������δ������������Ϣ��
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
				Toast.makeText(ShelfActivity.this, "��ʼ������", 0).show();
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(Void result) {
				init();
				gridAdapter.notifyDataSetChanged();
				Toast.makeText(ShelfActivity.this, "���������", 0).show();
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
				// ��Novel��Id�����Ķ�����
				// ����ǰС˵����Ϊ����Ķ���������������ܵ���ǰ��
				// ������ʾ����
				Novel novel = novelInfos.get(position);
				int novelId = novel.get_id();
				ShelfDao.setReadOrderNum(novel.get_id());
				Toast.makeText(ShelfActivity.this, "��ʼ�Ķ�" + novel.getName(), 0)
						.show();
				if (NovelDao.isNovelOnline(novelId) == 0) {
					// ���С˵���������Ķ������������ֱ�Ӷ�ȡ���غõ�С˵
					Intent intent = new Intent(ShelfActivity.this,
							ReadViewActivity.class);
					intent.putExtra("novelId", novelId);
					startActivity(intent);
				} else {
					// ���С˵����
					// 1�����ж��Ƿ������磬û������ֱ�ӷ���
					if (!NetUtil.getInstance(ShelfActivity.this)
							.isNetAvailable()) {
						Toast.makeText(ShelfActivity.this, "û�����磬���ܽ��������Ķ���", 0)
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
						// ����С˵�����ý��棬�����Ƴ���ܣ�ɾ���鼮�����ø������ѵ�����
						// ������ʾ����
						Novel novel = novelInfos.get(position);
						String name = novel.getName();
						currentNovel = novel;
						currentNovelId = novel.get_id();
						if (novel.getOnline() == 1) {
							String message = "�Ƿ񽫸��鼮�Ƴ���ܣ�";
							Intent intent = new Intent(ShelfActivity.this,
									Dialog2Activity.class);
							intent.putExtra("novelName", name);
							intent.putExtra("novelMessage", message);
							intent.putExtra("style", 1);
							startActivityForResult(intent, ONLINE);
						} else {
							String message = "��ѡ����Ҫ���еĲ���";
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
						"�ѽ�" + currentNovel.getName() + "�Ƴ����", 0).show();
			}
			if (resultCode == Dialog2Activity.BUTTON2) {

			}
		}
		if (requestCode == NOT_ONLINE) {
			if (resultCode == Dialog2Activity.BUTTON1) {
				// �����Ķ�
				NovelDao.setNovelOnline(currentNovelId);
				init();
				gridAdapter.notifyDataSetChanged();
				Toast.makeText(ShelfActivity.this,
						"�ѽ�" + currentNovel.getName() + "��Ϊ�����Ķ�", 0).show();
			}
			if (resultCode == Dialog2Activity.BUTTON2) {
				// �Ƴ����
				ShelfDao.deleteShelfNovelByNovelId(currentNovelId);
				init();
				gridAdapter.notifyDataSetChanged();
				Toast.makeText(ShelfActivity.this,
						"�ѽ�" + currentNovel.getName() + "�Ƴ����", 0).show();
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
		HomeTabActivity.top_title_name.setText("���");
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
			// ��ѯ���ݿ���м���
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
			// ���ߣ������߱�ǩ�ɼ�
			if (online == 1) {
				holder.iv_shelf_novel_online.setVisibility(View.VISIBLE);
			} else {
				holder.iv_shelf_novel_online.setVisibility(View.INVISIBLE);
			}
			int update = novel.getUpdateNotification();
			if (update == 0 || online == 0) {
				holder.iv_shelf_novel_update.setVisibility(View.INVISIBLE);
			} else if (update == 1) {
				// ���ø���������
				holder.iv_shelf_novel_update.setVisibility(View.VISIBLE);
				holder.iv_shelf_novel_update
						.setBackgroundResource(R.drawable.novel_update_normal);
			} else if (update == 2) {
				// ���ø������ѣ������и�����
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
