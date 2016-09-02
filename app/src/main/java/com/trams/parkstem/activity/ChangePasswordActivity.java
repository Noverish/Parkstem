package com.trams.parkstem.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseBackSearchActivity;
import com.trams.parkstem.others.Essentials;
import com.trams.parkstem.server.ServerClient;

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
        setToolbarTitle("비밀번호 변경");

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
        if (currentPW == null || newPW == null || confirmNewPW == null) {
            Log.e("ERROR", "NullPointerException!");
            return;
        }

        String currentPwStr = currentPW.getText().toString();
        String newPwStr = newPW.getText().toString();
        String newPwStrCheck = confirmNewPW.getText().toString();
        if (newPwStr.length() < 4)
            Toast.makeText(this, "비밀번호는 4자리 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
        else if (newPwStr.matches(".*[ㄱ-ㅎ가-힣].*"))
            Toast.makeText(this, "비밀번호는 영어 대소문자 및 특수문자의 조합이어야 합니다.", Toast.LENGTH_SHORT).show();
        else if (!newPwStr.equals(newPwStrCheck))
            Essentials.toastMessage(handler, this, "새 비밀번호가 일치하지 않습니다.");
        else {
            try {
                ServerClient.getInstance().changePasword(currentPwStr, newPwStr, newPwStrCheck);

                Toast.makeText(this, "비밀번호 변경이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            } catch (ServerClient.ServerErrorException ex) {
                Toast.makeText(this, ex.msg, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
