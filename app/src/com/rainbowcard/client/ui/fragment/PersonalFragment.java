package com.rainbowcard.client.ui.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.rainbowcard.client.R;
import com.rainbowcard.client.base.API;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.base.MyBaseFragment;
import com.rainbowcard.client.common.exvolley.btw.BtwRespError;
import com.rainbowcard.client.common.exvolley.btw.BtwVolley;
import com.rainbowcard.client.common.exvolley.utils.VolleyUtils;
import com.rainbowcard.client.model.TokenModel;
import com.rainbowcard.client.model.UserModel;
import com.rainbowcard.client.ui.AuthenticationActivity;
import com.rainbowcard.client.ui.BranchActivity;
import com.rainbowcard.client.ui.CapitalDetailActivity;
import com.rainbowcard.client.ui.CollectShopActivity;
import com.rainbowcard.client.ui.FreeWashTicketActivity;
import com.rainbowcard.client.ui.GuideActivity;
import com.rainbowcard.client.ui.IntegralActivity;
import com.rainbowcard.client.ui.IntegralShopActivity;
import com.rainbowcard.client.ui.InviteActivity;
import com.rainbowcard.client.ui.LoginActivity;
import com.rainbowcard.client.ui.MainActivity;
import com.rainbowcard.client.ui.ManageAddressActivity;
import com.rainbowcard.client.ui.MyAccountActivity;
import com.rainbowcard.client.ui.MyCardActivity;
import com.rainbowcard.client.ui.MyDiscountActivity;
import com.rainbowcard.client.ui.MyOrderActivity;
import com.rainbowcard.client.ui.MyWalletActivity;
import com.rainbowcard.client.ui.RechargeAccountActivity;
import com.rainbowcard.client.ui.RechargeOrderActivity;
import com.rainbowcard.client.ui.RechargeRainbowCardActivity;
import com.rainbowcard.client.ui.SettingActivity;
import com.rainbowcard.client.ui.SurprisedActivity;
import com.rainbowcard.client.ui.UserSetActivity;
import com.rainbowcard.client.ui.WithdrawDepositActivity;
import com.rainbowcard.client.utils.DensityUtil;
import com.rainbowcard.client.utils.LoginControl;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.PhoneSignUtil;
import com.rainbowcard.client.utils.UIUtils;
import com.rainbowcard.client.utils.UpdateManger;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.DecimalFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2017-1-9.
 */
public class PersonalFragment extends MyBaseFragment{

    @InjectView(R.id.card_logo)
    CircularImageView cardLogo;
    @InjectView(R.id.user_name)
    TextView userName;
    @InjectView(R.id.attestation_layout)
    RelativeLayout attestationLayout;
    @InjectView(R.id.attestation_text)
    TextView attestationTv;
    @InjectView(R.id.card_login_regist_rl)
    RelativeLayout loginRegistRl;
    @InjectView(R.id.name_layout)
    RelativeLayout nameLayout;
    @InjectView(R.id.account_layout)
    RelativeLayout accountLayout;
    @InjectView(R.id.balance)
    TextView balance;
    @InjectView(R.id.integral)
    TextView integral;
    @InjectView(R.id.card_layout)
    RelativeLayout cardLayout;
    @InjectView(R.id.order_layout)
    RelativeLayout orderLayout;
    @InjectView(R.id.store_layout)
    RelativeLayout storeLayout;
    @InjectView(R.id.addr_layout)
    RelativeLayout addrLayout;
    @InjectView(R.id.set_btn)
    TextView setBtn;
    @InjectView(R.id.discount)
    TextView discountTv;
    @InjectView(R.id.discount_layout)
    RelativeLayout discountLayout;
    @InjectView(R.id.recharge_layout)
    RelativeLayout rechargeLayout;
    @InjectView(R.id.withdraw_layout)
    RelativeLayout withdrawLayout;
    @InjectView(R.id.integral_layout)
    RelativeLayout integralLayout;
    @InjectView(R.id.recharge_record_layout)
    RelativeLayout rechargeRecordLayout;
    @InjectView(R.id.collect_layout)
    RelativeLayout collectLayout;
    @InjectView(R.id.free_layout)
    RelativeLayout freeLayout;
    @InjectView(R.id.share_layout)
    RelativeLayout shareLayout;
    @InjectView(R.id.capital_layout)
    RelativeLayout capitalLayout;
    @InjectView(R.id.help_icon)
    ImageView helpIcon;
    @InjectView(R.id.new_show)
    ImageView newShow;
    @InjectView(R.id.hot_show)
    ImageView hotShow;

    @InjectView(R.id.arrow_icon)
    ImageView arrowIcon;

    @InjectView(R.id.shop_entrance)
    ImageView shopEntrance;

    MainActivity instance = null;

    private String defaultNum;
    private String userPhone;

    private String couponUsed;
    private String couponUnUsed;
    private String couponExpire;
    private String integralUrl;

    int isAutonym;

    String token;

    public static PersonalFragment newInstance(){
        PersonalFragment fragment = new PersonalFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_personal, container, false);
        ButterKnife.inject(this, view);
//        token = String.format(getString(R.string.token), MyConfig.getSharePreStr(getActivity(), Constants.USERINFO, Constants.UID));
        instance = (MainActivity) getActivity();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();

//        if(MyConfig.getSharePreBoolean(getActivity(), Constants.USERINFO, Constants.IS_DOWNLOAD)){
//            new UpdateManger(getActivity(),MyConfig.getSharePreStr(getActivity(),Constants.USERINFO,Constants.DOWNLOAD_APK_URL)).
//                    showConfirmDialog("温馨提示",MyConfig.getSharePreStr(getActivity(),Constants.USERINFO,Constants.UPDATE_REMARKS));
//        }

        if(LoginControl.getInstance(getActivity()).isLogin()){
            nameLayout.setVisibility(View.VISIBLE);
            loginRegistRl.setVisibility(View.GONE);
            getUserInfo("");
        }else {
            nameLayout.setVisibility(View.GONE);
            loginRegistRl.setVisibility(View.VISIBLE);
            balance.setText("0元");
            discountTv.setText("0张");
            integral.setText("0分");

//            balance.setVisibility(View.GONE);
//            discountTv.setVisibility(View.GONE);
//            integral.setVisibility(View.GONE);
        }
    }

    void initView(){

        if(instance.bannerEntities.isEmpty()){
            shopEntrance.setVisibility(View.GONE);
        }else {
            if(instance.bannerEntities.get(0).status != 1){
                shopEntrance.setVisibility(View.GONE);
            }else {
                shopEntrance.setVisibility(View.VISIBLE);
                Picasso.with(getActivity()).load(String.format(getString(R.string.img_url),instance.bannerEntities.get(0).img))
                        .fit()
                        .centerCrop()
                        .placeholder(R.drawable.banner_detail)
                        .error(R.drawable.banner_detail).into(shopEntrance);
                shopEntrance.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent;
                        switch (instance.bannerEntities.get(0).type){
                            case 1:
                                intent = new Intent(getActivity(), SurprisedActivity.class);
                                intent.putExtra(Constants.KEY_URL,instance.bannerEntities.get(0).url);
//                                intent.putExtra(Constants.KEY_URL,"http://testwww.caihongka.com/wz");
//                                intent.putExtra(Constants.KEY_URL,String.format(getString(R.string.check_url),MyConfig.getSharePreStr(getActivity(), Constants.USERINFO, Constants.UID).replace(".","(dian)")));
//                                intent.putExtra(Constants.KEY_URL,"http://sports.sohu.com/20170120/n479180172.shtml");
                                startActivity(intent);
                                break;
                            case 2:

                                break;
                        }
                    }
                });
            }
        }

        //新手指引
        /*if(MyConfig.getSharePreBoolean(getActivity(), Constants.USERINFO, Constants.SHOW_HELP)){
            helpIcon.setVisibility(View.VISIBLE);
        }else {
            helpIcon.setVisibility(View.GONE);
        }

        helpIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpIcon.setVisibility(View.GONE);
                MyConfig.putSharePre(getActivity(),Constants.USERINFO,Constants.SHOW_HELP,false);
            }
        });*/

//        if(MyConfig.getSharePreBoolean(getActivity(), Constants.USERINFO, Constants.SHOW_HELP)){
//            newShow.setVisibility(View.VISIBLE);
//        }else {
//            newShow.setVisibility(View.GONE);
//        }
        if(MyConfig.getSharePreBoolean(getActivity(), Constants.USERINFO, Constants.HOT_SHOW)){
            hotShow.setVisibility(View.VISIBLE);
        }else {
            hotShow.setVisibility(View.GONE);
        }

        //设str为图片的字符串
//        byte[] imageByte = Base64.decode("iVBORw0KGgoAAAANSUhEUgAAAG4AAAAoCAIAAACQOFjdAAAKrUlEQVRoge1aaWxU1xU+b5vFM17AGHtsJ46hBorZiWlJG7NZIQRSki5UAokqtGmTSLSqRIoioFFLqy5KE9rQRoI0lUpTSkNQKAlLGcISQoxjdicBjA0Gj8cMNtizvZm33f54kzd37rvveTxgIlWcH6Pre889y3fPPee8CwxCCO7RnSD2izbg\\/4fuQXnH6B6Ud4zuQXnHyBJKfzBBDIaU7rK6oSDGpoLrXjX4XHfHlNtR5w8m7pqdVsRSw8EfTODzdyFSco5KwuzbsWFvQAxLWjZyqNYyCCFzOJhl3YUzzzkqDWtt9hrCbbS8cko8e1NeNt6RjQ1mOfSobPC5cCZ9PKSxqd9Qw9UcyN5IfJWKlD+Y4AWtKagGoxpxEQmIrK4sGZVG0tEH+IEbM0MUoYOVjJtqTFrBZJ5s8LlO31C2tSYDUS3fwdyS1HAS2vu1CSPYH052Au2aWp2WPs\\/s7xLxsLeyL5tLlDOZDwxMh2q\\/kSB7QBt8rqSK1jfFd7ZL62bkPTHK4eCYaxF1zfFYc7cKAP+cnz+lhKdqMSRTLrj+B6Eb58PdG4qbjiOFj3W9VsWQcMxKMnW8NyA+fSC6o01aUuNcUuN0cIw\\/mLgQlZfXOr41RmAA1h2PUeEj7CTSEYtvMHKBOVcaS1a25kzUkKeCSNy4sKTt7hSJULCJR4NzV5vSHFIAYGoJZ+zSV2fdxy+vFa6GtTzgzNt1M6yuC4tvwMMQt4wIHIL\\/9sksnEqE6j+di79wJLH4vf4DHfKW1ji+RBwDHkHvdIhHOxV9SVRI4Q0+1\\/QyflWdc0Zp+oITJcvAlHA\\/1aLbZCh\\/MDG5yFHgYD4IJQeVNG2SXXdMUxE61y89WuHGMSLK2oCpsCumLt0XCcURAJTmMQ+XC\\/UVwswywSMwVgnuVpR58XgK96VjnWvr8mxckFR0JJS0YcD94s1TxEyFk1+0q9\\/FMXOreA2JLMOYfbZSQ+RmUUEbz4rbW6W4giq8bFhCmzxSXRm3eqrn\\/e4kYDFFVEKzWGO+Mp8NxVUAuB5HJ24orX3qT4\\/ERhWxS0Y7PW7kdTDGRp3\\/QCANzdEu2cb+QFRdeTi2Y2EBZB42ZGYMEkpcGc56LCj\\/+HA0rkAE0Lbz8oEOZfX0PKdTGxBNc8pXNPRyc7IzggDgrQX5tcX8voD43yvKlk9lf0f\\/iomOZV\\/KszoDM6bGuKNfMyZ\\/Ni1vVoWw+Xx8S4v062aRY+ArPm5xjYCfis\\/NAag6\\/9WI1tQtzygTqC74PGxrn6po6ND1JGC5EizyXgaUBPc7bckXj8fltKnQI6Lnj8YqvIyzjkuyKoE71SCDPgyoOo4A8Mpp8c+zvfMr3BybaOlRr0XQxpPS10c6qwo4MOVEat+uq9vaFu9NpGTyLHy1jAeAp8flcSr70ilRRXCsS41J8Ob8fP0yAUCFl8PlrG+O71pUSHWBZRiWAQ1Z1jTiyDPKDm7xGxfiEU3dt7hwXhWflwE4BKLoRwejfz8nX+7XrHAkegAA6I6lz+RYUDkRUnSkJg7nAaBfQr9sihMWE40RocIfTHzSk5ZZU8Q6uRReo4vSeJ3pUfdflQ05OtwGtfVpvzkZpbrQFVNL3KyDY8Ca8PtOr1MAsGJs6rq9+nB+b0J77Vxi28Wkir0iNYeU5pAysYRdNEqgAkp0+5NKuGOBlACehZoi7kyfCgAdkRQcjd1KTEZ6xcBvELUD038\\/61WN+SdGOY2NVyMqvuVsr8I5NH1pQjE\\/fjj36c00w7YLcrmX9QPpwr9bpW9UO2xwhMw8xpoXzH14sYtdW5e3d3HB49UO\\/IzKvUyllwVaDBI4NvhcXy7mnproqB3OlXuZFRMcZ\\/qkBp+rO6Z9divlFQLQH\\/zsm0RjSVZRW186KuvLBZ1fVNDWCxll96ak4qJWT3fjXkgqbDyVbOtL5yt\\/MLHninSoU\\/5+7SC6Pbv3Sipd6lP\\/1ZosEJgRXjTSw1JLhGETMW9uUH7eGNt+SdJnxg\\/ntj9WYKXXXMcPB+RnD0ZTbgAsqXHMKBXyBGZzS+LkjXTHyDKwdqZzhJvFjdnUIm44nSGQZWBaKVdbzGoIOsPQI2obZ3tL3OTTuPnGGAhkC6WioZZeNSajB0t5RQOPYJdBgIaaOdlF4syaj+IGFs9Mcayc4LGRhpve4HOtb4pvvZiKvserHeOGcVsvJjujGrF3QTW\\/YJSAW6LT25eSvz8hRmTSfY8ADVX8nPt5lmEG\\/HYCDNMBoDzbozR2yx8FldY+9SdT3N+pcdowU1UCDcqkgt5rlw9fS6VOjwDfHedYNZmOI1WgP5j4xYcJo3xvnuv9WrmgaOix\\/4RvJbVZ5UJURk6OWVjtYAUNLHJFWNL2dshN3XIgpgksw3NozHB2ykiOZxni2AgDDMLF0qEMRNU3LyTfvSz1JBAAVBewm+d5yz2cmdPGbSJX6oOYjI52Ki09KscwBU6YOkLQGG3CCM7JMzibTbuq84Ri2q8aUyHp4qBxSZFeap87GD0UkF0cPFrNz63KaBjt2zWbLwKCx2qVAuWG0+JfP0kYxXpMEfe3Bu8w123926Q\\/mAhEtCOdyqnr6vQy7oVpnva4jGPtvyLX+fhCJwO2OBoOrDoW2d2eSoizK4S\\/zPHq42\\/vDhvV+ZEH+EWjBRuB9gZbbbRaIqHccFrc1JLG3sXB2wsLqgso8Zjlc4aooD+ciZ0Laed6MxqUynxmRhk\\/vYx7ssq95ni0pUddOY3MHkRENPhcr5+Pt\\/aimKodDyqxz7\\/6np\\/mfmp8ivOht\\/r6kumm\\/bf1LqMxNPcYVuqo3++E12b3M2JNUtE\\/zmcoeG6Sm8CR2iqDNa1riu6\\/ohA4AkBnBO1oldd+kJizo8\\/fIb8xL9\\/eK32m2M1MKuYkBZKYvH4pHQ14YCgadEYsE6X504XKY3XfzfwZUMZkFMfenYqczLKx6UihZpMBY\\/Olh\\/IPfrNw81zvg2WcyxTcCCAUR6\\/We0\\/dksCEoFn4k1Xu0kLYNDf\\/d\\/Wu1+Z46yu5Si+7qSXxzPuRPVckDaGaogwd+qPRcn848PmHAPHOgDtC7TqyTw7kBV+6L3z6hgoADMAr9Z5H7s9o981oDioNiQra1yHtuiw1diuG1pWTXc9OdJuZs3kr0Rkuh9UjAflkSPnjLO\\/H1+UfHIga7wYTS9hJJdyeNmXNTOeCSjch0+Yi50AklFEJbbmQ6E2gRQ84rP59g6BsHtwICsW1dy9LO9uTJXns65lXm1BnX8qpSx9fl18+JZ7pSaWA4S7mexMc1YUZLToMVI6tyEZvti06FcoBe5cBSdEQz5LdPvEhYfUdZa+3K6bu7Ei4OKYinzGehXI21VxtBig7NoR\\/ZRtjq7yTPZlxBNMzJbFq+EB9LjKo3MPVDOPuK2AJHPHXEEIsMSBMGrhCoKxpf5dIDPQx8afNLn1MbMmNspegqyOMx38JzkGJwvkpUFIhsxI94ACZQMzGXLOhuZGNPdQZfMkMNP5rPhJ6rswhA1ITnP3Fz7JADwXZlHKDzB8I9pwUKHNuEcwQWEE5IIj2zMRDUfYWWtlppZ14\\/rHnoZSdbF5eqfaZq0GD6UnYbBBxckTNMXtLeDXYimdVtfym\\/0hhNo\\/KYzBQoDQ2ZG8lER34Xly98UsFFIfGPgRybhjAuj2wOnK86FPbIIPhf00LvOGgzIycAAAAAElFTkSuQmCC",Base64.DEFAULT);
//
//        Bitmap bitmap = BitmapFactory.decodeByteArray(imageByte,0,imageByte.length);
//        cardLogo.setImageBitmap(bitmap);

        cardLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LoginControl.getInstance(getActivity()).isLogin()){
                    Intent intent = new Intent(getActivity(),UserSetActivity.class);
                    intent.putExtra(Constants.KEY_USER_PHONE,userPhone);
                    startActivity(intent);
                }else {
                    goToOthers(LoginActivity.class);
                }
            }
        });
        nameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LoginControl.getInstance(getActivity()).isLogin()){
                    Intent intent = new Intent(getActivity(),UserSetActivity.class);
                    intent.putExtra(Constants.KEY_USER_PHONE,userPhone);
                    startActivity(intent);
                }else {
                    goToOthers(LoginActivity.class);
                }
            }
        });
        loginRegistRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LoginControl.getInstance(getActivity()).isLogin()){
                    Intent intent = new Intent(getActivity(),UserSetActivity.class);
                    intent.putExtra(Constants.KEY_USER_PHONE,userPhone);
                    startActivity(intent);
                }else {
                    goToOthers(LoginActivity.class);
                }
            }
        });
        arrowIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LoginControl.getInstance(getActivity()).isLogin()){
                    Intent intent = new Intent(getActivity(),UserSetActivity.class);
                    intent.putExtra(Constants.KEY_USER_PHONE,userPhone);
                    startActivity(intent);
                }else {
                    goToOthers(LoginActivity.class);
                }
            }
        });
        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToOthers(UserSetActivity.class);
            }
        });

        attestationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAutonym == 1) {
                    goToOthers(AuthenticationActivity.class);
                }else {
                    UIUtils.toast("您已实名认证");
                }
            }
        });

        withdrawLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAutonym == 0){
                    Intent intent = new Intent(getActivity(),WithdrawDepositActivity.class);
                    startActivity(intent);
//                    UIUtils.toast("去提现");
                }else {
                    showDialog("温馨提示","为了保障您账户的资金安全,\n请先进行实名认证。");
                }
            }
        });


        loginRegistRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToOthers(LoginActivity.class);
            }
        });
        accountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LoginControl.getInstance(getActivity()).isLogin()){
                    Intent intent = new Intent(getActivity(),MyWalletActivity.class);
                    intent.putExtra(Constants.KEY_CITY_ID,instance.cityId);
                    startActivity(intent);
                }else {
                    goToOthers(LoginActivity.class);
                }
            }
        });
        freeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hotShow.setVisibility(View.GONE);
                MyConfig.putSharePre(getActivity(),Constants.USERINFO,Constants.HOT_SHOW,false);
                if(LoginControl.getInstance(getActivity()).isLogin()){
                    Intent intent = new Intent(getActivity(),FreeWashTicketActivity.class);
                    intent.putExtra(Constants.KEY_CITY_ID,instance.cityId);
                    intent.putExtra(Constants.KEY_LAT,instance.lat);
                    intent.putExtra(Constants.KEY_LNG,instance.lng);
                    startActivity(intent);
                }else {
                    goToOthers(LoginActivity.class);
                }
            }
        });
        shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LoginControl.getInstance(getActivity()).isLogin()){
                    Intent intent = new Intent(getActivity(),InviteActivity.class);
                    startActivity(intent);
                }else {
                    goToOthers(LoginActivity.class);
                }
            }
        });

        cardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LoginControl.getInstance(getActivity()).isLogin()){
                    Intent intent = new Intent(getActivity(),MyCardActivity.class);
                    intent.putExtra(Constants.KEY_DEFAULT_ACCOUNT,defaultNum);
                    intent.putExtra(Constants.KEY_USER_PHONE,userPhone);
                    startActivity(intent);
                }else {
                    goToOthers(LoginActivity.class);
                }
            }
        });

        orderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LoginControl.getInstance(getActivity()).isLogin()){
                    goToOthers(MyOrderActivity.class);
                }else {
                    goToOthers(LoginActivity.class);
                }
            }
        });
        capitalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LoginControl.getInstance(getActivity()).isLogin()){
                    goToOthers(CapitalDetailActivity.class);
                }else {
                    goToOthers(LoginActivity.class);
                }
            }
        });
        discountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LoginControl.getInstance(getActivity()).isLogin()){
//                    goToOthers(MyDiscountActivity.class);
                    Intent intent = new Intent(getActivity(),MyDiscountActivity.class);
                    intent.putExtra(Constants.KEY_COUPON_USED,couponUsed);
                    intent.putExtra(Constants.KEY_COUPON_UN_USED,couponUnUsed);
                    intent.putExtra(Constants.KEY_COUPON_EXPIRE,couponExpire);
                    startActivity(intent);
                }else {
                    goToOthers(LoginActivity.class);
                }
            }
        });
        integralLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LoginControl.getInstance(getActivity()).isLogin()){
                    refreshToken(integralUrl);
                }else {
                    goToOthers(LoginActivity.class);
                }
            }
        });
        rechargeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LoginControl.getInstance(getActivity()).isLogin()){
                    Intent intent = new Intent(getActivity(),RechargeAccountActivity.class);
                    intent.putExtra(Constants.KEY_CITY_ID,instance.cityId);
                    intent.putExtra(Constants.KEY_IS_CERTIFICATION,isAutonym);
                    startActivity(intent);
                }else {
                    goToOthers(LoginActivity.class);
                }
            }
        });
        rechargeRecordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LoginControl.getInstance(getActivity()).isLogin()){
                    Intent intent = new Intent(getActivity(),RechargeOrderActivity.class);
                    intent.putExtra(Constants.KEY_RECHARGE_OR_CARD,2);
                    startActivity(intent);
                }else {
                    goToOthers(LoginActivity.class);
                }
            }
        });
        collectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LoginControl.getInstance(getActivity()).isLogin()){
                    Intent intent = new Intent(getActivity(), CollectShopActivity.class);
                    intent.putExtra(Constants.KEY_CITY_ID,instance.cityId);
                    intent.putExtra(Constants.KEY_LAT,instance.lat);
                    intent.putExtra(Constants.KEY_LNG,instance.lng);
                    startActivity(intent);
                }else {
                    goToOthers(LoginActivity.class);
                }
            }
        });


        storeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LoginControl.getInstance(getActivity()).isLogin()) {
                    newShow.setVisibility(View.GONE);
                    MyConfig.putSharePre(getActivity(), Constants.USERINFO, Constants.SHOW_HELP, false);
                    Intent intent = new Intent(getActivity(), IntegralShopActivity.class);
                    intent.putExtra(Constants.KEY_CITY_ID, instance.cityId);
                    startActivity(intent);
                }else {
                    goToOthers(LoginActivity.class);
                }
            }
        });

        addrLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LoginControl.getInstance(getActivity()).isLogin()) {
                    Intent intent = new Intent(getActivity(), ManageAddressActivity.class);
                    startActivity(intent);
                }else {
                    goToOthers(LoginActivity.class);
                }
            }
        });
        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToOthers(SettingActivity.class);
            }
        });
    }

    public void goToOthers(Class<?> cls){
        Intent intent = new Intent(getActivity(),cls);
        startActivity(intent);
    }

    //获取用户信息
    public void getUserInfo(String token){
        if(TextUtils.isEmpty(token)) {
            token = String.format(getString(R.string.token), MyConfig.getSharePreStr(getActivity(), Constants.USERINFO, Constants.UID));
        }
        withBtwVolley().load(API.API_GET_USER_INFO)
                .method(Request.Method.GET)
                .setHeader("Authorization",token)
                .setHeader("Accept", API.VERSION)
                .setUIComponent(this)
                .setRetrys(0)
                .setTimeout(10000)
                .setResponseHandler(new BtwVolley.ResponseHandler<UserModel>() {
                    @Override
                    public void onStart() {
                        getUIUtils().loading();
                    }

                    @Override
                    public void onFinish() {
                        getUIUtils().dismissLoading();
                    }

                    @Override
                    public void onResponse(final UserModel resp) {
                        defaultNum = resp.data.defaultAccount;
                        userPhone = resp.data.phone;
                        couponUsed = resp.data.couponUsed;
                        couponUnUsed = resp.data.couponUnUsed;
                        couponExpire = resp.data.couponExpire;
                        integralUrl = resp.data.integralUrl;
                        userName.setText(encryptPhone(userPhone));
                        //小数点后00显示整数，否刚显示两位

//                        if(resp.data.money % 1 == 0){
//                            balance.setText(String.format(getString(R.string.pay_points), new
//                                    DecimalFormat("##,###,###,###,##0").format(resp.data.money)));
//                        }else {
                            balance.setText(String.format(getString(R.string.pay_points), new
                                    DecimalFormat("##,###,###,###,##0.00").format(resp.data.totalMoney)));
//                        }
//                        setRainbowBalance(resp.data.money);
                        integral.setText(String.format(getString(R.string.point),resp.data.integral));
                        discountTv.setText(String.format(getString(R.string.discount_count),resp.data.couponUnUsed));

                        isAutonym = resp.data.needCertification;
                        if(isAutonym == 0){
                            attestationTv.setText("已认证");
                            attestationTv.setBackgroundResource(R.drawable.bg_layout_focused_red_full);
                        }else {
                            attestationTv.setText("未认证");
                            attestationTv.setBackgroundResource(R.drawable.bg_layout_focused_gray_full);
                        }
//                        discountTv.setVisibility(View.VISIBLE);
//                        balance.setVisibility(View.VISIBLE);
//                        integral.setVisibility(View.VISIBLE);
                        MyConfig.putSharePre(getActivity(), Constants.USERINFO, Constants.DEFAULT_NO, resp.data.defaultAccount);
                        MyConfig.putSharePre(getActivity(), Constants.USERINFO, Constants.USER_PHONE, resp.data.phone);

                    }

                    @Override
                    public void onBtwError(BtwRespError<UserModel> error) {
                        Toast.makeText(getActivity(), error.errorMessage, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onNetworkError(VolleyUtils.NetworkError error) {
                        Toast.makeText(getActivity(), R.string.network_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRefreToken() {
                        refreshToken();
//                        getUserInfo();
                    }
                }).excute(UserModel.class);
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
                        getUserInfo(String.format(getString(R.string.token),resp.data.token));
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

    private String encryptPhone(String phone) {
        if (phone == null || phone.length() < 4) {
            return phone;
        }

        char[] chars = phone.toCharArray();
        for (int i = 0, len = chars.length; i < len; i++) {
            if (i < 3 || len - i <= 4) {
                continue;
            }
            chars[i] = '*';
        }

        return String.valueOf(chars);
    }

    @Override
    public void onPause() {
        super.onPause();
//        MyConfig.putSharePre(getActivity(),Constants.USERINFO,Constants.SHOW_HELP,false);
    }

    public void refreshToken(final String url){
        withBtwVolley().load(API.API_REFRESH_TOKEN)
                .method(Request.Method.POST)
                .setHeader("Authorization",String.format(getString(R.string.token), MyConfig.getSharePreStr(getActivity(), Constants.USERINFO, Constants.UID)))
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
                        Intent intent = new Intent(getActivity(), IntegralActivity.class);
                        intent.putExtra(Constants.KEY_URL,url);
                        startActivity(intent);
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

    private void showDialog(String title, String tip) {
        final Dialog dialog = new Dialog(getActivity(),R.style.loading_dialog);
        dialog.setContentView(R.layout.ui_ios_alert_dialog);
        Window dialogWindow = dialog.getWindow();
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = -80;
        lp.width = (int)(display.getWidth() * 0.90);
        dialogWindow.setAttributes(lp);

        TextView alertTitle = (TextView) dialog.findViewById(R.id.alert_title);
        TextView alertTip = (TextView) dialog.findViewById(R.id.alert_tip);

        alertTitle.setText(title);
        alertTip.setText(tip);

        dialog.findViewById(R.id.alert_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),AuthenticationActivity.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.alert_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}
