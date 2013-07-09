package com.happyReading;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.happyReading.bean.ChapterInfo;
import com.happyReading.bean.DetailedNovelInfo;
import com.happyReading.bean.SearchNetIds;
import com.happyReading.dao.NovelDao;
import com.happyReading.domain.Novel;
import com.happyReading.readingView.ReadViewActivity;
import com.happyReading.utils.NovelInfoUtils;
import com.umeng.analytics.MobclickAgent;

public class ReadViewMenuActivity extends Activity {
	private int novelId;
	private Novel novel;
	private TextView tv_book_name;
	private TextView tv_book_auther;
	private TextView tv_book_name2;
	private TextView tv_book_auther2;
	private TextView tv_book_lastest;
	private Button book_button1;
	private Button book_button2;
	private ProgressBar list_loading_progress;
	private Button order_btn;
	private ListView lv_chapter_show_list;
	private Button pre_btn;
	private TextView page_num;
	private Button next_btn;
	private List<ChapterInfo> chapterInfos;
	private List<ChapterInfo> orderInfos;
	// 用来设置正序和反序，正序1，反序2
	private int orderFlag = 1;
	private MyChapterListAdapter adapter;
	private int totalPage = 0;
	private int nowPage = 1;
	private int count = 0;
	private boolean hasData = false;
	public static DetailedNovelInfo detailedNovelInfo;
	public static int currentChapterIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 1.先异步加载，获取DetaledNovelInfo
		// 2.如果没有获取到DetaledNovelInfo，则显示（没有获取到图书信息）退出这个界面
		// 3.判断这本小说时在线，在线已下载，已下载
		// 4.如果是在线，显示（下载全本），（更新提醒）
		// 5.如果是在线已下载，显示（全本阅读），（更新提醒）
		// 6.如果是已下载，显示（在线阅读）
		setContentView(R.layout.activity_read_view_menu);
		findView();
		currentChapterIndex = 0;
		getData();
		setData();
		setListener();
		dealWithListView();
		getDetailedNovelInfo();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		adapter.notifyDataSetChanged();
		if (currentChapterIndex != 0) {
			if (orderFlag == 1) {
				lv_chapter_show_list.setSelection(currentChapterIndex - 1);
			} else {
				lv_chapter_show_list.setSelection(orderInfos.size()
						- (currentChapterIndex - 1) - 1);
			}
		}
	}

	private void dataChange() {
		lv_chapter_show_list.setAdapter(adapter);
		lv_chapter_show_list.post(new Runnable() {
			// fileList为与adapter做系结的list总数
			public void run() {
				count = lv_chapter_show_list.getChildCount();
				/* end of if */
				totalPage = (int) Math.ceil(((float) chapterInfos.size() / (count - 1)));
				page_num.setText(nowPage + "/" + totalPage);
				if (currentChapterIndex != 0) {
					if (orderFlag == 1) {
						lv_chapter_show_list
								.setSelection(currentChapterIndex - 1);
					} else {
						lv_chapter_show_list.setSelection(orderInfos.size()
								- (currentChapterIndex - 1) - 1);
					}
				}
			}
		});
		// 获取视图中ListView中含有多少个子元素
		totalPage = (int) Math
				.ceil(((float) chapterInfos.size() / (count - 1)));
		nowPage = 1;
		page_num.setText(nowPage + "/" + totalPage);
	}

	private void dealWithListView() {
		adapter = new MyChapterListAdapter();
		lv_chapter_show_list.setAdapter(adapter);
		if (currentChapterIndex != 0) {
			lv_chapter_show_list.setSelection(currentChapterIndex - 1);
		}
		orderInfos = chapterInfos;
		lv_chapter_show_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				// 异步加载章节信息，加载完毕，复制内容到nowChapter.txt中，然后跳转到阅读界面
				final File file = new File(ReadViewMenuActivity.this
						.getFilesDir(), "nowChapter.txt");
				final ChapterInfo chapterInfo = (ChapterInfo) lv_chapter_show_list
						.getItemAtPosition(position);
				new AsyncTask<Void, Void, Integer>() {

					@Override
					protected void onPreExecute() {
						list_loading_progress.setVisibility(View.VISIBLE);
						super.onPreExecute();
					}

					@Override
					protected void onPostExecute(Integer result) {
						list_loading_progress.setVisibility(View.INVISIBLE);
						if (result == 0) {
							Toast.makeText(ReadViewMenuActivity.this,
									"获取章节信息失败", 0).show();
						} else {
							Intent intent = new Intent(
									ReadViewMenuActivity.this,
									ReadViewActivity.class);
							intent.putExtra("novelId", novelId);
							intent.putExtra("chapterIndex",
									chapterInfo.getChapterIndex());
							startActivity(intent);
						}
						super.onPostExecute(result);
					}

					@Override
					protected Integer doInBackground(Void... params) {
						int result = 1;
						SearchNetIds searchNetIds = NovelInfoUtils
								.getSearchNetIds(detailedNovelInfo);
						if (searchNetIds == null) {
							result = 0;
						}
						String chapterContent = NovelInfoUtils
								.getChapterContent(searchNetIds,
										orderInfos.get(position));
						if (chapterContent == null) {
							result = 0;
						}
						FileWriter writer = null;
						try {
							writer = new FileWriter(file, false);
							writer.write(chapterContent);
							writer.flush();
							writer.close();
						} catch (IOException e) {
							result = 0;
							e.printStackTrace();
						}

						return result;
					}
				}.execute();
			}
		});
	}

	private void setListener() {
		book_button1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if ("更新提醒".equals(book_button1.getText().toString())) {
					NovelDao.setUpdateNotification(novelId, 1);
					book_button1.setText("取消提醒");
				} else {
					NovelDao.setUpdateNotification(novelId, 0);
					book_button1.setText("更新提醒");
				}
			}
		});
		book_button2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ReadViewMenuActivity.this,
						ReadViewActivity.class);
				intent.putExtra("novelId", novelId);
				NovelDao.setNovelNotOnline(novelId);
				startActivity(intent);
				ReadViewMenuActivity.this.finish();
			}
		});
		order_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (hasData == false) {
					return;
				}
				if (orderFlag == 1) {
					orderFlag = 2;
					order_btn.setText("反序");
					// 将章节列表反序显示出来
					orderInfos = new ArrayList<ChapterInfo>();
					for (int i = chapterInfos.size() - 1; i >= 0; i--) {
						orderInfos.add(chapterInfos.get(i));
					}
					adapter.notifyDataSetChanged();
					lv_chapter_show_list.setSelection(0);
					nowPage = 1;
					page_num.setText(nowPage + "/" + totalPage);

				} else {
					orderFlag = 1;
					order_btn.setText("正序");
					// 将章节列表正序显示出来
					orderInfos = chapterInfos;
					adapter.notifyDataSetChanged();
					lv_chapter_show_list.setSelection(0);
					nowPage = 1;
					page_num.setText(nowPage + "/" + totalPage);
				}
			}
		});
		pre_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (hasData == false) {
					return;
				}
				if (nowPage > 1) {
					nowPage--;
					lv_chapter_show_list.setSelection((count - 1)
							* (nowPage - 1));
					totalPage = (int) Math.ceil(((float) chapterInfos.size() / (count - 1)));
					page_num.setText(nowPage + "/" + totalPage);
				}
			}
		});
		next_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (hasData == false) {
					return;
				}
				if (nowPage < totalPage) {
					nowPage++;
					lv_chapter_show_list.setSelection((count - 1)
							* (nowPage - 1));
					totalPage = (int) Math.ceil(((float) chapterInfos.size() / (count - 1)));
					page_num.setText(nowPage + "/" + totalPage);
				}
			}
		});
	}

	private void getDetailedNovelInfo() {
		new AsyncTask<String, Void, DetailedNovelInfo>() {

			@Override
			protected void onPreExecute() {
				list_loading_progress.setVisibility(View.VISIBLE);
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(DetailedNovelInfo result) {
				super.onPostExecute(result);

				list_loading_progress.setVisibility(View.INVISIBLE);
				if (result != null) {
					detailedNovelInfo = result;
					NovelDao.setNovelLastChapterIndex(novelId, result
							.getInfos().size());
					chapterInfos = result.getInfos();
					orderInfos = chapterInfos;
					hasData = true;
					dataChange();
				} else {
					Toast.makeText(ReadViewMenuActivity.this, "获取小说信息失败", 0)
							.show();
				}
			}

			@Override
			protected DetailedNovelInfo doInBackground(String... params) {
				DetailedNovelInfo detailedNovelInfo = NovelInfoUtils
						.getDetailedNovelInfo(params[0], params[1]);
				return detailedNovelInfo;
			}

		}.execute(novel.getName(), novel.getUrl());
	}

	private void findView() {
		tv_book_name = (TextView) findViewById(R.id.tv_book_name);
		tv_book_auther = (TextView) findViewById(R.id.tv_book_auther);
		tv_book_name2 = (TextView) findViewById(R.id.tv_book_name2);
		tv_book_auther2 = (TextView) findViewById(R.id.tv_book_auther2);
		tv_book_lastest = (TextView) findViewById(R.id.tv_book_lastest);
		book_button1 = (Button) findViewById(R.id.book_button1);
		book_button2 = (Button) findViewById(R.id.book_button2);
		list_loading_progress = (ProgressBar) findViewById(R.id.list_loading_progress);
		order_btn = (Button) findViewById(R.id.order_btn);
		lv_chapter_show_list = (ListView) findViewById(R.id.lv_chapter_show_list);
		pre_btn = (Button) findViewById(R.id.pre_btn);
		page_num = (TextView) findViewById(R.id.page_num);
		next_btn = (Button) findViewById(R.id.next_btn);

	}

	private void getData() {
		novelId = getIntent().getIntExtra("novelId", 0);
		novel = NovelDao.getNovel(novelId);
		if (novel.getUpdateNotification() == 2) {
			NovelDao.setUpdateNotification(novelId, 1);
		}
		// TODO 获取之前读到的章节，然后存起来，将listview 最上面的显示到读到的章节

		int readerChapterIndex = NovelDao.getNovelReadedChapterIndex(novelId);
		if (readerChapterIndex != 0) {
			currentChapterIndex = readerChapterIndex;
		}
	}

	private void setData() {
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
		tv_book_name.setText(name1);
		tv_book_auther.setText(auther1);
		String bookName2 = "书名:" + novel.getName();
		String bookAuther2 = "作者:" + novel.getAuther();
		tv_book_name2.setText(bookName2);
		tv_book_auther2.setText(bookAuther2);
		tv_book_lastest.setText("最新:" + novel.getLastChapterName());
		page_num.setText("0/0");
		orderInfos = new ArrayList<ChapterInfo>();
		chapterInfos = new ArrayList<ChapterInfo>();
		if (novel.getUpdateNotification() > 0) {
			book_button1.setText("取消提醒");
		} else {
			book_button1.setText("更新提醒");
		}
		if (novel.getLoaded() == 1) {
			book_button2.setVisibility(View.VISIBLE);
		} else {
			book_button2.setVisibility(View.INVISIBLE);
		}
	}

	private class MyChapterListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return orderInfos.size();
		}

		@Override
		public Object getItem(int position) {
			return orderInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View view;
			ChapterListHolder holder;
			if (convertView != null) {
				view = convertView;
				holder = (ChapterListHolder) view.getTag();
			} else {
				view = View.inflate(ReadViewMenuActivity.this,
						R.layout.chapter_txt, null);
				holder = new ChapterListHolder();
				holder.tv_chapter_name = (TextView) view
						.findViewById(R.id.tv_chapter_name);
				holder.tv_chapter_order = (TextView) view
						.findViewById(R.id.tv_chapter_order);
				view.setTag(holder);
			}
			ChapterInfo info = orderInfos.get(position);
			if (info.getChapterIndex() == currentChapterIndex) {

				holder.tv_chapter_order.setText("第" + info.getChapterIndex()
						+ "章" + "   已读");
				holder.tv_chapter_name.setText(info.getChapterName());

			} else {
				holder.tv_chapter_order.setText("第" + info.getChapterIndex()
						+ "章");
				holder.tv_chapter_name.setText(info.getChapterName());
			}
			return view;
		}

	}

	static class ChapterListHolder {
		TextView tv_chapter_order;
		TextView tv_chapter_name;

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		NovelDao.setNovelReadedChapterIndex(novelId, currentChapterIndex);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
