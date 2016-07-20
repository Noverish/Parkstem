package com.trams.parkstem.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseBackSearchActivity;

/**
 * Created by Noverish on 2016-07-18.
 */
public class ManagePurchaseActivity extends BaseBackSearchActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_purchase);
    }
}
