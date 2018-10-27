package com.rainbowcard.client.base;

import android.app.DialogFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.rainbowcard.client.common.exvolley.btw.BtwVolley;
import com.rainbowcard.client.common.exvolley.ex.ExRequestBuilder;
import com.rainbowcard.client.common.exvolley.ex.ExVolley;
import com.rainbowcard.client.utils.MaterialUIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gc on 14-8-5.
 */
public class MyBaseDialogFragment extends DialogFragment {
    private int mTAG;
    private Boolean mIsFirstLoad = false;
    private List<AsyncTask> mAsyncTasks;
    private MaterialUIUtils mUIUtils;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTAG = hashCode();
        mAsyncTasks = new ArrayList<AsyncTask>();
        mUIUtils = new MaterialUIUtils(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!mIsFirstLoad) {
            onFirstLoad();
            mIsFirstLoad = true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUIUtils.dismissLoading();
        ExVolley.with(getActivity()).cancel(mTAG);
        cancelAllAsyncTask();
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

    public void onFirstLoad() {

    }

    public ExRequestBuilder withVolley() {
        ExRequestBuilder builder = ExVolley.with(getActivity());
        builder.setTag(mTAG);
        return builder;
    }

    public BtwVolley withBtwVolley() {
        return BtwVolley.with(getActivity(), withVolley());
    }

    @Deprecated
    public  MaterialUIUtils getUIUtils() {
        return mUIUtils;
    }

    abstract class SmartAsyncTask<A, B, C> extends AsyncTask<A, B, C> {

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
}
