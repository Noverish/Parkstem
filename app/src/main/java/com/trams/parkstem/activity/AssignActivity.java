package com.trams.parkstem.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.CheckBox;

import com.trams.parkstem.R;

/**
 * Created by Noverish on 2016-07-04.
 */
public class AssignActivity extends AppCompatActivity {
    private CheckBox checkBox;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        checkBox = (CheckBox) findViewById(R.id.assign_agree);
        //checkBox.isChecked(); 는 체크박스의 체크가 되어 있는지 아닌지를 반환함
    }
}
