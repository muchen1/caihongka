package com.rainbowcard.client.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.rainbowcard.client.R;
import com.rainbowcard.client.base.Constants;
import com.rainbowcard.client.base.MyBaseFragment;
import com.rainbowcard.client.ui.BindCardActivity;
import com.rainbowcard.client.ui.CardActivity;
import com.rainbowcard.client.ui.GetRainbowCardActivity;
import com.rainbowcard.client.ui.LoginActivity;
import com.rainbowcard.client.ui.QueryRainbowCardActivity;
import com.rainbowcard.client.ui.RechargeRainbowCardActivity;
import com.rainbowcard.client.utils.LoginControl;
import com.rainbowcard.client.utils.MyConfig;
import com.rainbowcard.client.utils.UpdateManger;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by gc on 2016/4/14.
 */
public class CaihongkaFragment extends MyBaseFragment {

    @InjectView(R.id.get_rainbow_card_layout)
    RelativeLayout getRainbowRl;
    @InjectView(R.id.recharge_rainbow_card_layout)
    RelativeLayout rechargeRainbowRl;
    @InjectView(R.id.query_rainbow_card_layout)
    RelativeLayout queryRainbowRl;


    public static CaihongkaFragment newInstance(){
        CaihongkaFragment fragment = new CaihongkaFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_caihongka, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void init(){
        getRainbowRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LoginControl.getInstance(getActivity()).isLogin()) {
                    goToOthers(GetRainbowCardActivity.class);
                }else {
                    goToOthers(LoginActivity.class);
                }
            }
        });
        rechargeRainbowRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LoginControl.getInstance(getActivity()).isLogin()) {
//                    goToOthers(RechargeRainbowCardActivity.class);
                    goToOthers(CardActivity.class);
                }else {
                    goToOthers(LoginActivity.class);
                }
            }
        });
        queryRainbowRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LoginControl.getInstance(getActivity()).isLogin()) {
                    goToOthers(BindCardActivity.class);
                }else {
                    goToOthers(LoginActivity.class);
                }
            }
        });

    }

    public void goToOthers(Class<?> cls){
        Intent intent = new Intent(getActivity(),cls);
        startActivity(intent);
    }

}
