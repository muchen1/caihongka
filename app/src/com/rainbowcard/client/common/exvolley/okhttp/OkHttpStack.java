package com.rainbowcard.client.common.exvolley.okhttp;

import android.util.Log;

import com.android.volley.toolbox.HurlStack;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;
import com.rainbowcard.client.common.utils.DLog;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


/**
 * Created by gc on 14-1-6.
 */
public class OkHttpStack extends HurlStack {

    private final OkUrlFactory mOkUrlFactory;

    public OkHttpStack() {
        mOkUrlFactory = new OkUrlFactory(buildHttpClient());
    }

    private OkHttpClient buildHttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient();
//周全代码
        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            X509Certificate[] myTrustedAnchors = new X509Certificate[0];
                            return myTrustedAnchors;
                        }
                    }
            }, new SecureRandom());
        } catch (GeneralSecurityException e) {
            DLog.e(Log.getStackTraceString(e));
            throw new AssertionError();
        }


        okHttpClient.setSslSocketFactory(sslContext.getSocketFactory());
        okHttpClient.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        return okHttpClient;
    }

    @Override
    protected HttpURLConnection createConnection(URL url) throws IOException {
//        HttpURLConnection httpURLConnection = null;
//        String hostName = url.getHost();
//        if (hostName.equals("m.betterwood.com")) {
//            url = new URL(url.toString().replace(hostName, "42.121.4.223"));
//            DLog.i("url = " + url);
//            DLog.i("replace url = " + url);
//            httpURLConnection = mOkUrlFactory.open(url);
//            httpURLConnection.setRequestProperty("Host", hostName);
//        } else {
//            httpURLConnection = mOkUrlFactory.open(url);
//        }
//        return httpURLConnection;
//wzg代码
//        if(url.toString().contains("https")) {
//            DLog.i("证书验证－－－－－－－－－－－－");
//            NukeSSLCerts.nuke();
//        }
//        return (HttpURLConnection)url.openConnection();
//周全代码
        return mOkUrlFactory.open(url);

    }

}
