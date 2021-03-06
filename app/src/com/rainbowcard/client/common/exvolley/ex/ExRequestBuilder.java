package com.rainbowcard.client.common.exvolley.ex;

import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.rainbowcard.client.common.exvolley.utils.UrlUtils;
import com.rainbowcard.client.common.utils.DLog;
import com.rainbowcard.client.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by gc on 14-1-6.
 */
public class ExRequestBuilder {

    public static final String PROTOCOL_CHARSET = "utf-8";
    public static final String CONTENT_TYPE_JSON = String.format("application/json; charset=%s", PROTOCOL_CHARSET);

    /** The default socket timeout in milliseconds */
    public static final int DEFAULT_TIMEOUT_MS = 5000;

    /** The default number of retries */
    public static final int DEFAULT_MAX_RETRIES = 1;

    /** The default backoff multiplier */
    public static final float DEFAULT_BACKOFF_MULT = 1f;

    private RequestQueue mRequestQueue;
    private int mMethod;
    private String mUrl;
    private SortedMap<Object, Object> mRequestParams;
    private Map<String, String> mHeaders;
    private String mRequestBody;

    private ExRequest mRequest;
    private Response.Listener mListener;
    private Response.ErrorListener mErrorListener;
    private Object mTag;
    private Class mResponseClass;
    private Boolean mShouldCache = false;
    private String mContentType = null;
    private int mTimeout = DEFAULT_TIMEOUT_MS;
    private int mRetryNum = DEFAULT_MAX_RETRIES;
    private float mBackoffMult = DEFAULT_BACKOFF_MULT;


    public ExRequestBuilder(RequestQueue requestQueue) {
        mRequestQueue = requestQueue;
        mTag = this.hashCode();
    }

    /**
     * set url
     * @param url
     * @return ExRequestBuilder
     */
    public ExRequestBuilder load(String url) {
        mUrl = url;
        return this;
    }

    /**
     * set request method: GET POST PUT DELETE
     * @param method
     * @return ExRequestBuilder
     */
    public ExRequestBuilder method(int method) {
        mMethod = method;
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
    public ExRequestBuilder setParam(String key, Object value) {
        if (mRequestParams == null) {
            mRequestParams = new TreeMap<Object, Object>();
        }
        mRequestParams.put(key, String.valueOf(value));
        return this;
    }

    /**
     * set request params
     * if request method is GET or DELETE, they will parse to url params
     * if request method is POST or PUT, they will parse to body params
     * @param formMap
     * @return ExRequestBuilder
     */
    public ExRequestBuilder setParams(Map<String, String> formMap) {
        if (mRequestParams == null) {
            mRequestParams = new TreeMap<Object, Object>();
        }
        mRequestParams.putAll(formMap);
        return this;
    }

    public ExRequestBuilder setJsonBody(JSONObject jsonBody) {
        mRequestBody = jsonBody == null ? null : jsonBody.toString();
        return this;
    }

    public ExRequestBuilder setRequestBody(String requestBody) {
        mRequestBody = requestBody;
        return this;
    }

    public ExRequestBuilder setHeader(String key, String value) {
        if (mHeaders == null) {
            mHeaders = new HashMap<String, String>();
        }
        mHeaders.put(key, value);
        return this;
    }

    public ExRequestBuilder setContentType(String contentType) {
        mContentType = contentType;
        return this;
    }

    public ExRequestBuilder asJsonRequest() {
        mContentType = CONTENT_TYPE_JSON;
        return this;
    }

    public ExRequestBuilder shouldCache(Boolean shouldCache) {
        mShouldCache = shouldCache;
        return this;
    }

    // TODO 待到jdk8时自动判断泛型type
    public ExRequestBuilder setResponseListener(Response.Listener<?> listener, Class responseClass) {
        mListener = listener;
        mResponseClass = responseClass;
        return this;
    }

    public ExRequestBuilder setErrorListener(Response.ErrorListener errorListener) {

        mErrorListener = errorListener;
        return this;
    }

    public ExRequestBuilder response(Response.Listener<?> listener, Class mResponseClass) {
        return setResponseListener(listener, mResponseClass);
    }

    public ExRequestBuilder error(Response.ErrorListener errorListener) {
        return setErrorListener(errorListener);
    }


    public ExRequestBuilder setTag(Object tag) {
        mTag = tag;
        return this;
    }

    /**
     *
     * @param timeout The timeout for the policy.
     * @return
     */
    public ExRequestBuilder setTimeout(int timeout) {
        mTimeout = timeout;
        return this;
    }

    public ExRequestBuilder setRetrys(int retryNum) {
        mRetryNum = retryNum;
        return this;
    }

    public ExRequestBuilder setBackoffMultiplier(float backoffMult) {
        mBackoffMult = backoffMult;
        return this;
    }

    public void cancel() {
        mRequestQueue.cancelAll(mTag);
    }

    public void cancel(Object tag) {
        mRequestQueue.cancelAll(tag);
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    /**
     * run
     * @return
     */
    public ExRequest excute() {
//        Log.d("GCCCCCCC","kulekule>>>>>>>>>>>>>>>>>>>>>>>>>");
        if (mRequestParams != null && !mRequestParams.isEmpty()) {
            switch (mMethod) {
                case Method.GET:
                case Method.DELETE:
                    mUrl = UrlUtils.UrlBuilder(mUrl, mRequestParams);
                    break;
                case Method.POST:
                case Method.PUT:
                    break;
            }
        }
        DLog.i(mUrl);

        if (mResponseClass.equals(String.class) ) {
            mRequest = new ExRequest<String>(mMethod, mUrl, mListener, mErrorListener, mResponseClass);
        } else if (mResponseClass.equals(JSONObject.class)) {
            mRequest = new ExRequest<JSONObject>(mMethod, mUrl, mListener, mErrorListener, mResponseClass);
        } else if (mResponseClass.equals(JSONArray.class)) {
            mRequest = new ExRequest<JSONArray>(mMethod, mUrl, mListener, mErrorListener, mResponseClass);
        } else {
            mRequest = new ExRequest(mMethod, mUrl, mListener, mErrorListener, mResponseClass);
        }

        mRequest.setTag(mTag);
        mRequest.setShouldCache(mShouldCache);
        mRequest.setContentType(mContentType);
        mRequest.setHeader(mHeaders);
//        if (mRequestParams != null && !mRequestParams.isEmpty()) {
//            mRequestParams.put("paramSign",StringUtil.createParameterSign(mRequestParams));
//        }
        mRequest.setParams(mRequestParams);
        mRequest.setRequestBody(mRequestBody);
        mRequest.setRetryPolicy(new DefaultRetryPolicy(mTimeout, mRetryNum, mBackoffMult));

        mRequestQueue.add(mRequest);

        return mRequest;
    }

    public ExRequest Excalibur() {
        return excute();
    }


}
