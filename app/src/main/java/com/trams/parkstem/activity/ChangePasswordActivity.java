package com.trams.parkstem.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseBackSearchActivity;
import com.trams.parkstem.others.Essentials;

/**
 * Created by Noverish on 2016-08-24.
 */
public class ChangePasswordActivity extends BaseBackSearchActivity {
    private EditText currentPW;
    private EditText newPW;
    private EditText confirmNewPW;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pw);
        setTitle("비밀번호 변경");

        currentPW = (EditText) findViewById(R.id.activity_change_pw_current);
        newPW = (EditText) findViewById(R.id.activity_change_pw_new);
        confirmNewPW = (EditText) findViewById(R.id.activity_change_pw_confirm);

        RelativeLayout changeButton = (RelativeLayout) findViewById(R.id.activity_change_pw_button);
        if(changeButton != null)
            changeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changePassword();
                }
            });
    }

    private void changePassword() {
        if(currentPW == null || newPW == null || confirmNewPW == null) {
            Log.e("ERROR", "NullPointerException!");
            return;
        }

        String newPwStr = newPW.getText().toString();
        if(!newPwStr.equals(confirmNewPW.getText().toString())) {
            Essentials.toastMessage(handler, this, "새 비밀번호가 일치하지 않습니다.");
            return;
        }
    }
}
