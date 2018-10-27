package com.rainbowcard.client.base;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;


import com.android.volley.Request;
import com.rainbowcard.client.R;
import com.rainbowcard.client.common.exvolley.btw.BtwRespError;
import com.rainbowcard.client.common.exvolley.btw.BtwVolley;
import com.rainbowcard.client.common.exvolley.ex.ExRequestBuilder;
import com.rainbowcard.client.common.exvolley.ex.ExVolley;
import com.rainbowcard.client.common.exvolley.utils.VolleyUtils;
import com.rainbowcard.client.model.TokenModel;
import com.rainbowcard.client.ui.CardActivity;
import com.rainbowcard.client.utils.MaterialUIUtils;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.UpdateManger;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by kleist on 14-7-21.
 * TODO login判断及逻辑
 */
public class MyBaseActivity extends BaseActivity{

    protected int mTAG;
    private List<AsyncTask> mAsyncTasks;

    private MaterialUIUtils mUIUtils;

    private Boolean mIsFirstLoad = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        YHApplication.instance().addActivity(this);
        mTAG = hashCode();
        mAsyncTasks = new ArrayList<AsyncTask>();
        mUIUtils = new MaterialUIUtils(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsFirstLoad = false;
        if(MyConfig.getSharePreBoolean(MyBaseActivity.this, Constants.USERINFO, Constants.IS_DOWNLOAD)){
            new UpdateManger(MyBaseActivity.this,MyConfig.getSharePreStr(MyBaseActivity.this,Constants.USERINFO,Constants.DOWNLOAD_APK_URL)).
                    showConfirmDialog("温馨提示",MyConfig.getSharePreStr(MyBaseActivity.this,Constants.USERINFO,Constants.UPDATE_REMARKS));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUIUtils.dismissLoading();
        ExVolley.with(this).cancel(mTAG);
        cancelAllAsyncTask();
    }

    public ExRequestBuilder withVolley() {
        ExRequestBuilder builder = ExVolley.with(this);
        builder.setTag(mTAG);
        return builder;
    }

    public BtwVolley withBtwVolley() {
        BtwVolley volley = BtwVolley.with(this, withVolley());
        return volley;
    }

    /**
     * Cancel all asyncTask that registered in activity
     */
    public void cancelAllAsyncTask() {
        List<AsyncTask> taskList = new ArrayList<AsyncTask>(mAsyncTasks);
        for (AsyncTask task : taskList) {
            task.cancel(true);
        }
    }

    //刷新Token
    public void refreshToken(){
        withBtwVolley().load(API.API_REFRESH_TOKEN)
                .method(Request.Method.POST)
                .setHeader("Authorization",String.format(getString(R.string.token),MyConfig.getSharePreStr(MyBaseActivity.this, Constants.USERINFO, Constants.UID)))
                .setHeader("Accept", API.VERSION)
                .setUIComponent(MyBaseActivity.this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<TokenModel>() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onFinish() {
                    }

                    @Override
                    public void onResponse(TokenModel resp) {
                        MyConfig.putSharePre(MyBaseActivity.this, Constants.USERINFO, Constants.UID, resp.data.token);
                    }

                    @Override
                    public void onBtwError(BtwRespError<TokenModel> error) {
                        Toast.makeText(MyBaseActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(MyBaseActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {

                    }
                }).excute(TokenModel.class);
    }
    public void refreshToken(final int classType){
        withBtwVolley().load(API.API_REFRESH_TOKEN)
                .method(Request.Method.POST)
                .setHeader("Authorization",String.format(getString(R.string.token),MyConfig.getSharePreStr(MyBaseActivity.this, Constants.USERINFO, Constants.UID)))
                .setHeader("Accept", API.VERSION)
                .setUIComponent(MyBaseActivity.this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<TokenModel>() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onFinish() {
                    }

                    @Override
                    public void onResponse(TokenModel resp) {
                        MyConfig.putSharePre(MyBaseActivity.this, Constants.USERINFO, Constants.UID, resp.data.token);
                        switch (classType){
                            case 1: // 我的卡片回调
                                CardActivity.getInstance().getMyCard();
                                break;
                            case 2:

                                break;
                        }
                    }

                    @Override
                    public void onBtwError(BtwRespError<TokenModel> error) {
                        Toast.makeText(MyBaseActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(MyBaseActivity.this, R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {

                    }
                }).excute(TokenModel.class);
    }

    public MaterialUIUtils getUIUtils() {
        return mUIUtils;
    }

    protected abstract class SmartAsyncTask<A, B, C> extends AsyncTask<A, B, C> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mAsyncTasks.add(this);
        }
        @Override
        protected void onCancelled() {
            super.onCancelled();
            mAsyncTasks.remove(this);
        }

    }

    protected Boolean isFirstLoad() {
        return mIsFirstLoad;
    }
}
