package com.rainbowcard.client.utils;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rainbowcard.client.base.API;
import com.rainbowcard.client.base.Config;
import com.rainbowcard.client.common.utils.DLog;
import com.rainbowcard.client.model.SimpleResult;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.UUID;

public class UploadUtil {
	private static final String TAG = "uploadFile";
	private static final int TIME_OUT = 10 * 1000; // 超时时间
	private static final String CHARSET = "utf-8"; // 设置编码

	private static UploadUtil uploadUtil;

	private UploadUtil() {

	}

	public static boolean isDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri
				.getAuthority());
	}

	public static UploadUtil inst() {
		if (uploadUtil == null) {
			return new UploadUtil();
		}
		return uploadUtil;
	}

	/**
	 * android上传文件到服务器
	 * 
	 * @param file
	 *            需要上传的文件
	 * @param RequestURL
	 *            请求的rul
	 * @return 返回响应的内容
	 */
	public String uploadFile(File file, String RequestURL,String token,String uid) {
		String result = null;
		String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
		String PREFIX = "--", LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data"; // 内容类型
		try {
			URL url = new URL(RequestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setDoInput(true); // 允许输入流
			conn.setDoOutput(true); // 允许输出流
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST"); // 请求方式
			conn.setRequestProperty("appid", API.API_APPID);
			conn.setRequestProperty("secret", API.API_SECRET);
			conn.setRequestProperty("Authorization", token);
			conn.setRequestProperty("uid", uid);
			conn.setRequestProperty("Charset", CHARSET); // 设置编码
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
					+ BOUNDARY);
			String params = "uid=" + URLEncoder.encode(uid, "UTF-8");
//			OutputStream os = conn.getOutputStream();
//			os.write(params.getBytes());
//			os.flush();
			if (file != null) {
				/**
				 * 当文件不为空，把文件包装并且上传
				 */
				DataOutputStream dos = new DataOutputStream(
						conn.getOutputStream());
				StringBuilder sb = new StringBuilder();
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINE_END);
				/**
				 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
				 * filename是文件的名字，包含后缀名的 比如:abc.png
				 */

				Log.e(TAG, file.getName());
				Log.e(TAG, file.length() + "");
				sb.append("Content-Disposition: form-data; name=\"img\"; filename=\""
						+ file.getName() + "\"" + LINE_END);
				sb.append("Content-Type: application/octet-stream; charset="
						+ CHARSET + LINE_END);
				sb.append(LINE_END);
				dos.write(sb.toString().getBytes());
				InputStream is = new FileInputStream(file);
				byte[] bytes = new byte[1024];
				int len = 0;
				while ((len = is.read(bytes)) != -1) {
					dos.write(bytes, 0, len);
				}
				Log.e(TAG, "close");
				is.close();
				dos.write(LINE_END.getBytes());
				byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
						.getBytes();
				dos.write(end_data);
				dos.write(params.getBytes());
				dos.flush();
				/**
				 * 获取响应码 200=成功 当响应成功，获取响应的流
				 */
				int res = conn.getResponseCode();
				Log.e(TAG, "response code:" + res);
				InputStream input = conn.getInputStream();
				StringBuilder sb1 = new StringBuilder();
				int ss;
				while ((ss = input.read()) != -1) {
					sb1.append((char) ss);
				}
				result = sb1.toString();
				Log.e(TAG, "result : " + result);
			}
		} catch (MalformedURLException e) {
			DLog.e(Log.getStackTraceString(e));
		} catch (IOException e) {
			DLog.e(Log.getStackTraceString(e));
		} catch (Exception e) {
			DLog.e(Log.getStackTraceString(e));
		}
		return result;
	}

	public void upload(File file, String RequestURL,String token,String uid) {
		if (file.length() > Config.FILE_UPLOAD_MAX_SIZE) {
			UIUtils.toast("上传图片的大小不能超过200KB");
		} else {
			new uploadTask(file, RequestURL,token,uid).execute();
		}
	}

	private class uploadTask extends AsyncTask<String, Integer, String> {

		File file;
		String RequestURL;
		String token;
		String uid;

		public uploadTask(File file, String RequestURL,String token,String uid) {
			this.file = file;
			this.RequestURL = RequestURL;
			this.token = token;
			this.uid = uid;
		}

		@Override
		protected String doInBackground(String... parameter) {
			return uploadFile(file, RequestURL,token,uid);
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
		}

		@Override
		protected void onPostExecute(String result) {

			SimpleResult<HashMap<String, String>> resultMap = new Gson()
					.fromJson(
							result,
							new TypeToken<SimpleResult<HashMap<String, String>>>() {
							}.getType());
			String imgUrl = resultMap.getResult().get("data");

		}
	}
}