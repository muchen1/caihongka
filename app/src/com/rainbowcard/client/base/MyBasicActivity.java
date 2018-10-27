package com.rainbowcard.client.base;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;


import com.rainbowcard.client.common.exvolley.btw.BtwVolley;
import com.rainbowcard.client.common.exvolley.ex.ExRequestBuilder;
import com.rainbowcard.client.common.exvolley.ex.ExVolley;
import com.rainbowcard.client.utils.MaterialUIUtils;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.UpdateManger;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by gc on 14-7-21.
 * TODO login判断及逻辑
 */
public class MyBasicActivity extends BasicActivity{

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
//        if(MyConfig.getSharePreBoolean(MyBasicActivity.this, Constants.USERINFO, Constants.IS_DOWNLOAD)){
//            new UpdateManger(MyBasicActivity.this,MyConfig.getSharePreStr(MyBasicActivity.this,Constants.USERINFO,Constants.DOWNLOAD_APK_URL)).
//                    showConfirmDialog("温馨提示",MyConfig.getSharePreStr(MyBasicActivity.this,Constants.USERINFO,Constants.UPDATE_REMARKS));
//        }
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
