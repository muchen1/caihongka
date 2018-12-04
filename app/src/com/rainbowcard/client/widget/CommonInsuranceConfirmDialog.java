package com.rainbowcard.client.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.rainbowcard.client.R;
import com.rainbowcard.client.model.InsuranceModelServerProxy;
import com.rainbowcard.client.model.InsurancePriceModel;

public class CommonInsuranceConfirmDialog extends Dialog implements View.OnClickListener {

    private TextView mCarStyleTextview;

    private TextView mInsuranceForceExpireDate;

    private TextView mInsuranceBusinessExpireDate;

    private TextView mInsurancePersonTextview;

    private TextView mInsuranceConfirmBt;

    private onConfirmClickListener mListener;

    public CommonInsuranceConfirmDialog(@NonNull Context context) {
        super(context);
        initView();
    }

    public CommonInsuranceConfirmDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        initView();
    }

    protected CommonInsuranceConfirmDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_insurance_confirm_carinfo);
        mInsuranceConfirmBt = (TextView) findViewById(R.id.insurance_confirm_button);
        mInsuranceForceExpireDate = (TextView) findViewById(R.id.textview_insurance_force_value);
        mInsuranceBusinessExpireDate = (TextView) findViewById(R.id.textview_insurance_business_value);
        mCarStyleTextview = (TextView) findViewById(R.id.textview_car_style_value);
        mInsurancePersonTextview = (TextView) findViewById(R.id.textview_insurance_person_value);
        mInsuranceConfirmBt.setOnClickListener(this);

    }

    public CommonInsuranceConfirmDialog initData(String carNum) {
        InsurancePriceModel insurancePriceModel = InsuranceModelServerProxy.getInstance().getInsurancePriceModel(carNum);
        mInsuranceBusinessExpireDate.setText(insurancePriceModel.userInfoEntity.businessExpireDate);
        mInsuranceForceExpireDate.setText(insurancePriceModel.userInfoEntity.forceExpireDate);
        mCarStyleTextview.setText(insurancePriceModel.userInfoEntity.modleName);
        mInsurancePersonTextview.setText(insurancePriceModel.userInfoEntity.insuredName);
        return this;
    }

    public CommonInsuranceConfirmDialog setOnConfirmClickListener(onConfirmClickListener l) {
        mListener = l;
        return this;
    }

    @Override
    public void onClick(View v) {
        if (v == mInsuranceConfirmBt) {
            dismiss();
            if (mListener != null) {
                mListener.onConfirmBtClick();
            }
        }
    }

    public interface onConfirmClickListener {
        void onConfirmBtClick();
    }
}
