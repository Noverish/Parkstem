package com.trams.parkstem.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseBackSearchActivity;

/**
 * Created by Noverish on 2016-08-24.
 */
public class FindPasswordActivity extends BaseBackSearchActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pw);
        setTitle("비밀번호찾기");
    }
}
