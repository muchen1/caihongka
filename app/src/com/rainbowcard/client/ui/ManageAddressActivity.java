package com.rainbowcard.client.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.rainbowcard.client.R;
import com.rainbowcard.client.base.API;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.base.MyBaseActivity;
import com.rainbowcard.client.common.exvolley.btw.BtwRespError;
import com.rainbowcard.client.common.exvolley.btw.BtwVolley;
import com.rainbowcard.client.common.exvolley.utils.VolleyUtils;
import com.rainbowcard.client.model.AddressEntity;
import com.rainbowcard.client.model.AddressModel;
import com.rainbowcard.client.ui.adapter.AddressListAdapter;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.widget.HeadControlPanel;
import com.rainbowcard.client.widget.LoadingFrameLayout;
import com.rainbowcard.client.widget.swipemenulistview.SwipeMenu;
import com.rainbowcard.client.widget.swipemenulistview.SwipeMenuCreator;
import com.rainbowcard.client.widget.swipemenulistview.SwipeMenuItem;
import com.rainbowcard.client.widget.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2017/12/6.
 */
public class ManageAddressActivity extends MyBaseActivity implements AddressListAdapter.SetAddressListener{

    @InjectView(R.id.head_layout)
    HeadControlPanel mHeadControlPanel;
    @InjectView(R.id.nav_back)
    RelativeLayout backBtn;
    @InjectView(R.id.v_container)
    LoadingFrameLayout mFlLoading;
    @InjectView(R.id.listview)
    SwipeMenuListView listView;

    @InjectView(R.id.no_addr)
    RelativeLayout noAddrLayout;
    @InjectView(R.id.addr_btn)
    TextView addrBtn;
    @InjectView(R.id.add_btn)
    TextView addBtn;

    AddressListAdapter adapter;
    List<AddressEntity> mAddressEntitys = new ArrayList<AddressEntity>();
    private String token;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_manage);
        ButterKnife.inject(this);
        UIUtils.setMiuiStatusBarDarkMode(ManageAddressActivity.this,true);
        UIUtils.setMeizuStatusBarDarkIcon(ManageAddressActivity.this,true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
        load();
    }

    private void initView(){

        mHeadControlPanel.initHeadPanel();
        mHeadControlPanel.setMiddleTitle(getString(R.string.manage_address));
        mHeadControlPanel.mMidleTitle.setTextColor(getResources().getColor(R.color.title_text));
        mHeadControlPanel.setMyBackgroundColor(Color.rgb(249,249,249));
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageAddressActivity.this, EditAddressActivity.class);
                startActivity(intent);
            }
        });
        addrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageAddressActivity.this, EditAddressActivity.class);
                startActivity(intent);
            }
        });

        adapter = new AddressListAdapter(this,this);
        listView.setAdapter(adapter);

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(60));
                // set item title
                deleteItem.setTitle(getString(R.string.delete));
                // set item title fontsize
                deleteItem.setTitleSize(18);
                // set item title font color
                deleteItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        // set creator
        listView.setMenuCreator(creator);

        // step 2. listener item click event
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                delAddress(mAddressEntitys.get(position).id);
                return false;
            }
        });

        // set SwipeListener
        listView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {
            @Override
            public void onSwipeStart(int position) {
                // swipe start
            }
            @Override
            public void onSwipeEnd(int position) {
                // swipe end
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra(Constants.KEY_ADDRESS_ENTITY,mAddressEntitys.get(position));
                setResult(Constants.REQUEST_INTEGRAL_EXCHANGE, intent);
                finish();
            }
        });
        loadMore(false);

        mFlLoading.setRetryButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load();
            }
        });
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    void loadMore(boolean toLoad) {
        if (toLoad) {
            listView
                    .setOnScrollToRefreshListener(new SwipeMenuListView.OnScrollToRefreshListener() {
                        @Override
                        public void onScrollToFooter() {
                            load();
                        }
                    });
        } else {
            listView.setOnScrollToRefreshListener(new SwipeMenuListView.OnScrollToRefreshListener() {
                @Override
                public void onScrollToFooter() {
                    listView.refreshComplete();
                }
            });
        }
    }

    void load(){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(ManageAddressActivity.this, Constants.USERINFO, Constants.UID));
        withBtwVolley().load(API.API_GET_ADDRESS_LIST)
                .method(Request.Method.GET)
                .setHeader("Authorization", token)
                .setHeader("Accept", API.VERSION)
                .setRetrys(0)
                .setUIComponent(this)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<AddressModel>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();;
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(AddressModel resp) {
                        mFlLoading.showLoadingView(false);
                        Log.d("GCCCCCCC??", "address" + resp);
                        if(resp.data != null && !resp.data.isEmpty()) {
                            adapter.setAddresseList(resp.data);
                            mAddressEntitys.clear();
                            mAddressEntitys.addAll(resp.data);

//                            if (resp.data.size() < 5) {
//                                listView.refreshComplete();
//                            }
                            noAddrLayout.setVisibility(View.GONE);
                            addBtn.setVisibility(View.VISIBLE);
                        }else {
                            noAddrLayout.setVisibility(View.VISIBLE);
                            addBtn.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onBtwError(BtwRespError<AddressModel> error) {
                        mFlLoading.showError(error.errorMessage);
                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        mFlLoading.showError(getString(R.string.network_error));
                    }

                    @Override
                    public void onRefreToken() {

                    }
                }).excute(AddressModel.class);

    }

    void setDefault(int arid){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(ManageAddressActivity.this, Constants.USERINFO, Constants.UID));
        withBtwVolley().load(String.format(getString(R.string.url),API.API_SET_DEFAULT_ADDRESS,arid))
                .method(Request.Method.POST)
                .setHeader("Authorization", token)
                .setHeader("Accept", API.VERSION)
                .setRetrys(0)
                .setUIComponent(this)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<String>() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onResponse(String resp) {
                        load();
                        Toast.makeText(ManageAddressActivity.this, R.string.set_default_success,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onBtwError(BtwRespError<String> error) {
                        Toast.makeText(ManageAddressActivity.this, error.errorMessage,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(ManageAddressActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {

                    }
                }).excute();
    }

    @Override
    public void setDefaultAddr(int id) {
        setDefault(id);
    }

    @Override
    public void delAddr(int id) {
//        delAddress(id);
        showDialog("温馨提示","是否确认删除该地址？",id);
    }

    void delAddress(int arid){
        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(ManageAddressActivity.this, Constants.USERINFO, Constants.UID));
        withBtwVolley().load(String.format(getString(R.string.url),API.API_DEL_ADDRESS,arid))
                .method(Request.Method.POST)
                .setHeader("Authorization", token)
                .setHeader("Accept", API.VERSION)
                .setRetrys(0)
                .setUIComponent(this)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<String>() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onResponse(String resp) {
                        load();
                        Toast.makeText(ManageAddressActivity.this, R.string.delete_success,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onBtwError(BtwRespError<String> error) {
                        Toast.makeText(ManageAddressActivity.this, error.errorMessage,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(ManageAddressActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {

                    }
                }).excute();
    }

    private void showDialog(String title, String tip, final int arid) {
        final Dialog dialog = new Dialog(ManageAddressActivity.this,R.style.loading_dialog);
        dialog.setContentView(R.layout.ui_ios_alert_dialog);
        Window dialogWindow = dialog.getWindow();
        Display display = getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = -80;
        lp.width = (int)(display.getWidth() * 0.90);
        dialogWindow.setAttributes(lp);

        TextView alertTitle = (TextView) dialog.findViewById(R.id.alert_title);
        TextView alertTip = (TextView) dialog.findViewById(R.id.alert_tip);
        TextView alertOk = (TextView) dialog.findViewById(R.id.alert_ok);
        TextView alertCancle = (TextView) dialog.findViewById(R.id.alert_cancel);
        alertTitle.setText(title);
        alertTip.setText(tip);
//        alertOk.setText("继续");
//        alertCancle.setText("取消");
        dialog.findViewById(R.id.alert_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delAddress(arid);
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.alert_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
