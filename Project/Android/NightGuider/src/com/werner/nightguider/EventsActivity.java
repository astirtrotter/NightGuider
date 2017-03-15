package com.werner.nightguider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.werner.nightguider.activitygroup.ActivityEventsStack;
import com.werner.nightguider.adapter.ListEventsAdapter;

public class EventsActivity extends Activity {

	// Declare Variables
	private PullToRefreshGridView mPullRefreshGridView;
	private GridView mGridView;

	private ProgressDialog mProgressDialog;
	private ListEventsAdapter mAdapter;
	private Context mContext;
	private EventsActivity thisContext;

	private String mCityName;
	private String mCityPick;

	private boolean mIsAllEventsLoaded;

	private int mPulledCount;

	private boolean mIsAllSearchEventLoaded = false;

	LinearLayout searchBar;
	List<ListEventsModel> aryListEvent;
	List<ListEventsModel> arySearchedListEvent;

	EditText txtSearchKey;
	List<String> aryGroupDate;

	private boolean isFilterSet = false;

	private List<ListEventsModel> arySearchResult;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_events);

		mContext = getParent();

		// SearchBar
		searchBar = (LinearLayout) findViewById(R.id.searchBarOnEvent);
		searchBar.setVisibility(View.GONE);

		ImageButton btnSearch = (ImageButton) findViewById(R.id.imgBtnSearchOnEvent);
		btnSearch.setOnTouchListener(GlobalConstants.touchListener);
		btnSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getParent(), SearchActivity.class);
				ActivityEventsStack activityStack = (ActivityEventsStack) getParent();
				activityStack.push("EventsActivity" + ++GlobalConstants.EVENTS_ACTIVITY_COUNT, intent);
			}
		});
		// arySearchResult = new ArrayList<ListEventsModel>();
		aryListEvent = null;

		arySearchedListEvent = null;
		aryGroupDate = null;

		txtSearchKey = (EditText) findViewById(R.id.txtSearchKey);

		txtSearchKey.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// if((event!=null && (event.getKeyCode() ==
				// KeyEvent.ACTION_DOWN)) || actionId ==
				// EditorInfo.IME_ACTION_DONE){
				// arySearchedListEvent = GlobalConstants.mEventListForSearch;
				// aryGroupDate = GlobalConstants.mGroupKeyList;
				// if (txtSearchKey.getText().toString().length() == 0) {
				// // aryListEvent = arySearchResult;
				// showEvents(false);
				// return true;
				// }
				//
				// // aryListEvent = null;
				// arySearchedListEvent = null;
				// // aryGroupDate = null;
				// arySearchedListEvent = new ArrayList<ListEventsModel>();
				// // aryGroupDate = new ArrayList<String>();
				// List<ListEventsModel> listEvent = arySearchedListEvent;
				// if (listEvent.size() == 0)
				// {
				// isFilterSet = false;
				// return true;
				// }
				// isFilterSet = true;
				// for(int i = 0; i < listEvent.size(); i++) {
				// String strEventTitle =
				// listEvent.get(i).getEventName().toLowerCase().trim();
				// String[] aryKeyComponent =
				// txtSearchKey.getText().toString().toLowerCase().split(" ");
				//
				// boolean isFound = true;
				// for (int j = 0; j < aryKeyComponent.length; j++) {
				// if (!strEventTitle.contains(aryKeyComponent[j])) {
				// isFound = false;
				// break;
				// }
				// }
				// if (isFound) {
				// arySearchedListEvent.add(listEvent.get(i));
				// }
				//
				// }
				// showEvents(false);
				// }
				// return false;
				// }
				if ((event != null && (event.getKeyCode() == KeyEvent.ACTION_DOWN)) || actionId == EditorInfo.IME_ACTION_DONE) {
					if (txtSearchKey.getText().toString().length() == 0) {
						isFilterSet = false;
						arySearchedListEvent = GlobalConstants.mEventListForSearch;
						// aryGroupDate = GlobalConstants.mGroupKeyList;
						showEvents(false);
						return true;
					}

					arySearchedListEvent = null;
					// aryGroupDate = null;
					arySearchedListEvent = new ArrayList<ListEventsModel>();
					// aryGroupDate = new ArrayList<String>();
					List<ListEventsModel> listEvent = GlobalConstants.mEventListForSearch;
					if (listEvent.size() == 0) {
						isFilterSet = false;
						return true;
					}
					isFilterSet = true;
					for (int i = 0; i < listEvent.size(); i++) {
						String strEventTitle = listEvent.get(i).getEventName().toLowerCase().trim();
						String[] aryKeyComponent = txtSearchKey.getText().toString().toLowerCase().split(" ");

						boolean isFound = true;
						for (int j = 0; j < aryKeyComponent.length; j++) {
							if (!strEventTitle.contains(aryKeyComponent[j])) {
								isFound = false;
								break;
							}
						}
						if (isFound) {
							arySearchedListEvent.add(listEvent.get(i));
						}

					}
					showEvents(false);
				}

				return false;
			}
		});

		// txtSearchKey.addTextChangedListener(new TextWatcher() {
		//
		// @Override
		// public void onTextChanged(CharSequence s, int start, int before, int
		// count) {
		// // TODO Auto-generated method stub
		//
		// if (txtSearchKey.getText().toString().length() == 0) {
		// isFilterSet = false;
		// arySearchedListEvent = GlobalConstants.mEventListForSearch;
		// // aryGroupDate = GlobalConstants.mGroupKeyList;
		// showEvents(false);
		// return;
		// }
		//
		// arySearchedListEvent = null;
		// // aryGroupDate = null;
		// arySearchedListEvent = new ArrayList<ListEventsModel>();
		// // aryGroupDate = new ArrayList<String>();
		// List<ListEventsModel> listEvent =
		// GlobalConstants.mEventListForSearch;
		// if (listEvent.size() == 0)
		// {
		// isFilterSet = false;
		// return;
		// }
		// isFilterSet = true;
		// for(int i = 0; i < listEvent.size(); i++) {
		// String strEventTitle =
		// listEvent.get(i).getEventName().toLowerCase().trim();
		// String[] aryKeyComponent =
		// txtSearchKey.getText().toString().toLowerCase().split(" ");
		//
		// boolean isFound = true;
		// for (int j = 0; j < aryKeyComponent.length; j++) {
		// if (!strEventTitle.contains(aryKeyComponent[j])) {
		// isFound = false;
		// break;
		// }
		// }
		// if (isFound) {
		// arySearchedListEvent.add(listEvent.get(i));
		// }
		//
		// }
		// showEvents(false);
		// }
		//
		// @Override
		// public void beforeTextChanged(CharSequence s, int start, int count,
		// int after) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void afterTextChanged(Editable s) {
		// // TODO Auto-generated method stub
		//
		// }
		// });
		Button btnCancel = (Button) findViewById(R.id.btnCancelSearch);
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				aryListEvent = GlobalConstants.mEventsList;
				aryGroupDate = GlobalConstants.mGroupKeyList;
				isFilterSet = false;
				txtSearchKey.setText("");
				showEvents(true);
			}
		});

		// >SearchBar

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		mCityName = prefs.getString(GlobalConstants.PREF_CITY_NAME, "");
		mCityPick = prefs.getString(GlobalConstants.PREF_CITY_PICK, "");

		mPullRefreshGridView = (PullToRefreshGridView) findViewById(R.id.list_events);
		mGridView = mPullRefreshGridView.getRefreshableView();

		// Set a listener to be invoked when the list should be refreshed.
		mPullRefreshGridView.setOnRefreshListener(new OnRefreshListener2<GridView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
				GlobalConstants.mEventsList = new ArrayList<ListEventsModel>();
				GlobalConstants.mGroupKeyList = new ArrayList<String>();
				mIsAllEventsLoaded = false;
				mPulledCount = 0;
				new GetEventsAsyncTask().execute();

				// display search panel
				if (searchBar.getVisibility() == View.GONE) {
					searchBar.setVisibility(View.VISIBLE);
					isFilterSet = true;
				}
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
				if (mIsAllEventsLoaded) {
					Toast.makeText(EventsActivity.this, "No more events!", Toast.LENGTH_SHORT).show();
					mPullRefreshGridView.onRefreshComplete();
					return;
				}

				mPulledCount++;
				new GetEventsAsyncTask().execute();
			}

		});

		mPullRefreshGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View parent, int position, long rowId) {
				// TODO Auto-generated method stub

				// if(GlobalConstants.isEventsDataLoading) return;

				Intent intent = new Intent();

				if (isFilterSet) {
					intent.putExtra(GlobalConstants.BUNDLE_EVENT_ID, arySearchedListEvent.get(position).getObjectId());
					intent.putExtra(GlobalConstants.BUNDLE_EVENT_EID, arySearchedListEvent.get(position).getEId());
					intent.putExtra(GlobalConstants.BUNDLE_EVENT_NAME, arySearchedListEvent.get(position).getEventName());
					intent.putExtra(GlobalConstants.BUNDLE_EVENT_HOST, arySearchedListEvent.get(position).getHost());
					intent.putExtra(GlobalConstants.BUNDLE_EVENT_DESCRIPTION, arySearchedListEvent.get(position).getDescription());
					intent.putExtra(GlobalConstants.BUNDLE_EVENT_ENTRANCEPRICE, arySearchedListEvent.get(position).getEntrancePrice());
					intent.putExtra(GlobalConstants.BUNDLE_EVENT_STARTTIME, arySearchedListEvent.get(position).getStartTime());
					intent.putExtra(GlobalConstants.BUNDLE_EVENT_ENDTIME, arySearchedListEvent.get(position).getEndTime());
					intent.putExtra(GlobalConstants.BUNDLE_EVENT_PICSMALL, arySearchedListEvent.get(position).getPicSmall());
					intent.putExtra(GlobalConstants.BUNDLE_EVENT_PICBIG, arySearchedListEvent.get(position).getPicBig());
					intent.putExtra(GlobalConstants.BUNDLE_EVENT_FBLINK, arySearchedListEvent.get(position).getFBLink());
					intent.putExtra(GlobalConstants.BUNDLE_EVENT_STREET, arySearchedListEvent.get(position).getStreet());
					intent.putExtra(GlobalConstants.BUNDLE_EVENT_LATITUDE, arySearchedListEvent.get(position).getLatitude());
					intent.putExtra(GlobalConstants.BUNDLE_EVENT_LONGITUDE, arySearchedListEvent.get(position).getLongitude());
				} else {

					intent.putExtra(GlobalConstants.BUNDLE_EVENT_ID, GlobalConstants.mEventsList.get(position).getObjectId());
					intent.putExtra(GlobalConstants.BUNDLE_EVENT_EID, GlobalConstants.mEventsList.get(position).getEId());
					intent.putExtra(GlobalConstants.BUNDLE_EVENT_NAME, GlobalConstants.mEventsList.get(position).getEventName());
					intent.putExtra(GlobalConstants.BUNDLE_EVENT_HOST, GlobalConstants.mEventsList.get(position).getHost());
					intent.putExtra(GlobalConstants.BUNDLE_EVENT_DESCRIPTION, GlobalConstants.mEventsList.get(position).getDescription());
					intent.putExtra(GlobalConstants.BUNDLE_EVENT_ENTRANCEPRICE, GlobalConstants.mEventsList.get(position).getEntrancePrice());
					intent.putExtra(GlobalConstants.BUNDLE_EVENT_STARTTIME, GlobalConstants.mEventsList.get(position).getStartTime());
					intent.putExtra(GlobalConstants.BUNDLE_EVENT_ENDTIME, GlobalConstants.mEventsList.get(position).getEndTime());
					intent.putExtra(GlobalConstants.BUNDLE_EVENT_PICSMALL, GlobalConstants.mEventsList.get(position).getPicSmall());
					intent.putExtra(GlobalConstants.BUNDLE_EVENT_PICBIG, GlobalConstants.mEventsList.get(position).getPicBig());
					intent.putExtra(GlobalConstants.BUNDLE_EVENT_FBLINK, GlobalConstants.mEventsList.get(position).getFBLink());
					intent.putExtra(GlobalConstants.BUNDLE_EVENT_STREET, GlobalConstants.mEventsList.get(position).getStreet());
					intent.putExtra(GlobalConstants.BUNDLE_EVENT_LATITUDE, GlobalConstants.mEventsList.get(position).getLatitude());
					intent.putExtra(GlobalConstants.BUNDLE_EVENT_LONGITUDE, GlobalConstants.mEventsList.get(position).getLongitude());
				}
				intent.setClass(getParent(), EventsDetailsActivity.class);
				ActivityEventsStack activityStack = (ActivityEventsStack) getParent();
				activityStack.push("EventsActivity" + ++GlobalConstants.EVENTS_ACTIVITY_COUNT, intent);

			}
		});

		if (GlobalConstants.mEventsList == null || GlobalConstants.mEventsList.size() == 0) {
			// Execute GetEventsAsyncTask AsyncTask
			GlobalConstants.mEventsList = new ArrayList<ListEventsModel>();
			GlobalConstants.mGroupKeyList = new ArrayList<String>();
			mPulledCount = 0;
			mIsAllEventsLoaded = false;
			new GetEventsAsyncTask().execute();

		} else {
			showEvents(false);
		}

		ImageButton btn_info = (ImageButton) this.findViewById(R.id.btn_infomation);
		btn_info.setOnTouchListener(GlobalConstants.touchListener);
		btn_info.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				AlertDialog.Builder buildSingle = new AlertDialog.Builder(getParent());
				buildSingle.setTitle(mCityName);
				buildSingle.setMessage(getResources().getString(R.string.change_city));
				buildSingle.setNegativeButton(android.R.string.no, null);
				buildSingle.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Intent intent = new Intent(EventsActivity.this,
										SelectCityActivity.class);
								intent.putExtra("From Setting", true);
								startActivity(intent);
								finish();
							}
						});
				buildSingle.show();


//				Intent intent = new Intent();
//				intent.setClass(getParent(), SettingActivity.class);
//				startActivity(intent);
				// ActivityEventsStack activityStack = (ActivityEventsStack)
				// getParent();
				// activityStack.push("EventsActivity" +
				// ++GlobalConstants.EVENTS_ACTIVITY_COUNT, intent);
			}
		});
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK ) {
			onBackPressed();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onResume() {
		super.onResume();

		TextView tv = (TextView) this.findViewById(R.id.textView_menutext);
		tv.setText(mCityName);
		tv.setTypeface(GlobalConstants.fontAller);
		if (searchBar.getVisibility() == View.GONE) {
			isFilterSet = false;
		} else {
			isFilterSet = true;
		}
	}

	@Override
	public void onPause() {
		super.onPause();

	}

	public void showEvents(boolean isCanCel) {
		if (mPulledCount == 0) {
			// mAdapter = new ListEventsAdapter(EventsActivity.this,
			// GlobalConstants.mEventsList, GlobalConstants.mGroupKeyList);
			// mAdapter = new ListEventsAdapter(EventsActivity.this,
			// aryListEvent, GlobalConstants.mGroupKeyList);

			if (!isFilterSet) {
				if (aryListEvent == null) {
					aryListEvent = GlobalConstants.mEventsList;
					aryGroupDate = GlobalConstants.mGroupKeyList;
					// isFilterSet = false;
				}
				mAdapter = new ListEventsAdapter(EventsActivity.this, aryListEvent, aryGroupDate);
				mGridView.setAdapter(mAdapter);
			} else {
				// isFilterSet = false;
				if (arySearchedListEvent == null) {
					arySearchedListEvent = GlobalConstants.mEventListForSearch;
				}
				mAdapter = new ListEventsAdapter(EventsActivity.this, arySearchedListEvent, aryGroupDate);
				mGridView.setAdapter(mAdapter);
			}
		} else {
			if (isFilterSet) {
				// isFilterSet = false;
				if (arySearchedListEvent == null) {
					arySearchedListEvent = GlobalConstants.mEventListForSearch;
				}
				mAdapter = new ListEventsAdapter(EventsActivity.this, arySearchedListEvent, aryGroupDate);
				mGridView.setAdapter(mAdapter);
			} else {
				if (isCanCel) {
					aryListEvent = GlobalConstants.mEventsList;
					aryGroupDate = GlobalConstants.mGroupKeyList;
					// isFilterSet = false;
					mAdapter = new ListEventsAdapter(EventsActivity.this, aryListEvent, aryGroupDate);
					mGridView.setAdapter(mAdapter);
				}
				// isFilterSet = true;
				// mAdapter = new ListEventsAdapter(EventsActivity.this,
				// aryListEvent, aryGroupDate);
				// mGridView.setAdapter(mAdapter);
				mAdapter.notifyDataSetChanged();
			}
		}
	}

	// GetEventsAsyncTask AsyncTask
	private class GetEventsAsyncTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setTitle("Please wait...");
			mProgressDialog.setMessage("Loading...");
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.setCancelable(true);

			// Show progressdialog
			if (mPulledCount == 0)
				mProgressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			try {

				boolean isEnd = false;

				if (isFilterSet) {
					int tmp_pulledCount = 0;
					if (GlobalConstants.mEventListForSearch == null) {
						GlobalConstants.mEventListForSearch = new ArrayList<ListEventsModel>();
					} else {
						if (mIsAllSearchEventLoaded)
							return null;
						else
							GlobalConstants.mEventListForSearch.clear();

					}
					while (!isEnd) {

						ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Events");
						query.whereGreaterThan(GlobalConstants.PARSE_KEY_EVENTENDTIME, new Date());

						query.whereEqualTo("city_pick", mCityPick);
						query.setLimit(GlobalConstants.refreshEventsCount);
						query.setSkip(tmp_pulledCount * GlobalConstants.refreshEventsCount);
						tmp_pulledCount++;
						query.orderByAscending(GlobalConstants.PARSE_KEY_EVENTSPECIAL);
						query.orderByAscending(GlobalConstants.PARSE_KEY_EVENTSTARTTIME);

						String date = "";

						List<ParseObject> objectsList = query.find();
						if (objectsList.size() < GlobalConstants.refreshEventsCount) {
							isEnd = true;
							mIsAllSearchEventLoaded = true;
						}
						for (ParseObject event : objectsList) {

							try {
								ListEventsModel map = new ListEventsModel();
								ListEventsModel map0 = new ListEventsModel();

								map.setObjectId(event.getObjectId());
								ParseFile picSmall = (ParseFile) event.get(GlobalConstants.PARSE_KEY_EVENTPICSMALL);
								map.setPicSmall(picSmall.getUrl());
								ParseFile picBig = (ParseFile) event.get(GlobalConstants.PARSE_KEY_EVENTPICBIG);
								map.setPicBig(picBig.getUrl());

								map.setEventName((String) event.get(GlobalConstants.PARSE_KEY_EVENTNAME));
								map.setEId(String.valueOf(event.getLong(GlobalConstants.PARSE_KEY_EVENTEID)));
								map.setHost((String) event.get(GlobalConstants.PARSE_KEY_HOST));
								map.setDescription((String) event.get(GlobalConstants.PARSE_KEY_EVENTDESCRIPTION));
								map.setEntrancePrice((String) event.get(GlobalConstants.PARSE_KEY_EVENTENTRANCEPRICE));
								map.setSpecial((Boolean) event.get(GlobalConstants.PARSE_KEY_EVENTSPECIAL));
								map.setFBLink((String) event.get(GlobalConstants.PARSE_KEY_FBLINK));
								map.setStreet((String) event.get(GlobalConstants.PARSE_KEY_STREET));
								map.setLatitude(event.getParseGeoPoint(GlobalConstants.PARSE_KEY_LOCATION).getLatitude());
								map.setLongitude(event.getParseGeoPoint(GlobalConstants.PARSE_KEY_LOCATION).getLongitude());

								SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy_H:mm");
								Date startDate = (Date) event.get(GlobalConstants.PARSE_KEY_EVENTSTARTTIME);
								if (startDate == null)
									continue;
								map.setStartTime(df.format(startDate));
								Date endDate = (Date) event.get(GlobalConstants.PARSE_KEY_EVENTENDTIME);
								if (endDate == null)
									continue;
								map.setEndTime(df.format(endDate));

								if (!date.equalsIgnoreCase(GlobalConstants.dateToString(startDate))) {
									date = GlobalConstants.dateToString(startDate);
									// GlobalConstants.mGroupKeyList.add(date);
									map0.setGroupName(date);
									GlobalConstants.mEventListForSearch.add(map0);

								}
								map.setGroupName(date + "xxx");
								GlobalConstants.mEventListForSearch.add(map);

							} catch (Exception e) {
								continue;
							}
						}
					}
				} else {
					ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Events");
					query.whereGreaterThan(GlobalConstants.PARSE_KEY_EVENTENDTIME, new Date());

					query.whereEqualTo("city_pick", mCityPick);
					query.setLimit(GlobalConstants.refreshEventsCount);
					query.setSkip(mPulledCount * GlobalConstants.refreshEventsCount);
					query.orderByAscending(GlobalConstants.PARSE_KEY_EVENTSPECIAL);
					query.orderByAscending(GlobalConstants.PARSE_KEY_EVENTSTARTTIME);

					String date = "";

					List<ParseObject> objectsList = query.find();
					if (objectsList.size() < GlobalConstants.refreshEventsCount) {
						mIsAllEventsLoaded = true;
					}
					for (ParseObject event : objectsList) {

						try {
							ListEventsModel map = new ListEventsModel();
							ListEventsModel map0 = new ListEventsModel();

							map.setObjectId(event.getObjectId());
							ParseFile picSmall = (ParseFile) event.get(GlobalConstants.PARSE_KEY_EVENTPICSMALL);
							map.setPicSmall(picSmall.getUrl());
							ParseFile picBig = (ParseFile) event.get(GlobalConstants.PARSE_KEY_EVENTPICBIG);
							map.setPicBig(picBig.getUrl());

							map.setEventName((String) event.get(GlobalConstants.PARSE_KEY_EVENTNAME));
							map.setEId(String.valueOf(event.getLong(GlobalConstants.PARSE_KEY_EVENTEID)));
							map.setHost((String) event.get(GlobalConstants.PARSE_KEY_HOST));
							map.setDescription((String) event.get(GlobalConstants.PARSE_KEY_EVENTDESCRIPTION));
							map.setEntrancePrice((String) event.get(GlobalConstants.PARSE_KEY_EVENTENTRANCEPRICE));
							map.setSpecial((Boolean) event.get(GlobalConstants.PARSE_KEY_EVENTSPECIAL));
							map.setFBLink((String) event.get(GlobalConstants.PARSE_KEY_FBLINK));
							map.setStreet((String) event.get(GlobalConstants.PARSE_KEY_STREET));
							map.setLatitude(event.getParseGeoPoint(GlobalConstants.PARSE_KEY_LOCATION).getLatitude());
							map.setLongitude(event.getParseGeoPoint(GlobalConstants.PARSE_KEY_LOCATION).getLongitude());

							SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy_H:mm");
							Date startDate = (Date) event.get(GlobalConstants.PARSE_KEY_EVENTSTARTTIME);
							if (startDate == null)
								continue;
							map.setStartTime(df.format(startDate));
							Date endDate = (Date) event.get(GlobalConstants.PARSE_KEY_EVENTENDTIME);
							if (endDate == null)
								continue;
							map.setEndTime(df.format(endDate));

							if (!date.equalsIgnoreCase(GlobalConstants.dateToString(startDate))) {
								date = GlobalConstants.dateToString(startDate);
								GlobalConstants.mGroupKeyList.add(date);
								map0.setGroupName(date);
								GlobalConstants.mEventsList.add(map0);

							}
							map.setGroupName(date + "xxx");
							GlobalConstants.mEventsList.add(map);

						} catch (Exception e) {
							continue;
						}
					}
				}

			} catch (ParseException e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}

			// try{
			//
			// // add Parse.com clubs to database.
			// mClubsList = new ArrayList<ListClubsModel>();
			//
			// // Locate the class table named "Country" in Parse.com
			// SharedPreferences pref =
			// PreferenceManager.getDefaultSharedPreferences(EventsActivity.this);
			// ParseQuery<ParseObject> query1 = new ParseQuery<ParseObject>(
			// "Clubs");
			// query1.whereEqualTo("city_pick",
			// pref.getString(GlobalConstants.PREF_CITY_PICK, ""));
			// query1.orderByAscending(GlobalConstants.PARSE_KEY_CLUBNAME);
			// // query1.whereEqualTo("city", GlobalConstants.mCityName );
			// mParseObject = query1.find();
			//
			// for (ParseObject club : mParseObject) {
			//
			// try{
			// ParseFile image = (ParseFile)
			// club.get(GlobalConstants.PARSE_KEY_CLUBTHUMBNAIL);
			//
			// ListClubsModel map = new ListClubsModel();
			// map.setClubName((String)
			// club.get(GlobalConstants.PARSE_KEY_CLUBNAME));
			// map.setThumbnail(image.getUrl());
			// map.setDescription((String)
			// club.get(GlobalConstants.PARSE_KEY_CLUBDESCRIPTION));
			// map.setStreet((String)
			// club.get(GlobalConstants.PARSE_KEY_CLUBSTREET));
			// map.setHost((String) club.get(GlobalConstants.PARSE_KEY_HOST));
			// map.setUrl((String) club.get(GlobalConstants.PARSE_KEY_CLUBURL));
			// map.setFacebook((String)
			// club.get(GlobalConstants.PARSE_KEY_CLUBFACEBOOK));
			// map.setPhoneNumber((String)
			// club.get(GlobalConstants.PARSE_KEY_CLUBPHONE));
			// ParseGeoPoint userLocation =
			// club.getParseGeoPoint(GlobalConstants.PARSE_KEY_CLUBLOCATION);
			// map.setLatitude(userLocation.getLatitude());
			// map.setLongtitude(userLocation.getLongitude());
			//
			// String strDist="Google location Service not allowed.";
			// if(GlobalConstants.currentLocation!=null)
			// {
			// double distance =
			// GlobalConstants.distanceBetween(location.getLatitude(),
			// location.getLongitude(),
			// userLocation.getLatitude(), userLocation.getLongitude())/1000.0f;
			// strDist = "Distanz :   " + String.format("%.2f", distance) +
			// " km";
			// }
			//
			// map.setDistance(strDist);
			// addClubs(map);
			// mClubsList.add(map);
			//
			// }catch(Exception e){
			// continue;
			// }
			// }
			//
			// } catch (ParseException e) {
			// Log.e("Error", e.getMessage());
			// e.printStackTrace();
			// }
			Log.v("%s", "fadsf");
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			// Close the progressdialog
			if (mProgressDialog != null)
				mProgressDialog.dismiss();

			if (!isFilterSet) {
				showEvents(false);
			}
			mPullRefreshGridView.onRefreshComplete();
		}
	}

	// public void addClubs(ListClubsModel model) {
	//
	// CityClubsTable clubs = new CityClubsTable();
	//
	// clubs.setName(model.getClubName());
	// clubs.setHost(model.getHost());
	// clubs.setDescription(model.getDescription());
	// clubs.setStreet(model.getStreet());
	// clubs.setThumbnail(model.getThumbnail());
	// clubs.setLatitude(model.getLatitude());
	// clubs.setLongitude(model.getLongtitude());
	//
	// DatabaseHelper db = new DatabaseHelper(mContext);
	// db.enterClubs(clubs);
	// }

}