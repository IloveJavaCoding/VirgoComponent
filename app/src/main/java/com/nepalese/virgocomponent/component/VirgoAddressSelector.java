package com.nepalese.virgocomponent.component;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.nepalese.virgocomponent.R;
import com.nepalese.virgocomponent.view.VirgoPickerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author nepalese on 2021/1/11 15:15
 * @usage 简单地址选择器
 */
public class VirgoAddressSelector {
    private static final String TAG = "VirgoAddressSelector";

    private Context context;
    private ResultCallBack callBack;

    private Dialog selectorDialog;
    private VirgoPickerView pvProvince;
    private VirgoPickerView pvCity;

    private final long ANIMATORDELAY = 200L;

    public interface ResultCallBack {
        void choosed(String province, String city);
    }

    private List<String> order, province, city;
    private String selectProvince, selectCity;

    public VirgoAddressSelector(Context context, ResultCallBack resultCallBack) {
        this.context = context;
        this.callBack = resultCallBack;

        initData();
        initDialog();
        initView();
    }

    //默认选择
    private void initData() {
        province = new ArrayList<>();
        city = new ArrayList<>();
        order = Arrays.asList(context.getResources().getStringArray(R.array.province));
        province.addAll(order);
        selectProvince = province.get(0);
        city.addAll(Arrays.asList(context.getResources().getStringArray(R.array.city_01)));
        selectCity = city.get(0);
    }

    private void initDialog() {
        if (selectorDialog == null) {
            selectorDialog = new Dialog(context, R.style.VirgoPick_Dialog);
            selectorDialog.setCancelable(false);
            selectorDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            selectorDialog.setContentView(R.layout.layout_address_selector);//layout of the dialog

            Window window = selectorDialog.getWindow();
            window.setGravity(Gravity.CENTER);//location -- center
//            WindowManager.LayoutParams lp = window.getAttributes();
//            lp.width = dialogWidth;
//            window.setAttributes(lp);
        }
    }

    private void initView() {
        pvProvince = selectorDialog.findViewById(R.id.pvProvince);
        pvCity = selectorDialog.findViewById(R.id.pvCity);

        TextView tvCancel = selectorDialog.findViewById(R.id.tvCancel);
        TextView tvConfirm = selectorDialog.findViewById(R.id.tvConfirm);

        tvCancel.setOnClickListener(view -> selectorDialog.dismiss());

        tvConfirm.setOnClickListener(view -> {
            callBack.choosed(selectProvince, selectCity);
            selectorDialog.dismiss();
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void show() {
        initAddress();
        addListener();
        selectorDialog.show();
    }

    //默认选择值
    private void initAddress() {
        pvProvince.setData(province);
        pvProvince.setSelected(0);
        pvCity.setData(city);
        pvCity.setSelected(0);
    }

    private void addListener() {
        pvProvince.setOnSelectListener(text -> {
            selectProvince = text;
            cityChange();
        });

        pvCity.setOnSelectListener(text -> {
            selectCity = text;
        });
    }

    private void cityChange() {
        city.clear();
        int index = 0;
        for (int i=0; i<order.size(); i++){
            if(selectProvince.equals(order.get(i))){
                index = i+1;
                break;
            }
        }
        Log.i(TAG, "cityChange: " + index);

        List<String> temp;
        switch (index){
            case 1:
                temp = Arrays.asList(context.getResources().getStringArray(R.array.city_01));
                break;
            case 2:
                temp = Arrays.asList(context.getResources().getStringArray(R.array.city_02));
                break;
            case 3:
                temp = Arrays.asList(context.getResources().getStringArray(R.array.city_03));
                break;
            case 4:
                temp = Arrays.asList(context.getResources().getStringArray(R.array.city_04));
                break;
            case 5:
                temp = Arrays.asList(context.getResources().getStringArray(R.array.city_05));
                break;

            case 6:
                temp = Arrays.asList(context.getResources().getStringArray(R.array.city_06));
                break;
            case 7:
                temp = Arrays.asList(context.getResources().getStringArray(R.array.city_07));
                break;
            case 8:
                temp = Arrays.asList(context.getResources().getStringArray(R.array.city_01));
                break;
            case 9:
                temp = Arrays.asList(context.getResources().getStringArray(R.array.city_09));
                break;
            case 10:
                temp = Arrays.asList(context.getResources().getStringArray(R.array.city_10));
                break;

            case 11:
                temp = Arrays.asList(context.getResources().getStringArray(R.array.city_11));
                break;
            case 12:
                temp = Arrays.asList(context.getResources().getStringArray(R.array.city_12));
                break;
            case 13:
                temp = Arrays.asList(context.getResources().getStringArray(R.array.city_13));
                break;
            case 14:
                temp = Arrays.asList(context.getResources().getStringArray(R.array.city_14));
                break;
            case 15:
                temp = Arrays.asList(context.getResources().getStringArray(R.array.city_15));
                break;

            case 16:
                temp = Arrays.asList(context.getResources().getStringArray(R.array.city_16));
                break;
            case 17:
                temp = Arrays.asList(context.getResources().getStringArray(R.array.city_17));
                break;
            case 18:
                temp = Arrays.asList(context.getResources().getStringArray(R.array.city_18));
                break;
            case 19:
                temp = Arrays.asList(context.getResources().getStringArray(R.array.city_19));
                break;
            case 20:
                temp = Arrays.asList(context.getResources().getStringArray(R.array.city_20));
                break;

            case 21:
                temp = Arrays.asList(context.getResources().getStringArray(R.array.city_21));
                break;
            case 22:
                temp = Arrays.asList(context.getResources().getStringArray(R.array.city_22));
                break;
            case 23:
                temp = Arrays.asList(context.getResources().getStringArray(R.array.city_23));
                break;

            case 24:
                temp = Arrays.asList(context.getResources().getStringArray(R.array.city_24));
                break;
            case 25:
                temp = Arrays.asList(context.getResources().getStringArray(R.array.city_25));
                break;
            case 26:
                temp = Arrays.asList(context.getResources().getStringArray(R.array.city_26));
                break;
            case 27:
                temp = Arrays.asList(context.getResources().getStringArray(R.array.city_27));
                break;

            case 28:
                temp = Arrays.asList(context.getResources().getStringArray(R.array.city_28));
                break;
            case 29:
                temp = Arrays.asList(context.getResources().getStringArray(R.array.city_29));
                break;
            case 30:
                temp = Arrays.asList(context.getResources().getStringArray(R.array.city_30));
                break;
            case 31:
                temp = Arrays.asList(context.getResources().getStringArray(R.array.city_31));
                break;
            case 32:
                temp = Arrays.asList(context.getResources().getStringArray(R.array.city_32));
                break;

            case 33:
                temp = Arrays.asList(context.getResources().getStringArray(R.array.city_33));
                break;
            case 34:
                temp = Arrays.asList(context.getResources().getStringArray(R.array.city_34));
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + index);
        }
        city.addAll(temp);

        pvCity.setData(city);
        pvCity.setSelected(0);
        executeAnimator(ANIMATORDELAY, pvCity);
    }

    private void executeAnimator(long ANIMATORDELAY, View view) {
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("alpha", 1f, 0f, 1f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.3f, 1f);
        PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.3f, 1f);
        ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY, pvhZ).setDuration(ANIMATORDELAY).start();
    }

    public void setIsLoop(boolean isLoop) {
        this.pvProvince.setIsLoop(isLoop);
        this.pvCity.setIsLoop(isLoop);
    }
}
