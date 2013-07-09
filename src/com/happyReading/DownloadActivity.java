package com.happyReading;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.happyReading.application.MyApplication;
import com.happyReading.bean.DetailedNovelInfo;
import com.happyReading.dao.NovelDao;
import com.happyReading.dao.ShelfDao;
import com.happyReading.domain.Novel;
import com.happyReading.download.NovelDownload;
import com.happyReading.readingView.mark.MarkHelper;
import com.happyReading.utils.NovelInfoUtils;

public class DownloadActivity extends Activity {
	private RadioGroup loadGroup;
	private RadioButton radio_button_loading;
	private static final int CHOOSE_LOADING = 0;
	private static final int CHOOSE_LOADED = 1;
	private ListView lv_load_show_list;
	private int chooseNum;
	private LoadShowListAdapter adapter;
	private List<Novel> loadingAndWaitLoadNovels;
	private List<Novel> loadedNovels;
	public String loadSDPath;
	private SharedPreferences sp;
	private int[] topBgResource = { R.drawable.toolbar_bg,
			R.drawable.skin_blue, R.drawable.skin_purple, R.drawable.skin_red };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_download);
		super.onCreate(savedInstanceState);
		findView();
		init();
		setListener();
		getData();
		setAdapter();
	}

	private void findView() {
		loadGroup = (RadioGroup) findViewById(R.id.load_radio_group);
		radio_button_loading = (RadioButton) findViewById(R.id.radio_button_loading);
		lv_load_show_list = (ListView) findViewById(R.id.lv_load_show_list);
	}

	private void init() {
		HomeTabActivity.top_title_name.setText("下载");
		sp = getSharedPreferences("config", MODE_PRIVATE);
		int top_bg_style = sp.getInt("top_bg", 1);
		HomeTabActivity.top_title_name.setBackgroundResource(topBgResource[top_bg_style]);
		radio_button_loading.setChecked(true);
		chooseNum = CHOOSE_LOADING;
		loadingAndWaitLoadNovels = new ArrayList<Novel>();
		loadedNovels = new ArrayList<Novel>();
		loadSDPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/happyReading";
	}

	@Override
	protected void onResume() {
		super.onResume();
		sp = getSharedPreferences("config", MODE_PRIVATE);
		int top_bg_style = sp.getInt("top_bg", 1);
		HomeTabActivity.top_title_name.setBackgroundResource(topBgResource[top_bg_style]);
		HomeTabActivity.top_title_name.setText("下载");
		getData();
	}

	private void setListener() {
		loadGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.radio_button_loading:
					chooseNum = CHOOSE_LOADING;
					getData();
					break;
				case R.id.radio_button_loaded:
					chooseNum = CHOOSE_LOADED;
					getData();
					break;
				default:
					break;
				}
			}
		});
	}

	private void getData() {
		loadingAndWaitLoadNovels = new ArrayList<Novel>();
		loadedNovels = new ArrayList<Novel>();
		List<Novel> novelsLoading = NovelDao.getNovelsLoading();
		List<Novel> novelsWaitLoad = NovelDao.getNovelsWaitLoad();
		List<Novel> novelsLoaded = NovelDao.getNovelsLoaded();
		loadingAndWaitLoadNovels.addAll(novelsLoading);
		loadingAndWaitLoadNovels.addAll(novelsWaitLoad);
		loadedNovels.addAll(novelsLoaded);
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}

	private void setAdapter() {
		adapter = new LoadShowListAdapter();
		lv_load_show_list.setAdapter(adapter);
	}

	/**
	 * 异步获取在线阅读的小说的详细信息，并且开启新线程下载小说
	 * 
	 * @param resultInfo
	 */
	private void loadNovel(final String name, final String url,
			final TextView tv, final String txtName) {

		new AsyncTask<String, Void, DetailedNovelInfo>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(DetailedNovelInfo result) {
				super.onPostExecute(result);
				if (result != null) {
					if (NovelDownload.taskNum <= NovelDownload.TASK_NUM_MAX) {
						int novelId = NovelDao.getNovelId(result);
						int startChapterNum = 0;
						if (novelId == 0) {
							NovelDao.insertLoadingNovel(result);
							novelId = NovelDao.getNovelId(result);
						} else {
							NovelDao.setWaitLoadToLoading(novelId);
							startChapterNum = NovelDao
									.getNovelLoadedChapterNum(novelId);
							getData();
						}
						final NovelDownload loader = new NovelDownload(result);

						loader.setStartChapterNum(startChapterNum);
						loader.setHandler(new MyLoadHandler(novelId, tv));
						loader.setTxtName(txtName);
						new Thread() {

							@Override
							public void run() {
								super.run();
								loader.download();
							}

						}.start();
						Toast.makeText(DownloadActivity.this,
								"开始下载" + result.getNovelTitle(), 0).show();
						MyApplication.loadMap.put(novelId, loader);
						return;
					} else {
						Toast.makeText(DownloadActivity.this, "请不要同时下载超过5本书籍",
								0).show();
					}
				} else {
					Toast.makeText(DownloadActivity.this, "由于网络问题下载失败，请稍后再试", 0)
							.show();
				}
			}

			@Override
			protected DetailedNovelInfo doInBackground(String... params) {
				DetailedNovelInfo detailedNovelInfo = NovelInfoUtils
						.getDetailedNovelInfo(params[0], params[1]);
				return detailedNovelInfo;
			}

		}.execute(name, url);
	}

	private class LoadShowListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (chooseNum == CHOOSE_LOADING) {
				return loadingAndWaitLoadNovels.size();
			} else {
				return loadedNovels.size();
			}
		}

		@Override
		public Object getItem(int position) {
			if (chooseNum == CHOOSE_LOADING) {
				return loadingAndWaitLoadNovels.get(position);
			} else {
				return loadedNovels.get(position);
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			final LoadListViewHolder holder;
			if (convertView != null) {
				view = convertView;
				holder = (LoadListViewHolder) view.getTag();
			} else {
				view = View.inflate(DownloadActivity.this,
						R.layout.load_book_item, null);
				holder = new LoadListViewHolder();
				holder.tv_book_name = (TextView) view
						.findViewById(R.id.tv_book_name);
				holder.tv_book_auther = (TextView) view
						.findViewById(R.id.tv_book_auther);
				holder.tv_book_order_num = (TextView) view
						.findViewById(R.id.tv_book_order_num);
				holder.tv_book_name2 = (TextView) view
						.findViewById(R.id.tv_book_name2);
				holder.tv_book_auther2 = (TextView) view
						.findViewById(R.id.tv_book_auther2);
				holder.tv_book_lastest = (TextView) view
						.findViewById(R.id.tv_book_lastest);
				holder.load_book_button1 = (Button) view
						.findViewById(R.id.load_book_button1);
				holder.load_book_button2 = (Button) view
						.findViewById(R.id.load_book_button2);
				holder.tv_book_load_progress = (TextView) view
						.findViewById(R.id.tv_book_load_progress);

				view.setTag(holder);
			}
			final Novel novel;
			if (chooseNum == CHOOSE_LOADING) {
				novel = loadingAndWaitLoadNovels.get(position);
				final NovelDownload load = MyApplication.loadMap.get(novel
						.get_id());
				holder.tv_book_load_progress.setVisibility(View.VISIBLE);
				holder.tv_book_load_progress.setText(novel.getLoadProgress());
				if (novel.getLoading() == 1) {
					holder.load_book_button1.setText("暂停下载");

					// 控制里面显示进度的TextView变化
					MyLoadHandler handler = new MyLoadHandler(novel.get_id(),
							holder.tv_book_load_progress);
					if (load != null) {
						load.setHandler(handler);
					}
					// 在load中加入标记，从而控制下载的暂停和继续
					holder.load_book_button1
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									if (load != null) {
										load.setPause(true);
									}
									NovelDao.setLoadingToWaitLoad(novel
											.get_id());
									getData();
									Toast.makeText(DownloadActivity.this,
											"暂停下载" + novel.getName(), 0).show();
								}
							});
					holder.load_book_button2
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									if (load != null) {
										load.setPause(true);
									}
									try {
										Thread.sleep(200);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									String txtName = NovelDao
											.getNovelTxtName(novel.get_id());
									File file = new File(loadSDPath + "/"
											+ txtName);
									if (file.exists()) {
										file.delete();
										// TODO 删除书签数据库中的相关信息
										MarkHelper markhelper = new MarkHelper(
												DownloadActivity.this);
										String path0 = file.getAbsolutePath();
										// 删除数据库书签记录
										SQLiteDatabase db2 = markhelper
												.getWritableDatabase();
										db2.delete("markhelper", "path='"
												+ path0 + "'", null);
										db2.close();
									}
									if (NovelDao.isNovelOnline(novel.get_id()) == 1) {
										NovelDao.setNovelToNoLoad(novel
												.get_id());
									} else {
										NovelDao.deleteNovelByNovelId(novel
												.get_id());
									}
									Toast.makeText(DownloadActivity.this,
											"删除书籍" + novel.getName(), 0).show();
									getData();
								}
							});

				} else {
					holder.load_book_button1.setText("开始下载");
					holder.load_book_button1
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									loadNovel(novel.getName(), novel.getUrl(),
											holder.tv_book_load_progress,
											novel.getTxtName());
									getData();
								}
							});
					holder.load_book_button2
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									String txtName = NovelDao
											.getNovelTxtName(novel.get_id());
									File file = new File(loadSDPath + "/"
											+ txtName);
									if (file.exists()) {
										file.delete();
										// TODO 删除书签数据库中的信息
										MarkHelper markhelper = new MarkHelper(
												DownloadActivity.this);
										String path0 = file.getAbsolutePath();
										// 删除数据库书签记录
										SQLiteDatabase db2 = markhelper
												.getWritableDatabase();
										db2.delete("markhelper", "path='"
												+ path0 + "'", null);
										db2.close();
									}
									if (NovelDao.isNovelOnline(novel.get_id()) == 1) {
										NovelDao.setNovelToNoLoad(novel
												.get_id());
									} else {
										NovelDao.deleteNovelByNovelId(novel
												.get_id());
									}
									Toast.makeText(DownloadActivity.this,
											"删除书籍" + novel.getName(), 0).show();
									getData();
								}
							});

				}
				holder.load_book_button2.setText("删除书籍");
			} else {
				novel = loadedNovels.get(position);
				final int novelId = novel.get_id();
				holder.tv_book_load_progress.setVisibility(View.GONE);
				if (novel.getInShelf() == 1) {
					holder.load_book_button1.setText("移除书架");
					holder.load_book_button1
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									ShelfDao.deleteShelfNovelByNovelId(novelId);
									getData();
								}
							});

				} else {
					holder.load_book_button1.setText("加入书架");
					holder.load_book_button1
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									ShelfDao.insertShelfNovel(novelId);
									getData();
								}
							});
				}
				holder.load_book_button2.setText("删除书籍");
				holder.load_book_button2
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								String txtName = NovelDao
										.getNovelTxtName(novelId);
								File file = new File(loadSDPath + "/" + txtName);
								if (file.exists()) {
									file.delete();
									// TODO 删除书签数据库中的信息
									MarkHelper markhelper = new MarkHelper(
											DownloadActivity.this);
									String path0 = file.getAbsolutePath();
									// 删除数据库书签记录
									SQLiteDatabase db2 = markhelper
											.getWritableDatabase();
									db2.delete("markhelper", "path='" + path0
											+ "'", null);
									db2.close();
								}
								if (NovelDao.isNovelOnline(novel.get_id()) == 1) {
									NovelDao.setNovelToNoLoad(novel.get_id());
								} else {
									NovelDao.deleteNovelByNovelId(novel
											.get_id());
								}
								Toast.makeText(DownloadActivity.this,
										"删除书籍" + novel.getName(), 0).show();
								getData();
							}
						});
			}
			String name1 = "  " + novel.getName();
			String auther1 = "  " + novel.getAuther();
			if (novel.getName().length() > 5) {
				String subName1 = novel.getName().substring(0, 4);
				name1 = "  " + subName1 + "...";
			}
			if (novel.getAuther().length() > 6) {
				String subAuther1 = novel.getAuther().substring(0, 5);
				auther1 = "  " + subAuther1 + "...";
			}
			holder.tv_book_order_num.setText("序号:" + (position + 1));
			holder.tv_book_name.setText(name1);
			holder.tv_book_auther.setText(auther1);
			String bookName2 = "书名:" + novel.getName();
			String bookAuther2 = "作者:" + novel.getAuther();
			holder.tv_book_name2.setText(bookName2);
			holder.tv_book_auther2.setText(bookAuther2);
			holder.tv_book_lastest.setText("最新:" + novel.getLastChapterName());

			return view;
		}
	}

	static class LoadListViewHolder {
		Button load_book_button1;
		Button load_book_button2;
		TextView tv_book_name;
		TextView tv_book_auther;
		TextView tv_book_order_num;
		TextView tv_book_name2;
		TextView tv_book_auther2;
		TextView tv_book_lastest;
		TextView tv_book_load_progress;
	}

	private class MyLoadHandler extends Handler {
		int novelId;
		int total;
		TextView tv;

		public MyLoadHandler(int novelId, TextView tv) {
			super();
			this.novelId = novelId;
			this.tv = tv;
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				total = msg.getData().getInt("totalChapterCount");
				break;
			case 2:
				int loaded = msg.getData().getInt("loadedChapterCount");
				String loadProgress = loaded * 100 / total + "%";
				NovelDao.setLoadProgress(novelId, loadProgress);
				NovelDao.setNovelLoadedChapterNum(novelId, loaded);
				tv.setText(loadProgress);
				break;
			case 3:
				Novel novel = NovelDao.getNovel(novelId);
				Toast.makeText(DownloadActivity.this,
						novel.getName() + "-" + novel.getAuther() + "已经下载完成", 0)
						.show();
				NovelDao.setLoadingToLoaded(novelId);
				MyApplication.loadMap.remove(novelId);
				getData();
				break;
			case 4:
				Novel novel2 = NovelDao.getNovel(novelId);
				total = msg.getData().getInt("totalChapterCount");
				int loadErrorChapterNum = msg.getData().getInt(
						"loadedChapterCount");
				Toast.makeText(
						DownloadActivity.this,
						novel2.getName() + "由于网络问题下载停止，已下载到第"
								+ loadErrorChapterNum + "章", 0).show();
				String loadProgress2 = loadErrorChapterNum * 100 / total + "%";
				NovelDao.setLoadProgress(novelId, loadProgress2);
				NovelDao.setLoadingToWaitLoad(novelId);
				NovelDao.setNovelLoadedChapterNum(novelId, loadErrorChapterNum);
				MyApplication.loadMap.remove(novelId);
				getData();
				break;
			case 5:
				Toast.makeText(DownloadActivity.this, "由于网络问题下载失败，请稍后再试", 0)
						.show();
				MyApplication.loadMap.remove(novelId);
				getData();
				break;
			default:
				break;
			}
		}

	}
}
