package com.rainbowcard.client.base;

/***
 * 接口地址
 * **/

public class API extends ApiRelease {

	public final static int API_VERSION = 1;
	public final static String ENCRYPT_KEY = "caihong_9527_Key";
	public final static String API_TYPE = "android";
	public final static String API_APPID = "10000";
	public final static String API_KEY = "Ca1iH3ong4Ke6y5";
	public final static String API_PARAMETER_KEY = "85tnBTNVAR5k";
	public final static String API_SECRET = "PPiYtOUXsGhZA4La";
	public final static String VERSION = "application/vnd.caihongka.v2.0.0+json";
	public final static String VERSION2D1 = "application/vnd.caihongka.v2.1.0+json";
	public final static String VERSION2D11 = "application/vnd.caihongka.v2.1.1+json";
	public final static int API_CLIENT_ID = 2;

	//彩虹卡余额查询
	public final static String API_RAINBOW_QUERY = API_HOST + "card/balance";

	//彩虹卡信息查询
	public final static String API_RAINBOW_INFO = API_HOST + "card/base";

	//获取商户列表
	public final static String API_GET_CARD_STORE = API_HOST + "card/store";

	//检查更新
	public final static String API_CHECK_UPDATE = API_HOST + "version/update";

	//充值创建订单
	public final static String API_GET_RAINBOW_CARD = API_HOST + "card";

	//在线购卡创建订单
	public final static String API_RAINBOW_RECHARGE = API_HOST + "order/buy-card";
	//充值实体卡创建订单
	public final static String API_RAINBOWCARD_RECHARGE = API_HOST + "order/recharge/s";

	//充值账户
	public final static String API_RECHARGE_ACCOUNT = API_HOST + "order/recharge/x";

	//支付宝支付
	public final static String API_RAINBOW_ALI_PAY = API_HOST + "pay/alipay";

	//微信支付
	public final static String API_RAINBOW_WX_PAY = API_HOST + "pay/wxpay";

	//第三方登录
	public final static String API_AUTO_LOGIN = API_HOST + "v1/users/auto-login";

	//问题反馈
	public final static String API_COMMIT_FEEDBACK = API_HOST + "propose";
	//获取签到详情界面
	public final static String API_GET_SIGN_DETAIL = API_HOST + "v1/user/sign";
	//签到
	public final static String API_USER_SIGH = API_HOST + "v1/user/sign";


	//获取地图洗车列表
	public final static String API_GET_AMOUNT_LIST = API_HOST + "shop/map/list";

	//获取洗车商户列表
	public final static String API_GET_SHOP_LIST = API_HOST + "shop/list";

	//获取城市下的区域
	public final static String API_GET_AREA = API_HOST + "shop/area";

	//获取商户详情
	public final static String API_GET_SHOP_INFO = API_HOST + "shop/info";

	//获取违章查询所有城市
	public final static String API_GET_ILLEGAL_CHECK_ALL_CITY = API_HOST + "wz/all-city";

	//获取默认查询城市
	public final static String API_GET_DEFAULT_CHECK_CITY = API_HOST + "wz/default-city";

	//违章查询详情
	public final static String API_GET_CHECK_DETAIL = API_HOST + "wz/query";

	//违章查询-已查询的详情
	public final static String API_GET_SEARCH_RESULT = API_HOST + "wz/info/";

	public final static String API_LOGOUT = API_HOST + "logout";

	//获取注册短信验证码
	public final static String API_GET_REGIST_SMS = API_HOST + "register/sms";
	//注册
	public final static String API_REGIST = API_HOST + "register";

	//获取用户信息
	public final static String API_GET_USER_INFO = API_HOST + "user/me";

	//获取用户充值金额
	public final static String API_GET_USER_MONEY = API_HOST + "user/money";

	//设置默认账户
	public final static String API_SET_DEFAULT_ACCOUNT = API_HOST + "user/account";

	//刷新token
	public final static String API_REFRESH_TOKEN = API_HOST + "token/refresh";

	//获取短信登陆短信码
	public final static String API_GET_LOGIN_SMS = API_HOST + "login/sms";

	//获取修改手机短信码
	public final static String API_GET_CHANGE_PHONE_CODE = API_HOST + "user/phone/sms";

	//获取修改密码短信码
	public final static String API_GET_CHANGE_PASSWORD_CODE = API_HOST + "user/password/sms";

	//获取找回密码短信码
	public final static String API_GET_FIND_PASSWORD_CODE = API_HOST + "forget/pwd/sms";

	//找回密码
	public final static String API_FIND_PASSWORD = API_HOST + "forget/pwd";

	//修改密码
	public final static String API_CHANGE_PASSWORD = API_HOST + "user/password";

	//修改手机号
	public final static String API_CHANGE_PHONE = API_HOST + "user/phone";

	//短信登陆
	public final static String API_SMS_LOGIN = API_HOST + "login/by-sms";
	//密码登陆
	public final static String API_PASSWORD_LOGIN = API_HOST + "login/by-pwd";

	//获取banner
	public final static String API_GET_BANNER = API_HOST + "init/banner";
	//获取插屏
	public final static String API_GET_POPUP_WINDOWS = API_HOST + "init/ad";
	//获取限行公告
	public final static String API_GET_NOTICE = API_HOST  + "init/notice";
	//获取推荐商户
	public final static String API_GET_RECOMMEND = API_HOST + "recommend/shop";

	//绑定彩虹卡
	public final static String API_BIND_CARD = API_HOST + "card/bind";

	//解除绑定
	public final static String API_UNBIND_CARD = API_HOST + "user/card/delete/";

	//获取我的卡片
	public final static String API_GET_MY_CARD = API_HOST + "user/card";


	//获取可充值账户的金额
	public final static String API_GET_RECHARGE_PRICE = API_HOST + "init/recharge";

	//获取我的消费记录
	public final static String API_GET_MY_PAYMENT = API_HOST + "user/payment";
	//获取消费记录详情
	public final static String API_GET_PAYMENT_DETAIL = API_HOST + "payment/info";
	//获取充值订单
	public final static String API_GET_MY_RECHARGE_ORDER = API_HOST + "user/recharge";
	//获取可用优惠券
	public final static String API_GET_MY_USABLE_DISCUNT = API_HOST + "coupon/use";
	//获取优惠券
	public final static String API_GET_MY_DISCUNT = API_HOST + "user/coupon";
	//添加优惠券
	public final static String API_ADD_DISCOUNT = API_HOST + "coupon/bind";
	//获取充值记录
	public final static String API_GET_USER_RECHARGE = API_HOST + "user/recharge";
	//获取充值记录详情
	public final static String API_GET_USER_RECHARGE_DETAIL = API_HOST + "order/info/";
	//删除充值记录
	public final static String API_DELETE_ORDER = API_HOST + "order/delete/";
	//删除消费记录
	public final static String API_DELETE_PAYMENT = API_HOST + "payment/delete/";
	//提交评论
	public final static String API_SUBMIT_USER_COMMENT = API_HOST + "comment";
	//获取商户评论
	public final static String API_GET_SHOP_COMMENT_LIST = API_HOST + "comment";

	//购买数字码
	public final static String API_PAYMENT_CODE = API_HOST + "payment/code";

	//免费洗车券下单
	public final static String API_FREE_PAYMENT_CODE = API_HOST + "payment/code/finance";

	//支付消费订单
	public final static String API_PAYMENT_PAY = API_HOST + "payment/pay";

	//获取数字码详情
	public final static String API_GET_PAYMENT_INFO = API_HOST + "payment/info";

	//获取城市列表
	public final static String API_GET_CITY_LIST = API_HOST + "shop/city";
	//获取热门城市列表
	public final static String API_GET_HOT_CITY_LIST = API_HOST + "shop/city/hot";
	//收藏商户
	public final static String API_COLLECT_SHOP = API_HOST + "user/collect/shop";
	//删除收藏商户
	public final static String API_DELETE_COLLECT_SHOP = API_HOST + "user/collect/shop/delete";
	//获取收藏商户列表
	public final static String API_GET_COLLECT_LIST = API_HOST + "user/collect/shop";

	//获取单播信息
	public final static String API_GET_MESSAGE_ALONE = API_HOST + "unicast";
	//获取广播信息
	public final static String API_GET_MESSAGE_ALL = API_HOST + "broadcast";
	//提交设备token给服务端
	public final static String API_SUB_DEVICE_TOKEN = API_HOST + "push/token";

	//获取地址列表
	public final static String API_GET_ADDRESS_LIST = API_HOST + "user/address";

	//设置默认地址
	public final static String API_SET_DEFAULT_ADDRESS = API_HOST + "user/address/default/";

	//删除地址
	public final static String API_DEL_ADDRESS = API_HOST + "user/address/delete/";

	//添加地址
	public final static String API_ADD_ADDRESS = API_HOST + "user/address";

	//编辑地址
	public final static String API_EDIT_ADDRESS = API_HOST + "user/address/edit/";

	//我的积分
	public final static String API_GET_MY_INTEGRAL = API_HOST + "my/integral";

	//获取积分Banner
	public final static String API_GET_INTEGRAL_BANNER = API_HOST + "integral/banner";
	//获取热门商品列表
	public final static String API_GET_HOT_GOODS_LIST = API_HOST + "store/goods/hot";
	//获取商品详情
	public final static String API_GET_GOODS_DETAIL = API_HOST + "store/goods/";
	//兑换商品
	public final static String API_EXCHANGE_GOODS = API_HOST + "goods/order";
	//获取可兑换商品列表
	public final static String API_GET_GOODS_LIST = API_HOST + "store/goods";
	//获取兑换记录
	public final static String API_GET_EXCHANGE_LIST = API_HOST + "goods/order";
	//获取兑换订单详情
	public final static String API_GET_EXCHANGE_DETAIL = API_HOST + "goods/order/";


	//理财

	//检查用户实名验证
	public final static String API_QUERY_CERTIFICATION = API_HOST + "init/certification";
	//提交银行汇款信息
	public final static String API_SUBMIT_REMITTANCE_INFO = API_HOST + "pay/remit/money";
	//获取银行列表
	public final static String API_GET_BANK_LIST = API_HOST + "init/bank/category";
	//实名认证
	public final static String API_USER_CERTIFICATION = API_HOST + "user/certification";

	//修改银行卡
	public final static String API_MODIFICATION_BANK_CARD = API_HOST + "user/bank/card/";

	//获取可用券、可提金额等基本信息
	public final static String API_GET_USER_BASE = API_HOST + "user/base";
	//出售免洗券
	public final static String API_SELL_COUPON = API_HOST + "finance/sell/coupon";
	//获取金融计算工式
	public final static String API_GET_FORMULA = API_HOST + "init/finance/formula";
	//理财下单
	public final static String API_FINANCE_ORDER = API_HOST + "finance/order";
	//理财支付
	public final static String API_FINANCE_PAY = API_HOST + "finance/pay";

	//获取券流水
	public final static String API_GET_BLOTTER_LIST = API_HOST + "finance/blotter";

	//获取朋友参与获得券
	public final static String API_GET_INVITE_TICKET = API_HOST + "finance/friend/participate";

	//获取被邀请人列表
	public final static String API_GET_INVITER_LIST = API_HOST + "user/invite/info";

	//获取资金明细
	public final static String API_GET_MONEY_BLOTTER = API_HOST + "user/money/blotter";

	//获取冻结中的券
	public final static String API_GET_LOOK_LIST = API_HOST + "finance/coupon/lock";
	//获取购买记录
	public final static String API_GET_FINANCE_RECORD_LIST = API_HOST + "finance/record";

	//获取用户银行信息
	public final static String API_GET_USER_BANK_INFO = API_HOST + "user/bank/info";

	//获取用户实名信息
	public final static String API_GET_USER_AUTONYM_INFO = API_HOST + "user/certification";

	//获取提现详情
	public final static String API_GET_WASH_INFO = API_HOST + "finance/cash/info";
	//提现
	public final static String API_SUBMIT_CASH = API_HOST + "finance/cash";
	//获取提现记录
	public final static String API_GET_WITHDRAWAL_RECORD_LIST = API_HOST + "finance/cash/record";

	//获取邀请滚动条
	public final static String API_GET_INVITE_HEADLINE = API_HOST + "user/free/coupon";
	//获得用户邀请人娄
	public final static String API_GET_INVITE_NUMBER = API_HOST + "user/invite/number";




	public static String getUrl(String url, Object... obj) {
		return String.format(url, obj);
	}

	// ==================  微信登录  ===================
	public final static String API_GET_WECHAT_ACCESS_TOKEN = API_WX_HOST + "oauth2/access_token";

}