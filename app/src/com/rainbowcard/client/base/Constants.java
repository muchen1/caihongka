package com.rainbowcard.client.base;

/**
 * Created by gc on 16-44-13.
 */
public class Constants {
    public static final String  ACCRESSTOKEN  = "accresstoken";
    public static final String ACCESS_TOKEN = "access_token";

    public static final String USERINFO = "userinfo";
    public static final String UID = "uid";
    public static final String DEFAULT_NO = "default_no";
    public static final String SHOW_HELP = "show_help";
    public static final String HOT_SHOW = "hot_show";
    public static final String DOWNLOAD_APK_URL = "download_apk_url";
    public static final String IS_DOWNLOAD = "is_download";
    public static final String UPDATE_REMARKS = "update_remarks";
    public static final String KEY_IS_RB = "key_is_rb";

    public static final String USER_PHONE = "user_phone";

    public static final String PHONE = "phone";
    public static final String QINIUURL = "qiniuurl";
    public static final String NEWSURL = "newsurl";




    public static int CLIENT_TYPE = 2;  //`1`为ios, `2`为android
    public static int CONTINUESIGNDAY = 0;//没有连续签到

    //Btn的标识
    public static final int BTN_FLAG_MAP = 0x01;
    public static final int BTN_FLAG_GEREN = 0x01 << 1;
    public static final int BTN_FLAG_SET = 0x01 << 2;
    public static final int BTN_FLAG_LIST = 0x01 << 3;
    public static final int BTN_FLAG_OLYMPIC = 16;

    //Fragment的标识
    public static final String FRAGMENT_FLAG_MAP = "";
    public static final String FRAGMENT_FLAG_LIST = "";
    public static final String FRAGMENT_FLAG_GEREN = "彩虹卡";
    public static final String FRAGMENT_FLAG_SET = "个人中心";

    public final static String KEY_IMAGE_PATH = "key_image_path";

    public final static String KEY_IMAGE_URI = "key_image_uri";
    public final static String KEY_IMAGE_HEIGHT = "key_image_height";
    public final static String KEY_IMAGE_WIDTH = "key_image_width";
    public final static String KEY_RECHARGE_NO = "key_recharge_no";
    public final static String KEY_AMOUNT = "key_amount";
    public final static String KEY_RATE = "key_rate";
    public final static String KEY_EXCHANGE_SCORE = "key_exchange_score";
    public final static String KEY_PAY_STATUS = "key_pay_status";
    public final static String KEY_TENPAN = "key_tenpay";

    public final static String KEY_NAME = "key_name";
    public final static String KEY_CARD = "key_card";
    public final static String KEY_PHONE = "key_phone";
    public final static String KEY_PRICE = "key_price";
    public final static String KEY_REBATE = "key_rebate";
    public final static String KEY_ADDR = "key_addr";
    public final static String KEY_RAINBOW_TYPE = "key_rainbow_type";
    public final static String KEY_POIINFO = "key_poiinfo";
    public final static String KEY_ORDER_MODEL = "key_order_model";
    public final static String KEY_S = "S";//实体卡
    public final static String KEY_B = "B";//在线办卡
    public final static String KEY_X = "X";//冲虚拟卡
    public final static String KEY_TYPE = "key_type";
    public final static String KEY_CITY_ID = "key_city_id";
    public final static String KEY_LAT = "key_lat";
    public final static String KEY_LNG = "key_lng";
    public final static String KEY_IS_GOODS = "key_is_goods";
    public final static String KEY_IS_FREE = "key_is_free";
    public final static String KEY_FINANCE_ORDER_ENTITY = "key_finance_order_entity";

    public static final String KEY_DEFAULT_ACCOUNT = "key_default_account";
    public static final String KEY_USER_PHONE = "key_user_phone";

    public static final String KEY_SHOP_ORDER = "key_shop_order";
    public static final String KEY_SHOP_IMG = "key_shop_img";
    public static final String KEY_SHOP_ID = "key_shop_id";
    public static final String KEY_GOODS_ID = "key_goods_id";
    public static final String KEY_SHOP_NAME = "key_shop_name";
    public static final String KEY_SERVICE_TYPE = "key_service_type";
    public static final String KEY_RB_USER = "key_rb_user";
    public static final String KEY_SERVICE_NAME = "key_service_name";
    public static final String KEY_SON_SERVICE_TYPE = "key_son_service_type";
    public static final String KEY_SERVICE_MONEY = "key_service_money";
    public static final String KEY_ACTIVITY_MONEY = "key_activity_money";
    public static final String KEY_TRADE_NO = "key_trade_no";
    public static final String KEY_COUPON_USED = "key_coupon_used";
    public static final String KEY_COUPON_UN_USED = "key_coupon_un_used";
    public static final String KEY_COUPON_EXPIRE = "key_coupon_expire";
    public static final String KEY_CITYS = "key_citys";
    public static final String KEY_ENTRANCE = "key_entrance";
    public static final String KEY_RECHARGE_OR_CARD = "key_recharge_or_card";
    public static final String KEY_AGREEMENT_TYPE = "key_agreement_type";
    public static final String KEY_IS_DISCOUNT = "key_is_discount";
    public static final String KEY_IS_REMITTANCE_STATUS = "key_is_remittance_status";
    public static final String KEY_IS_RECORD = "key_is_record";
    public static final String KEY_IS_RETURN_FREE_WACH_ORDER = "key_is_return_free_wash_order";   //判断充值后是否返回免费洗车券下单  为1时返回

    public static final String KEY_IS_CERTIFICATION = "key_is_ertification";

    public static final String KEY_IS_PERSONAL = "key_is_personal";

    public static final String KEY_URL = "key_url";
    public static final String KEY_CONTENT = "key_content";

    public static final String KEY_PROVINCE = "key_province";
    public static final String KEY_CITY = "key_city";
    public static final String KEY_BANNERS = "key_banners";

    public static final String KEY_PIC_URLS = "key_pic_urls";
    public static final String KEY_PIC_POS = "key_pic_pos";

    public final static String KEY_SIGN = "key_sign";

    public final static String KEY_SHOW_TYPE = "key_show_type";

    public final static String KEY_USABLE_DISCOUNT = "key_usable_discount";
    public final static String KEY_DISCOUNT_ENTITY = "key_discount_entity";
    public final static String KEY_IS_SHOW = "key_is_show";
    public final static String KEY_IS_TYPE = "key_is_type";
    public final static String KEY_CITYENTITY = "key_cityentity";
    public final static String KEY_IS_COMMENT = "key_is_comment";

    public final static int RAINBOW_RECHARGE = 0;  //彩虹卡充值
    public final static int RAINBOW_RECHARGE_ACCOUNT = 2;  //默认账户充值
    public final static int RAINBOW_BUY = 1;  //彩虹卡购买
    public final static int REQUEST_MAIN = 10000;
    public final static int REQUEST_KEYWORD = 10001;
    public final static int REQUEST_SET_USER = 10002;
    public final static int REQUEST_CHANGE_PHONE = 10003;
    public final static int REQUEST_USABLE_DISCOUNT = 10004;
    public final static int REQUEST_PAY = 10005;
    public final static int REQUEST_CITY = 10006;
    public final static int REQUEST_ADDRESS = 10007;
    public final static int REQUEST_INTEGRAL_EXCHANGE = 10008;
    public final static int REQUEST_COMMENT = 10009;
    public final static int REQUEST_ORDER = 10010;

    public final static String EXTRA_BUNDLE = "MainActivity";
    public final static String STATR_TYPE = "start_type";
    public final static String MESSAGE_TYPE = "message_type";

    public static int IS_CHIEFLY = 1;  //1为默认地址
    public final static String KEY_ADDRESS_ENTITY = "key_address_entity";

    public final static String KEY_GOODS_ENTITY = "key_goods_entity";


    //pay
    public final static int REQUEST_ALIPAY_WAP = 10003;

    public final static String KEY_ALIPAY_URL = "key_alipay_url";

    public static final String SHARE_APP_TAG = "share_app_tag";
    public final static String ISFIRSTOPENAPP = "is_first_open_app";

    public final static String SP_SCREEN_AD = "sp_screen_ad";
    public final static String SP_SCREEN_DATE = "sp_screen_date";
    public final static String SP_SCREEN_COUNT = "sp_screen_count";



    public static final String DESCRIPTOR = "com.umeng.share";
    public final static String APP_ID = "wxa02cab04e7402a38";
    public final static String APP_SECRET = "10364447cf32f99371f1b2467c043d1e";
    public final static String QQ_APP_ID = "1105858542";
    public final static String QQ_APP_KEY = "YUcSZAbGhdIK7iZe";


}
