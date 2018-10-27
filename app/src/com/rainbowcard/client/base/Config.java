package com.rainbowcard.client.base;

import android.os.Environment;

import java.io.File;

public class Config {
    public static final long TS_MAX = 253402271999000L;// 最大有限期
    public static final int COMPRESS_MAX_X = 600;
    public static final int COMPRESS_MAX_Y = 600;
    public static final int FILE_UPLOAD_MAX_SIZE = 200*1024;

    public static final String BACK_APP_URL = "http://backapp";
    // 商城每页数据条数
    public static final int MALL_LIST_PAGE_SIZE = 10;
    public static final int ORDER_LIST_PAGE_SIZE = 10;
    public static final String BAIDU_SDK_KEY = "FC56546ff4776a28a258943e11811742";
    public static final int LOCATION_OVERTIME = 2 * 60 * 1000;// 两分钟
    public static final int RESULT_TAG_TAN_FGM = 19;

    public static final String APP_NAME = "betterwood";
    public static String EXTERNAL_DIR = getExternalStoragePath() + File.separator + APP_NAME;
    public static String TEMP_PHOTO = EXTERNAL_DIR + File.separator + "temp.jpg";
    public static String TEMP_CACHE = EXTERNAL_DIR + File.separator + "cache";
    public static final boolean DEBUG = true;
    public static final boolean PERSISTLOG = true;
    public static final boolean OPEN_BAIDU_MAP = true;  //是否开启百度地图

    static{
        if(!new File(EXTERNAL_DIR).isDirectory()){
            new File(EXTERNAL_DIR).mkdirs();
        }
        if(!new File(TEMP_CACHE).isDirectory()){
            new File(TEMP_CACHE).mkdirs();
        }
    }

    public static File getExternalStorageDir() {
        return Environment.getExternalStorageDirectory();
    }

    /**
     * get the external storage file path
     *
     * @return the file path
     */
    public static String getExternalStoragePath() {
        return getExternalStorageDir().getAbsolutePath();
    }


    public static final class MEB_ORDER_TYPE {
        public static final int ALL = 0;// '所有类型都显示',
        public static final int OPEN_ACCOUNT = 1;// '开户',
        public static final int POINT_CONVERSION = 2;// '第三方积分兑换百达币',
        public static final int POINT_REDEMPTION = 3;// '百达币兑换商品',
        public static final int POINT_EARNING = 4;// '赚百达币',
        public static final int POINT_REWARD = 5;// '奖励百达币'
    }

    public static final class MEB_ORDER_STATUS {
        public static final int NEW = 1;// '新订单，未支付',
        public static final int FINISHED = 2;// '订单已完成',
        public static final int FAILED = 3;// '订单失败',
        public static final int CANCELLED = 4;// '订单已取消'
    }

}