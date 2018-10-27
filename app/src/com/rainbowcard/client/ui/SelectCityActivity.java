package com.rainbowcard.client.ui;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.Request;
import com.rainbowcard.client.R;
import com.rainbowcard.client.base.API;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.common.city.CityEntity;
import com.rainbowcard.client.common.city.LocateEntity;
import com.rainbowcard.client.common.exvolley.btw.BtwRespError;
import com.rainbowcard.client.common.exvolley.btw.BtwVolley;
import com.rainbowcard.client.common.exvolley.utils.VolleyUtils;
import com.rainbowcard.client.model.HotCity;
import com.rainbowcard.client.model.ShopModel;
import com.rainbowcard.client.ui.adapter.SelectCityAdapter;
import com.rainbowcard.client.utils.LocateManager;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.HeadControlPanel;
import com.rainbowcard.client.widget.IndexSideBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import se.emilsjolander.stickylistheaders.ExpandableStickyListHeadersListView;

/**
 * Created by gc on 17-3-14.
 */
public class SelectCityActivity extends MyBaseActivity {

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;

    @InjectView(R.id.v_search)
    SearchView mSearchView;
    @InjectView(R.id.lv_cinema)
    ExpandableStickyListHeadersListView mLvCinema;
    @InjectView(R.id.v_sider)
    IndexSideBar mIndexSideBar;
    @InjectView(R.id.tv_index)
    TextView mTvIndex;

    private List<CityEntity> mHistoryList;
    private LocateManager mLocateManager;
    private SelectCityAdapter mAdapter;
    CityEntity currentPosition;

    private EventBus mEventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_select_city);
        currentPosition = (CityEntity) getIntent().getSerializableExtra("cityEntity");
        ButterKnife.inject(this);


        mLocateManager = LocateManager.getInstance(this);
        mAdapter = new SelectCityAdapter(this);

        int presentCityId = getIntent().getIntExtra(Constants.KEY_CITY_ID, 0);
        mAdapter.setPresentCityId(presentCityId);

        mEventBus = EventBus.getDefault();

        initView();
        load();
    }

    private void initView() {

        mHeadControlPanel.initHeadPanel();
        mHeadControlPanel.setMiddleTitle(getString(R.string.select_city));
        mHeadControlPanel.mMidleTitle.setTextColor(getResources().getColor(R.color.title_text));
        mHeadControlPanel.setMyBackgroundColor(Color.rgb(249,249,249));
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mLvCinema.setAdapter(mAdapter);
        mLvCinema.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CityEntity city = (CityEntity) mAdapter.getItem((int) id);
                if(city != null) {
                    if (city.id == 0) {
                        return;
                    }

                    mAdapter.setPresentCityId(city.id);
                }
                Intent intent = new Intent();
                intent.putExtra(Constants.KEY_CITYENTITY, city);
                setResult(Constants.REQUEST_MAIN, intent);
                finish();
            }
        });
        int id = mSearchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) mSearchView.findViewById(id);
        textView.setTextColor(getResources().getColor(R.color.gray));
        mSearchView.onActionViewExpanded();
        mSearchView.setIconified(false);
        mSearchView.clearFocus();
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                if (TextUtils.isEmpty(mSearchView.getQuery())) {
                    mSearchView.clearFocus();
                    return true;
                }
                return false;
            }
        });
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    mAdapter.clearSearch();
                    mIndexSideBar.setVisibility(View.VISIBLE);
                } else {
                    mAdapter.search(newText);
                    mLvCinema.setSelection(0);
                    mIndexSideBar.setVisibility(View.INVISIBLE);
                }
                return false;
            }
        });


        mIndexSideBar.setOnIndexListener(new IndexSideBar.OnIndexListener() {
            @Override
            public void onSelect(int pos, String index) {
                switch (pos){
                    case 0:
                        mTvIndex.setText(index);
                        mLvCinema.setSelection(0);
                        break;
                    case 1:
                        mTvIndex.setText(index);
                        mLvCinema.setSelection(1);
                        break;
//                    case 2:
//                        mTvIndex.setText(index);
//                        mLvCinema.setSelection(mHistoryList.size()+1);
//                        break;
                    default:
                        mTvIndex.setText(index);
                        int indexPos = mAdapter.findCitySectionByIndexChar(index);
                        if (indexPos == 0) {
                            return;
                        }
                        mLvCinema.setSelection(indexPos);
                        break;
                }
            }

            @Override
            public void onTouchDown() {
                mTvIndex.setAlpha(1f);
                mTvIndex.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTouchUp() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    mTvIndex.animate().setDuration(500)
                            .alpha(0f)
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    mTvIndex.setVisibility(View.GONE);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            })
                            .start();
                } else {
                    mTvIndex.setVisibility(View.GONE);
                }
            }
        });
    }

    private void load() {
//        loadAllCity();
//        loadPresentCity();
        loadHotCity();
        loadTotalCity();
//        loadHistory();
    }

    private void loadAllCity() {
        new SmartAsyncTask<Void, Void, List<CityEntity>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected List<CityEntity> doInBackground(Void... params) {
                return mLocateManager.getLocationCityList();
            }

            @Override
            protected void onPostExecute(List<CityEntity> cityEntities) {
                super.onPostExecute(cityEntities);
                mAdapter.setAllCityList(cityEntities);
            }
        }.execute();
    }

    private void loadPresentCity() {
        mLocateManager.locate(new LocateManager.OnLocateListener() {
            @Override
            public void onLocate(LocateEntity locateEntity) {
                if (!SelectCityActivity.this.isFinishing()) {
                    List<CityEntity> cityEntities = new ArrayList<CityEntity>(1);
                    cityEntities.add(locateEntity.city);
                    mAdapter.setPresentCity(cityEntities);
                }
            }
        });
    }
    private void loadCurrentPosition(final List<CityEntity> list) {
        mLocateManager.locate(new LocateManager.OnLocateListener() {
            @Override
            public void onLocate(LocateEntity locateEntity) {
                if (!SelectCityActivity.this.isFinishing()) {
                    List<CityEntity> cityEntities = new ArrayList<CityEntity>(1);
                    CityEntity cityEntity = new CityEntity();
                    cityEntity.provinceId = locateEntity.city.provinceId;
                    cityEntity.baiduId = locateEntity.city.baiduId;
                    for (CityEntity mCityEntity : list) {{
                        if(locateEntity.city.name.contains(mCityEntity.name)){
                            cityEntity.id = mCityEntity.id;
                        }
                    }}
                    cityEntity.latitude = locateEntity.city.latitude;
                    cityEntity.longitude = locateEntity.city.longitude;
                    cityEntity.name = locateEntity.city.name.replace("市","");
                    cityEntities.add(cityEntity);
                    mAdapter.setCurrentPosition(cityEntities);
                }
            }
        });
//                if (!HotelSelectCityActivity.this.isFinishing()) {
//                    List<CityEntity> cityEntities = new ArrayList<CityEntity>(1);
//                    cityEntities.add(currentPosition);
//                    mAdapter.setCurrentPosition(cityEntities);
//                }
    }

    //获取全部城市列表
    private void loadTotalCity() {
            withBtwVolley().load(API.API_GET_CITY_LIST)
                    .setHeader("Accept", API.VERSION)
                    .method(Request.Method.GET)
                    .setRetrys(0)
                    .setUIComponent(this)
                    .setTimeout(20000)
                    .setResponseHandler(new BtwVolley.ResponseHandler<HotCity>() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onFinish() {

                        }

                        @Override
                        public void onResponse(HotCity resp) {
                            loadCurrentPosition(resp.data);
                            mAdapter.setAllCityList(resp.data);
                        }

                        @Override
                        public void onBtwError(BtwRespError<HotCity> error) {
                            UIUtils.toast(error.errorMessage);
                        }

                        @Override
                        public void onNetworkError(VolleyUtils.NetworkError error) {
                            UIUtils.toast(getString(R.string.network_error));
                        }

                        @Override
                        public void onRefreToken() {

                        }
                    }).excute(HotCity.class);
    }
    //获取热门城市列表
    private void loadHotCity() {
            withBtwVolley().load(API.API_GET_HOT_CITY_LIST)
                    .setHeader("Accept", API.VERSION)
                    .method(Request.Method.GET)
                    .setRetrys(0)
                    .setUIComponent(this)
                    .setTimeout(20000)
                    .setResponseHandler(new BtwVolley.ResponseHandler<HotCity>() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onFinish() {

                        }

                        @Override
                        public void onResponse(HotCity resp) {

                            mAdapter.setHotCityList(resp.data);
                        }

                        @Override
                        public void onBtwError(BtwRespError<HotCity> error) {
                            UIUtils.toast(error.errorMessage);
                        }

                        @Override
                        public void onNetworkError(VolleyUtils.NetworkError error) {
                            UIUtils.toast(getString(R.string.network_error));
                        }

                        @Override
                        public void onRefreToken() {

                        }
                    }).excute(HotCity.class);
    }

}
