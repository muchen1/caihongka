package com.rainbowcard.client.common.exvolley.btw;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.google.gson.Gson;
import com.rainbowcard.client.R;
import com.rainbowcard.client.base.API;
import com.rainbowcard.client.base.YHApplication;
import com.rainbowcard.client.common.exvolley.ex.ExRequest;
import com.rainbowcard.client.common.exvolley.ex.ExRequestBuilder;
import com.rainbowcard.client.common.exvolley.ex.ExVolley;
import com.rainbowcard.client.common.exvolley.utils.VolleyUtils;
import com.rainbowcard.client.common.utils.DLog;
import com.rainbowcard.client.model.UserModel;
import com.rainbowcard.client.ui.LoginActivity;
import com.rainbowcard.client.utils.PhoneSignUtil;
import com.rainbowcard.client.utils.UIUtils;

import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by kleist on 14-8-16.
 */
public class BtwVolley {

    private ExRequestBuilder mExRequestBuilder;
    private ResponseHandler mResponseHandler;
    private Gson mGson;
    private Fragment mFragment;
    private static Activity mActivity;
    private Boolean mIsSetFragment = false;
    private Boolean mIsSetAcivity = false;
    private int mVersion = API.API_VERSION;
    private String mAppid = API.API_APPID;
    private String mSecret = API.API_SECRET;
    private String mUrl;
    private Context mContext;

    /**
     * Don't use this !!!
     * @param context
     */
    private BtwVolley(Context context) {
        this(context, ExVolley.with(context));
    }

    /**
     * Don't use this !!!
     * @param context
     * @param builder
     */
    private BtwVolley(Context context, ExRequestBuilder builder) {
        mContext = context;
        mGson = new Gson();
        mExRequestBuilder = builder;
    }

    public static BtwVolley with(Context context) {
        BtwVolley btwVolley = new BtwVolley(context);
        return btwVolley;
    }

    public static BtwVolley with(Context context, ExRequestBuilder builder) {
        return new BtwVolley(context, builder);
    }


    /**
     * set url
     * @param url
     * @return ExRequestBuilder
     */
    public BtwVolley load(String url) {
        mUrl = url;
        mExRequestBuilder.load(url);
        return this;
    }

    /**
     * set request method: GET POST PUT DELETE
     * @param method
     * @return ExRequestBuilder
     */
    public BtwVolley method(int method) {
        mExRequestBuilder.method(method);
        return this;
    }

    /**
     * set request param
     * if request method is GET or DELETE, it will parse to url param
     * if request method is POST or PUT, it will parse to body param
     * @param key
     * @param value
     * @return ExRequestBuilder
     */
    public BtwVolley setParam(String key, Object value) {
        mExRequestBuilder.setParam(key, value);
        return this;
    }

    /**
     * set request params
     * if request method is GET or DELETE, they will parse to url params
     * if request method is POST or PUT, they will parse to body params
     * @param formMap
     * @return ExRequestBuilder
     */
    public BtwVolley setParams(Map<String, String> formMap) {
        mExRequestBuilder.setParams(formMap);
        return this;
    }

    public BtwVolley setJsonBody(JSONObject jsonBody) {
        mExRequestBuilder.setJsonBody(jsonBody);
        return this;
    }

    public BtwVolley setRequestBody(String requestBody) {
        mExRequestBuilder.setRequestBody(requestBody);
        return this;
    }

    public BtwVolley setHeader(String key, String value) {
        mExRequestBuilder.setHeader(key, value);
        return this;
    }

    public BtwVolley setContentType(String contentType) {
        mExRequestBuilder.setContentType(contentType);
        return this;
    }

    public BtwVolley asJsonRequest() {
        mExRequestBuilder.asJsonRequest();
        return this;
    }

    public BtwVolley shouldCache(Boolean shouldCache) {
        mExRequestBuilder.shouldCache(shouldCache);
        return this;
    }

    public BtwVolley setVersion(int version) {
        mVersion = version;
        return this;
    }

    /**
     *
     * @param timeout The timeout for the policy.
     * @return
     */
    public BtwVolley setTimeout(int timeout) {
        mExRequestBuilder.setTimeout(timeout);
        return this;
    }

    public BtwVolley setRetrys(int retryNum) {
        mExRequestBuilder.setRetrys(retryNum);
        return this;
    }

    public BtwVolley setBackoffMultiplier(float backoffMult) {
        mExRequestBuilder.setBackoffMultiplier(backoffMult);
        return this;
    }

    public BtwVolley setTag(Object tag) {
        mExRequestBuilder.setTag(tag);
        return this;
    }

    public BtwVolley setUIComponent(Activity activity) {
        mIsSetAcivity = true;
        mActivity = activity;
        return this;
    }

    public BtwVolley setUIComponent(Fragment fragment) {
        mIsSetFragment = true;
        mFragment = fragment;
        return this;
    }

    public BtwVolley setResponseHandler(ResponseHandler<?> handler) {
        mResponseHandler = handler;
        return this;
    }
    public BtwVolley setResponseHandler(){
        return this;
    }

    public ExRequest excute() {
        if (mResponseHandler == null) {
            throw  new NullPointerException("need btw listener");
        }


//        mExRequestBuilder.setParam("version", mVersion);
//        mExRequestBuilder.setParam("device","android");
//        mExRequestBuilder.setHeader("appid",mAppid);
//        mExRequestBuilder.setHeader("secret", mSecret);
//        mExRequestBuilder.setHeader("Content-Type", "application/json; charset=utf-8");
//        mExRequestBuilder.setHeader("Accept", "application/vnd.caihongka.v1.0.0+json");
        mExRequestBuilder.setHeader("Client",String.valueOf(API.API_CLIENT_ID));
        mExRequestBuilder.setHeader("Version",YHApplication.instance().getVersionName());
        mExRequestBuilder.setHeader("DeviceId", PhoneSignUtil.getDeviceId(mContext));
        mExRequestBuilder.setTimeout(20000);
        mExRequestBuilder.response(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!isUIComponentAlive()) {
                    return;
                }
                handleResponse(response);
            }
        }, String.class);

        mExRequestBuilder.error(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!isUIComponentAlive()) {
                    return;
                }
                deliverNetworkError(error);
                mResponseHandler.onFinish();
            }
        });

        if (isUIComponentAlive()) {
            mResponseHandler.onStart();
        }

        return mExRequestBuilder.excute();
    }

    public ExRequest excute(int timeout) {
        if (mResponseHandler == null) {
            throw new NullPointerException("need btw listener");
        }


//        mExRequestBuilder.setParam("version", mVersion);
//        mExRequestBuilder.setParam("device","android");
//        mExRequestBuilder.setHeader("appid",mAppid);
//        mExRequestBuilder.setHeader("secret", mSecret);
//        mExRequestBuilder.setHeader("Content-Type", "application/json; charset=utf-8");
//        mExRequestBuilder.setHeader("Accept", "application/vnd.caihongka.v1.0.0+json");
        mExRequestBuilder.setHeader("Client", String.valueOf(API.API_CLIENT_ID));
        mExRequestBuilder.setHeader("Version", YHApplication.instance().getVersionName());
        mExRequestBuilder.setHeader("DeviceId", PhoneSignUtil.getDeviceId(mContext));
        mExRequestBuilder.setTimeout(timeout);
        mExRequestBuilder.response(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!isUIComponentAlive()) {
                    return;
                }
                handleResponse(response);
            }
        }, String.class);

        mExRequestBuilder.error(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!isUIComponentAlive()) {
                    return;
                }
                deliverNetworkError(error);
                mResponseHandler.onFinish();
            }
        });

        if (isUIComponentAlive()) {
            mResponseHandler.onStart();
        }

        return mExRequestBuilder.excute();
    }

    public ExRequest excute(final Class raw) {
        if (mResponseHandler == null) {
            throw  new NullPointerException("need btw listener");
        }

//        mExRequestBuilder.setParam("version", mVersion);
//        mExRequestBuilder.setParam("device","android");
//        mExRequestBuilder.setHeader("appid",mAppid);
//        mExRequestBuilder.setHeader("Accept", "application/vnd.caihongka.v2.0.0+json");
        mExRequestBuilder.setHeader("Client",String.valueOf(API.API_CLIENT_ID));
        mExRequestBuilder.setHeader("Version",YHApplication.instance().getVersionName());
        mExRequestBuilder.setHeader("DeviceId", PhoneSignUtil.getDeviceId(mContext));
        mExRequestBuilder.setTimeout(20000);
        mExRequestBuilder.response(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!isUIComponentAlive()) {
                    return;
                }
                handleResponse(response,raw);
            }
        }, String.class);

        mExRequestBuilder.error(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!isUIComponentAlive()) {
                    return;
                }
                deliverNetworkError(error);
                mResponseHandler.onFinish();
            }
        });

        if (isUIComponentAlive()) {
            mResponseHandler.onStart();
        }

        return mExRequestBuilder.excute();
    }

    private void handleResponse(String response) {
        DLog.i("handle response:" + response);
        BtwRespModel respModel;

        try {
            respModel = parseResponse(response);
        } catch (Exception e) {
            DLog.e(Log.getStackTraceString(e));
            VolleyLog.e(e.toString());
            deliverNetworkError(new VolleyError());
            return;
        }

        if (respModel == null) {
            deliverNetworkError(null);
            return;
        }

        switch (respModel.errorCode) {
            case 200:
                deliverResult(response);
//                deliverResult(mGson.fromJson(response, type(raw, mResponseHandler.type)));
                break;
            case 401:
                DLog.i("unlogin" + mUrl+"bbbbbb"+respModel.errorCode);
//                mResponseHandler.login();
                mResponseHandler.showDialog();
                break;
            case 403:
                mResponseHandler.logout(respModel,mContext);
                break;
            case 432:
                mResponseHandler.onRefreToken();
                break;
            case 431:
                //强制更新
                mResponseHandler.updateAPK(respModel.errorMessage);
                break;
            default:
                deliverBtwError(respModel);
        }

        if (isUIComponentAlive()) {
            mResponseHandler.onFinish();
        }
    }
    private void handleResponse(String response,final Class raw) {
        DLog.i("handle response2:" + response);
        BtwRespModel respModel;

        try {
            respModel = parseResponse(response);
        } catch (Exception e) {
            DLog.e(Log.getStackTraceString(e));
            VolleyLog.e(e.toString());
            deliverNetworkError(new VolleyError());
            return;
        }

        if (respModel == null) {
            deliverNetworkError(null);
            return;
        }

        switch (respModel.errorCode) {
            case 0:
//                deliverResult(respModel.result);
                deliverResult(mGson.fromJson(response, type(raw, mResponseHandler.type)));
                break;
            case 200:
                deliverResult(mGson.fromJson(response, type(raw, mResponseHandler.type)));
                break;
            case 401:
                DLog.i("unlogin" + mUrl+"bbbbbb"+respModel.errorCode);
//                mResponseHandler.login();
                mResponseHandler.showDialog();
                break;
            case 403:
                mResponseHandler.logout(respModel,mContext);
                break;
            case 432:
                mResponseHandler.onRefreToken();
                break;
            default:
                deliverBtwError(respModel);
        }

        if (isUIComponentAlive()) {
            mResponseHandler.onFinish();
        }
    }

    private Boolean isUIComponentAlive() {
        if (mIsSetAcivity && (mActivity == null || mActivity.isFinishing())) {
            return false;
        }

        if (mIsSetFragment && (mFragment == null || !mFragment.isAdded())) {
            return false;
        }

        return true;
    }

    private BtwRespModel parseResponse(String response) {
        Type objType = type(BtwRespModel.class, mResponseHandler.type);
        return mGson.fromJson(response, objType);
    }



    private static ParameterizedType type(final Class raw, final Type... args) {
        return new ParameterizedType() {
            public Type getRawType() {
                return raw;
            }

            public Type[] getActualTypeArguments() {
                return args;
            }

            public Type getOwnerType() {
                return null;
            }
        };
    }

    private void deliverResult(Object result) {
        mResponseHandler.onResponse(result);
    }

    private void refreToken(){
        mResponseHandler.onRefreToken();
    }

    private void deliverBtwError(BtwRespModel model) {
        BtwRespError error = new BtwRespError();
        error.errorCode = model.errorCode;
        error.errorMessage = model.errorMessage;
        error.result = model.result;
        error.isNetworlError = false;
        mResponseHandler.onBtwError(error);
    }

    private void deliverNetworkError(VolleyError volleyError) {
        BtwRespError error = new BtwRespError();
        error.isNetworlError = true;
        VolleyUtils.NetworkError responseError;
        if (volleyError == null) {
            responseError = new VolleyUtils.NetworkError();
            responseError.code = 0;
            responseError.message = "error response";

        } else {
            responseError = VolleyUtils.parseError(volleyError);
        }
        mResponseHandler.onNetworkError(responseError);
    }



    public static abstract class ResponseHandler<T> {
        public Type type;

        public ResponseHandler() {
            Type type1 = getClass().getGenericSuperclass();
            type = ((ParameterizedType) type1).getActualTypeArguments()[0];
        }

        public abstract void onStart();

        public abstract void onFinish();

        public abstract void onResponse(T resp);

        public abstract void onBtwError(BtwRespError<T> error);

        public abstract void onNetworkError(VolleyUtils.NetworkError error);
        public abstract void onRefreToken();


        private void showDialog() {
            final Dialog dialog = new Dialog(mActivity, R.style.loading_dialog);
            dialog.setContentView(R.layout.ui_ios_alert_dialog);
            Window dialogWindow = dialog.getWindow();
            Display display = mActivity.getWindowManager().getDefaultDisplay();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.y = -80;
            lp.width = (int)(display.getWidth() * 0.90);
            dialogWindow.setAttributes(lp);

            TextView alertTitle = (TextView) dialog.findViewById(R.id.alert_title);
            TextView alertTip = (TextView) dialog.findViewById(R.id.alert_tip);
            TextView alertOk = (TextView) dialog.findViewById(R.id.alert_ok);
            TextView alertCancle = (TextView) dialog.findViewById(R.id.alert_cancel);
            ImageView line = (ImageView) dialog.findViewById(R.id.line);

            line.setVisibility(View.GONE);
            alertCancle.setText("我知道啦");
            alertCancle.setVisibility(View.GONE);
            alertOk.setText("去登陆");
            alertTitle.setText("温馨提示");
            alertTip.setText("登陆已失效，请重新登陆");
            dialog.findViewById(R.id.alert_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    login();
                    dialog.dismiss();
                }
            });
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        public void login() {
//            LoginControl.getInstance(YHApplication.instance()).logout();
            Intent loginIntent = new Intent(YHApplication.instance(), LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            YHApplication.instance().startActivity(loginIntent);
        }

        public void updateAPK(String hint){



        }
        public void logout(BtwRespModel model,Context mContext){
            UIUtils.showDialog("警告",model.errorMessage, (Activity) mContext);
        }
    }

}
