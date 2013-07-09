package com.happyReading;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.happyReading.application.MyApplication;
import com.happyReading.bean.BaseNovelInfo;
import com.happyReading.bean.DetailedNovelInfo;
import com.happyReading.bean.DetailedNovelNoListInfo;
import com.happyReading.bean.NovelInfo;
import com.happyReading.bean.SimpleNovelInfo;
import com.happyReading.dao.NovelDao;
import com.happyReading.dao.ShelfDao;
import com.happyReading.domain.Novel;
import com.happyReading.download.NovelDownload;
import com.happyReading.net.NetUtil;
import com.happyReading.utils.NovelInfoUtils;

public class SearchActivity extends Activity {
	public static final int ONLINE_ONE = 0;
	public static final int ONLINE_MORE = 1;
	public static final int LOAD_ONE = 2;
	public static final int LOAD_MORE = 3;
	private EditText search_book_name;
	private Button clear_text;
	private Button search_now;
	private Button search_book_one_button_online;
	private Button search_book_one_button_download;
	private Button search_book_one_button_more;
	private RelativeLayout result_layout;
	private RelativeLayout search_book_one;
	private RelativeLayout loading;
	private ListView searchList;
	private TextView no_result;
	private NovelInfo resultInfo;
	private List<SimpleNovelInfo> simpleNovelInfos;
	private TextView tv_book_name;
	private TextView tv_book_auther;
	private TextView tv_book_name2;
	private TextView tv_book_auther2;
	private TextView tv_book_summary;
	private String bookName;
	private boolean noMoreBooks;
	private int searchPageNum;
	private boolean loadingMoreBookInfo;
	private boolean loadingOneBookInfo;
	// 用于横屏切换处理
	private int pageIndex;
	// 用于判断是哪个LISIVIEW哪个条目点击了按钮
	private int currentPosition;
	private Map<Integer, NovelDownload> loadMap;
	private SharedPreferences sp;
	private int[] topBgResource = { R.drawable.toolbar_bg,
			R.drawable.skin_blue, R.drawable.skin_purple, R.drawable.skin_red };


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		findView();
		init();
		setListener();
	}

	private void findView() {
		search_book_name = (EditText) findViewById(R.id.search_book_name);
		clear_text = (Button) findViewById(R.id.clear_text);
		search_now = (Button) findViewById(R.id.search_now);
		result_layout = (RelativeLayout) findViewById(R.id.result_layout);
		searchList = (ListView) findViewById(R.id.searchList);
		no_result = (TextView) findViewById(R.id.no_result);
		tv_book_name = (TextView) findViewById(R.id.tv_book_name);
		tv_book_auther = (TextView) findViewById(R.id.tv_book_auther);
		tv_book_name2 = (TextView) findViewById(R.id.tv_book_name2);
		tv_book_auther2 = (TextView) findViewById(R.id.tv_book_auther2);
		tv_book_summary = (TextView) findViewById(R.id.tv_book_summary);
		search_book_one = (RelativeLayout) findViewById(R.id.search_book_one);
		loading = (RelativeLayout) findViewById(R.id.loading);
		search_book_one_button_online = (Button) findViewById(R.id.search_book_one_button_online);
		search_book_one_button_download = (Button) findViewById(R.id.search_book_one_button_download);
		search_book_one_button_more = (Button) findViewById(R.id.search_book_one_button_more);

	}

	private void init() {
		// 设置获取更多书籍时的标签，默认不能获得更多书籍
		noMoreBooks = true;
		HomeTabActivity.top_title_name.setText("搜书");
		sp = getSharedPreferences("config", MODE_PRIVATE);
		int top_bg_style = sp.getInt("top_bg", 1);
		HomeTabActivity.top_title_name.setBackgroundResource(topBgResource[top_bg_style]);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		int topBgStyle = sp.getInt("top_bg", 1);
		HomeTabActivity.top_title_name.setBackgroundResource(topBgResource[topBgStyle]);
		loadingMoreBookInfo = false;
		loadingOneBookInfo = false;
		loadMap = MyApplication.loadMap;
	}

	private void setListener() {
		search_book_name.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
				if (search_book_name.getText().length() > 0) {
					clear_text.setVisibility(View.VISIBLE);
				}
				if (search_book_name.getText().length() == 0) {
					clear_text.setVisibility(View.GONE);
				}
			}
		});
		clear_text.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				search_book_name.setText("");
				clear_text.setVisibility(View.GONE);
			}
		});
		search_now.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				bookName = search_book_name.getText().toString().trim();
				if (TextUtils.isEmpty(bookName)) {
					Toast.makeText(SearchActivity.this, "书名不能为空！", 0).show();
					return;
				}
				if (!NetUtil.getInstance(SearchActivity.this).isNetAvailable()) {
					Toast.makeText(SearchActivity.this, "没有网络，不能进行搜索！", 0)
							.show();
					return;
				}
				if (loadingOneBookInfo == false) {
					searchBookByName(bookName);
				}
			}
		});
		search_book_one_button_online.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 将小说加入书架，进行在线阅读
				// 先要判断之前是否已经有该书了,判断存在问题，NovelDao.getNovelId(resultInfo)这个方法保持不变，其他地方
				// 用到，需要重新再写一个方法，来判断之前是否下载过，可以用书名+作者检索，检索到然后让用户选择
				search_book_one_button_online.setClickable(false);
				int novelId = NovelDao.getNovelId(resultInfo);
				String name = resultInfo.getNovelTitle();
				// 判断这本小说之前是否存入过数据库，根据URL判断
				// 这本小说之前没有存入数据库
				if (novelId == 0) {
					// 判断是否有同名小说在线
					if (NovelDao.isNovelOnline(name) == 0) {
						setNovelInshelf(resultInfo);
					} else {
						String message = "该书名的书籍已经有在线阅读了，是否设置这本书籍在线阅读？";
						Intent intent = new Intent(SearchActivity.this,
								DialogActivity.class);
						intent.putExtra("novelName", name);
						intent.putExtra("novelMessage", message);
						startActivityForResult(intent, ONLINE_ONE);
					}
				} else {
					// 这本小说之前存入过数据库
					// 这本小说未设置为在线，小说在线默认加入书架
					if (NovelDao.isNovelOnline(novelId) == 0) {
						if(ShelfDao.isNovelInShelf(novelId)==0){
							NovelDao.setNovelOnlineAndInShelf(novelId);
						}else{
							NovelDao.setNovelOnline(novelId);
						}
						Toast.makeText(SearchActivity.this, "成功设置在线阅读，并已放在书架上",
								0).show();
					} else {
						Toast.makeText(SearchActivity.this, "该书籍已经设置在线阅读了", 0)
								.show();
					}
				}
			}
		});
		search_book_one_button_download
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 下载全本，先异步获取小说的DetailedNovelInfo 然后传入进行下载
						// 需要解决好，各种逻辑控制问题
						search_book_one_button_download.setClickable(false);
						int novelId = NovelDao.getNovelId(resultInfo);
						String name = resultInfo.getNovelTitle();
						if (novelId == 0) {
							// 判断是否有同名小说下载过
							if (NovelDao.isNovelLoaded(name) == 0
									&& NovelDao.isNovelLoading(name) == 0
									&& NovelDao.isNovelWaitLoad(name) == 0) {
								loadNovel(resultInfo);
							} else {
								String message = "该书名的书籍已经下载过，是否下载这本书籍？";
								Intent intent = new Intent(SearchActivity.this,
										DialogActivity.class);
								intent.putExtra("novelName", name);
								intent.putExtra("novelMessage", message);
								startActivityForResult(intent, LOAD_ONE);
							}
						} else {
							// 这本小说之前存入过数据库
							// 这本小说未设置为在线，小说在线默认加入书架
							if (NovelDao.isNovelLoaded(novelId) == 0
									&& NovelDao.isNovelLoading(novelId) == 0
									&& NovelDao.isNovelWaitLoad(novelId) == 0) {
								// 判断是否有同名小说下载过
								if (NovelDao.isNovelLoaded(name) == 0
										&& NovelDao.isNovelLoading(name) == 0
										&& NovelDao.isNovelWaitLoad(name) == 0) {
									loadNovel(resultInfo);
								} else {
									String message = "该书名的书籍已经下载过，是否下载这本书籍？";
									Intent intent = new Intent(SearchActivity.this,
											DialogActivity.class);
									intent.putExtra("novelName", name);
									intent.putExtra("novelMessage", message);
									startActivityForResult(intent, LOAD_ONE);
								}
							} else {
								Toast.makeText(SearchActivity.this, "已经下载过该书籍",
										0).show();
							}
						}
					}
				});
		search_book_one_button_more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (noMoreBooks == false) {
					search_book_one_button_more.setClickable(false);
					searchPageNum = 1;
					simpleNovelInfos = new ArrayList<SimpleNovelInfo>();
					searchMoreBookByName(bookName, searchPageNum);
					dealWithBookListView();
				}
			}
		});

	}

	private void dealWithBookListView() {
		final BookListAdapter adapter = new BookListAdapter();
		searchList.setAdapter(adapter);
		searchList.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_FLING:
					break;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
					break;
				case OnScrollListener.SCROLL_STATE_IDLE:
					int startindex = searchList.getFirstVisiblePosition();
					int count = searchList.getChildCount();
					int currentLastViewIndex = startindex + count;
					if (currentLastViewIndex == simpleNovelInfos.size()) {
						if (noMoreBooks == false
								&& loadingMoreBookInfo == false) {
							searchPageNum++;
							searchMoreBookByName(bookName, searchPageNum);
							adapter.notifyDataSetChanged();
						}
					}
				default:
					break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});
	}

	/**
	 * 异步获取在线阅读的小说的详细信息
	 * 
	 * @param resultInfo
	 */
	private void setNovelInshelf(final BaseNovelInfo resultInfo) {

		new AsyncTask<BaseNovelInfo, Void, DetailedNovelNoListInfo>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(DetailedNovelNoListInfo result) {
				super.onPostExecute(result);
				if (result != null) {
					NovelDao.insertOnlineNovel(result);

					Toast.makeText(SearchActivity.this, "成功设置在线阅读，并已放在书架上", 0)
							.show();
				} else {
					Toast.makeText(SearchActivity.this, "由于网络问题设置在线阅读失败，请稍后再试",
							0).show();
				}
				search_book_one_button_online.setClickable(true);
			}

			@Override
			protected DetailedNovelNoListInfo doInBackground(
					BaseNovelInfo... params) {
				DetailedNovelNoListInfo detailedNovelNoListInfo = NovelInfoUtils
						.getDetailedNovelNolistInfo(resultInfo);
				return detailedNovelNoListInfo;
			}

		}.execute(resultInfo);
	}

	/**
	 * 异步获取在线阅读的小说的详细信息，并且开启新线程下载小说
	 * 
	 * @param resultInfo
	 */
	private void loadNovel(final BaseNovelInfo resultInfo) {

		new AsyncTask<BaseNovelInfo, Void, DetailedNovelInfo>() {

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
						if (novelId == 0) {
							NovelDao.insertLoadingNovel(result);
							novelId = NovelDao.getNovelId(result);
						}else{
							NovelDao.setNovelLoading(novelId);
						}
						search_book_one_button_download.setClickable(true);
						final NovelDownload loader = new NovelDownload(result);
						loader.setHandler(new MyLoadHandler(novelId));
						new Thread() {

							@Override
							public void run() {
								super.run();
								loader.download();
							}

						}.start();
						Toast.makeText(SearchActivity.this,
								"开始下载" + result.getNovelTitle(), 0).show();
						loadMap.put(novelId, loader);
						return;
					} else {
						Toast.makeText(SearchActivity.this, "请不要同时下载超过5本书籍，该书籍已经加入下载列表", 0)
								.show();
						NovelDao.insertWaitLoadNovel(result);
					}
				} else {
					Toast.makeText(SearchActivity.this, "由于网络问题下载失败，请稍后再试", 0)
							.show();
				}
			}

			@Override
			protected DetailedNovelInfo doInBackground(BaseNovelInfo... params) {
				DetailedNovelInfo detailedNovelInfo = NovelInfoUtils
						.getDetailedNovelInfo(resultInfo);
				return detailedNovelInfo;
			}

		}.execute(resultInfo);
	}

	private void searchBookByName(String bookName) {

		new AsyncTask<String, Void, NovelInfo>() {

			@Override
			protected void onPreExecute() {
				result_layout.setVisibility(View.GONE);
				searchList.setVisibility(View.GONE);
				search_book_one.setVisibility(View.GONE);
				no_result.setVisibility(View.GONE);
				loading.setVisibility(View.VISIBLE);
				loadingOneBookInfo = true;
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(NovelInfo result) {
				loading.setVisibility(View.INVISIBLE);
				dealWithAfterSearchOneUI(result);
				loadingOneBookInfo = false;
				super.onPostExecute(result);
			}

			@Override
			protected NovelInfo doInBackground(String... params) {
				NovelInfo novelInfo = null;
				novelInfo = NovelInfoUtils.getNovelInfo(params[0]);
				return novelInfo;
			}
		}.execute(bookName);
	}

	private void searchMoreBookByName(String bookName, final int pageNum) {

		new AsyncTask<String, Void, List<SimpleNovelInfo>>() {

			@Override
			protected void onPreExecute() {
				result_layout.setVisibility(View.VISIBLE);
				searchList.setVisibility(View.VISIBLE);
				search_book_one.setVisibility(View.GONE);
				no_result.setVisibility(View.GONE);
				loading.setVisibility(View.VISIBLE);
				loadingMoreBookInfo = true;
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(List<SimpleNovelInfo> result) {
				loading.setVisibility(View.GONE);
				//TODO 
				search_book_one_button_more.setClickable(true);
				dealWithAfterSearchMoreUI(result);
				loadingMoreBookInfo = false;
				super.onPostExecute(result);
			}

			@Override
			protected List<SimpleNovelInfo> doInBackground(String... params) {
				List<SimpleNovelInfo> infos = null;
				infos = NovelInfoUtils.getMoreNovelInfo(SearchActivity.this,params[0], pageNum);
				return infos;
			}
		}.execute(bookName);
	}

	private void dealWithAfterSearchOneUI(NovelInfo result) {
		if (result == null) {
			result_layout.setVisibility(View.VISIBLE);
			searchList.setVisibility(View.GONE);
			search_book_one.setVisibility(View.GONE);
			no_result.setVisibility(View.VISIBLE);
			pageIndex = 1;
			return;
		}
		// result 说明可以执行获取更多书籍
		pageIndex = 2;
		noMoreBooks = false;
		resultInfo = result;
		result_layout.setVisibility(View.VISIBLE);
		searchList.setVisibility(View.GONE);
		search_book_one.setVisibility(View.VISIBLE);
		no_result.setVisibility(View.GONE);
		String bookName1 = "  " + resultInfo.getNovelTitle();
		String bookAuther1 = "  " + resultInfo.getNovelAuther();
		if (resultInfo.getNovelTitle().length() > 5) {
			bookName1 = "  " + resultInfo.getNovelTitle().substring(0, 4)
					+ "...";
		}
		if (resultInfo.getNovelAuther().length() > 6) {
			bookAuther1 = "  " + resultInfo.getNovelAuther().substring(0, 5)
					+ "...";
		}
		tv_book_name.setText(bookName1);
		tv_book_auther.setText(bookAuther1);
		String bookName2 = "书名:" + resultInfo.getNovelTitle();
		String bookAuther2 = "作者:" + resultInfo.getNovelAuther();
		tv_book_name2.setText(bookName2);
		tv_book_auther2.setText(bookAuther2);
		String bookSummary = resultInfo.getNovelAbstract();
		tv_book_summary.setText(bookSummary);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ONLINE_ONE) {
			if (resultCode == DialogActivity.SURE) {
				setNovelInshelf(resultInfo);
			}
		}
		if (requestCode == ONLINE_MORE) {
			if (resultCode == DialogActivity.SURE) {
				setNovelInshelf(simpleNovelInfos.get(currentPosition));
			}
		}
		if (requestCode == LOAD_ONE) {
			if (resultCode == DialogActivity.SURE) {
				loadNovel(resultInfo);
			}
		}
		if (requestCode == LOAD_MORE) {
			if (resultCode == DialogActivity.SURE) {
				loadNovel(simpleNovelInfos.get(currentPosition));
			}
		}
	}

	private void dealWithAfterSearchMoreUI(List<SimpleNovelInfo> result) {
		pageIndex = 3;
		if (result == null) {
			result_layout.setVisibility(View.VISIBLE);
			searchList.setVisibility(View.VISIBLE);
			search_book_one.setVisibility(View.GONE);
			no_result.setVisibility(View.GONE);
			Toast.makeText(SearchActivity.this, "没有更多相关书籍了", 0).show();
			noMoreBooks = true;
			return;
		}
		result_layout.setVisibility(View.VISIBLE);
		searchList.setVisibility(View.VISIBLE);
		search_book_one.setVisibility(View.GONE);
		no_result.setVisibility(View.GONE);
		simpleNovelInfos.addAll(result);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (pageIndex == 1) {
			result_layout.setVisibility(View.VISIBLE);
			searchList.setVisibility(View.GONE);
			search_book_one.setVisibility(View.GONE);
			no_result.setVisibility(View.VISIBLE);
		} else if (pageIndex == 2) {
			result_layout.setVisibility(View.VISIBLE);
			searchList.setVisibility(View.GONE);
			search_book_one.setVisibility(View.VISIBLE);
			no_result.setVisibility(View.GONE);
		} else if (pageIndex == 3) {
			result_layout.setVisibility(View.VISIBLE);
			searchList.setVisibility(View.VISIBLE);
			search_book_one.setVisibility(View.GONE);
			no_result.setVisibility(View.GONE);
		}
	}

	private class BookListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return simpleNovelInfos.size();
		}

		@Override
		public Object getItem(int position) {
			return simpleNovelInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View view;
			BookListViewHolder holder;
			if (convertView != null) {
				view = convertView;
				holder = (BookListViewHolder) view.getTag();
			} else {
				view = View.inflate(SearchActivity.this,
						R.layout.search_book_item, null);
				holder = new BookListViewHolder();
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
				holder.search_book_more_item_button1 = (Button) view
						.findViewById(R.id.search_book_more_item_button1);
				holder.search_book_more_item_button2 = (Button) view
						.findViewById(R.id.search_book_more_item_button2);
				view.setTag(holder);
			}
			String name1 = "  "
					+ simpleNovelInfos.get(position).getNovelTitle();
			String auther1 = "  "
					+ simpleNovelInfos.get(position).getNovelAuther();
			if (simpleNovelInfos.get(position).getNovelTitle().length() > 5) {
				String subName1 = simpleNovelInfos.get(position)
						.getNovelTitle().substring(0, 4);
				name1 = "  " + subName1 + "...";
			}
			if (simpleNovelInfos.get(position).getNovelAuther().length() > 6) {
				String subAuther1 = simpleNovelInfos.get(position)
						.getNovelAuther().substring(0, 5);
				auther1 = "  " + subAuther1 + "...";
			}
			holder.tv_book_name.setText(name1);
			holder.tv_book_auther.setText(auther1);
			holder.tv_book_order_num.setText("序号:"
					+ simpleNovelInfos.get(position).getOrderNum());
			String bookName2 = "书名:"
					+ simpleNovelInfos.get(position).getNovelTitle();
			String bookAuther2 = "作者:"
					+ simpleNovelInfos.get(position).getNovelAuther();
			holder.tv_book_name2.setText(bookName2);
			holder.tv_book_auther2.setText(bookAuther2);
			holder.tv_book_lastest.setText(simpleNovelInfos.get(position)
					.getNovelLastChapterName());
			holder.search_book_more_item_button1
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// 在线阅读
							currentPosition = position;
							int novelId = NovelDao.getNovelId(simpleNovelInfos.get(position));
							String name = simpleNovelInfos.get(position).getNovelTitle();
							// 判断这本小说之前是否存入过数据库，根据URL判断
							// 这本小说之前没有存入数据库
							if (novelId == 0) {
								// 判断是否有同名小说在线
								if (NovelDao.isNovelOnline(name) == 0) {
									setNovelInshelf(simpleNovelInfos.get(position));
								} else {
									String message = "该书名的书籍已经有在线阅读了，是否设置这本书籍在线阅读？";
									Intent intent = new Intent(SearchActivity.this,
											DialogActivity.class);
									intent.putExtra("novelName", name);
									intent.putExtra("novelMessage", message);
									startActivityForResult(intent, ONLINE_MORE);
								}
							} else {
								// 这本小说之前存入过数据库
								// 这本小说未设置为在线，小说在线默认加入书架
								if (NovelDao.isNovelOnline(novelId) == 0) {
									if(ShelfDao.isNovelInShelf(novelId)==0){
										NovelDao.setNovelOnlineAndInShelf(novelId);
									}else{
										NovelDao.setNovelOnline(novelId);
									}
									Toast.makeText(SearchActivity.this, "成功设置在线阅读，并已放在书架上",
											0).show();
								} else {
									Toast.makeText(SearchActivity.this, "该书籍已经设置在线阅读了", 0)
											.show();
								}
							}
						}
					});
			holder.search_book_more_item_button2
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// 下载全本，先异步获取小说的DetailedNovelInfo 然后传入进行下载
							// 需要解决好，各种逻辑控制问题
							currentPosition = position;
							int novelId = NovelDao.getNovelId(simpleNovelInfos.get(position));
							String name = simpleNovelInfos.get(position).getNovelTitle();
							if (novelId == 0) {
								// 判断是否有同名小说下载过
								if (NovelDao.isNovelLoaded(name) == 0
										&& NovelDao.isNovelLoading(name) == 0
										&& NovelDao.isNovelWaitLoad(name) == 0) {
									loadNovel(simpleNovelInfos.get(position));
								} else {
									String message = "该书名的书籍已经下载过，是否下载这本书籍？";
									Intent intent = new Intent(SearchActivity.this,
											DialogActivity.class);
									intent.putExtra("novelName", name);
									intent.putExtra("novelMessage", message);
									startActivityForResult(intent, LOAD_MORE);
								}
							} else {
								// 这本小说之前存入过数据库
								// 这本小说未设置为在线，小说在线默认加入书架
								if (NovelDao.isNovelLoaded(novelId) == 0
										&& NovelDao.isNovelLoading(novelId) == 0
										&& NovelDao.isNovelWaitLoad(novelId) == 0) {
									// 判断是否有同名小说下载过
									if (NovelDao.isNovelLoaded(name) == 0
											&& NovelDao.isNovelLoading(name) == 0
											&& NovelDao.isNovelWaitLoad(name) == 0) {
										loadNovel(simpleNovelInfos.get(position));
									} else {
										String message = "该书名的书籍已经下载过，是否下载这本书籍？";
										Intent intent = new Intent(SearchActivity.this,
												DialogActivity.class);
										intent.putExtra("novelName", name);
										intent.putExtra("novelMessage", message);
										startActivityForResult(intent, LOAD_MORE);
									}
								} else {
									Toast.makeText(SearchActivity.this, "已经下载过该书籍",
											0).show();
								}
							}
						}
					});
			return view;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		sp = getSharedPreferences("config", MODE_PRIVATE);
		int top_bg_style = sp.getInt("top_bg", 1);
		HomeTabActivity.top_title_name.setBackgroundResource(topBgResource[top_bg_style]);
		HomeTabActivity.top_title_name.setText("搜书");
	}

	static class BookListViewHolder {
		Button search_book_more_item_button1;
		Button search_book_more_item_button2;
		TextView tv_book_name;
		TextView tv_book_auther;
		TextView tv_book_order_num;
		TextView tv_book_name2;
		TextView tv_book_auther2;
		TextView tv_book_lastest;
	}

	private class MyLoadHandler extends Handler {
		int novelId;
		int total;

		public MyLoadHandler(int novelId) {
			super();
			this.novelId = novelId;
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
				break;
			case 3:
				Novel novel = NovelDao.getNovel(novelId);
				Toast.makeText(SearchActivity.this,
						novel.getName() + "-" + novel.getAuther() + "已经下载完成", 0)
						.show();
				NovelDao.setLoadingToLoaded(novelId);
				break;
			case 4:
				Novel novel2 = NovelDao.getNovel(novelId);
				int loadErrorChapterNum = msg.getData().getInt(
						"loadedChapterCount");
				Toast.makeText(
						SearchActivity.this,
						novel2.getName() + "由于网络问题下载停止，已下载到第"
								+ loadErrorChapterNum + "章", 0).show();
				String loadProgress2 = loadErrorChapterNum * 100 / total + "%";
				NovelDao.setLoadProgress(novelId, loadProgress2);
				NovelDao.setLoadingToWaitLoad(novelId);
				NovelDao.setNovelLoadedChapterNum(novelId, loadErrorChapterNum);
				break;
			case 5:
				Toast.makeText(SearchActivity.this, "由于网络问题下载失败，请稍后再试", 0)
						.show();
				break;
			default:
				break;
			}
		}

	}
}
