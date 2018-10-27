package com.rainbowcard.client.base;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.rainbowcard.client.ui.MainActivity;
import com.rainbowcard.client.ui.fragment.PersonalFragment;
import com.rainbowcard.client.utils.MaterialUIUtils;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.UpdateManger;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by gc on 14-7-22.
 */
public class MyBasicFragment extends Fragment {
    protected int mTAG;
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

//    @Override
//    public void onResume() {
//        super.onResume();
//        if(MyConfig.getSharePreBoolean(getActivity(), Constants.USERINFO, Constants.IS_DOWNLOAD)){
//            new UpdateManger(getActivity(),MyConfig.getSharePreStr(getActivity(),Constants.USERINFO,Constants.DOWNLOAD_APK_URL)).
//                    showConfirmDialog("温馨提示",MyConfig.getSharePreStr(getActivity(),Constants.USERINFO,Constants.UPDATE_REMARKS));
//        }
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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

    //刷新Token
    public void refreshToken(){
        withBtwVolley().load(API.API_REFRESH_TOKEN)
                .method(Request.Method.POST)
                .setHeader("Authorization",String.format(getString(R.string.token),MyConfig.getSharePreStr(getActivity(), Constants.USERINFO, Constants.UID)))
                .setHeader("Accept", API.VERSION)
                .setUIComponent(getActivity())
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
                        MyConfig.putSharePre(getActivity(), Constants.USERINFO, Constants.UID, resp.data.token);

                    }

                    @Override
                    public void onBtwError(BtwRespError<TokenModel> error) {
                        Toast.makeText(getActivity(), error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(getActivity(), R.string.network_error,
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
                .setHeader("Authorization",String.format(getString(R.string.token),MyConfig.getSharePreStr(getActivity(), Constants.USERINFO, Constants.UID)))
                .setHeader("Accept", API.VERSION)
                .setUIComponent(getActivity())
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
                        MyConfig.putSharePre(getActivity(), Constants.USERINFO, Constants.UID, resp.data.token);
                        switch (classType){
                            case 1: // 我的卡片回调
                                Log.d("GCCCCCC>>>???<<<<","我的卡片");
//                                PersonalFragment.newInstance().getUserInfo();
//                                new PersonalFragment().getUserInfo();
                                break;
                            case 2:

                                break;
                        }
                    }

                    @Override
                    public void onBtwError(BtwRespError<TokenModel> error) {
                        Toast.makeText(getActivity(), error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(getActivity(), R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {

                    }
                }).excute(TokenModel.class);
    }

    @Deprecated
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
}
