package com.trams.parkstem.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseBackSearchActivity;
import com.trams.parkstem.login.OnLoginSuccessListener;
import com.trams.parkstem.server.ServerClient;

/**
 * Created by Noverish on 2016-08-24.
 */
public class MyPageActivity extends BaseBackSearchActivity implements View.OnTouchListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);
        setTitle("마이페이지");

        RelativeLayout changeEmail = (RelativeLayout) findViewById(R.id.activity_my_page_change_email);
        if(changeEmail != null) {
            if(ServerClient.getInstance().getMemberGubun().equals(OnLoginSuccessListener.KAKAO)) {
                changeEmail.setOnTouchListener(this);
                changeEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MyPageActivity.this, ChangeEmailActivity.class));
                    }
                });
            }
        }

        RelativeLayout changePassword = (RelativeLayout) findViewById(R.id.activity_my_page_change_password);
        if(changePassword != null) {
            changePassword.setOnTouchListener(this);
            changePassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(ServerClient.getInstance().getMemberGubun().equals(OnLoginSuccessListener.PARKSTEM))
                        startActivity(new Intent(MyPageActivity.this, ChangePasswordActivity.class));
                    else
                        Toast.makeText(MyPageActivity.this, "이메일로 로그인 하신 분만 사용할 수 있습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        ImageView memberGubunImage = (ImageView) findViewById(R.id.activity_my_page_member_gubun_image);
        if(memberGubunImage != null) {
            String memberGubun = ServerClient.getInstance().getMemberGubun();

            if(memberGubun.equals(OnLoginSuccessListener.PARKSTEM))
                memberGubunImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.login_email_logo0));
            else if(memberGubun.equals(OnLoginSuccessListener.NAVER))
                memberGubunImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.login_naver_logo));
            else if(memberGubun.equals(OnLoginSuccessListener.KAKAO))
                memberGubunImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.login_kakao_logo));
            else if(memberGubun.equals(OnLoginSuccessListener.FACEBOOK))
                memberGubunImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.login_fb_logo));
            else
                Log.e("ERROR", "Unknown MemberGubun");
        }

        TextView memberGubunText = (TextView) findViewById(R.id.activity_my_page_member_gubun_text);
        if(memberGubunText != null) {
            String memberGubun = ServerClient.getInstance().getMemberGubun();

            if(memberGubun.equals(OnLoginSuccessListener.PARKSTEM))
                memberGubunText.setText("이메일");
            else if(memberGubun.equals(OnLoginSuccessListener.NAVER))
                memberGubunText.setText("네이버");
            else if(memberGubun.equals(OnLoginSuccessListener.KAKAO))
                memberGubunText.setText("카카오");
            else if(memberGubun.equals(OnLoginSuccessListener.FACEBOOK))
                memberGubunText.setText("페이스북");
            else
                Log.e("ERROR", "Unknown MemberGubun");
        }

        TextView name = (TextView) findViewById(R.id.activity_my_page_name);
        if(name != null) {
            name.setText(ServerClient.getInstance().getUserName());
        }

        TextView phone = (TextView) findViewById(R.id.activity_my_page_phone);
        if(phone != null) {
            phone.setText(ServerClient.getInstance().getUserMobile());
        }

        TextView email = (TextView) findViewById(R.id.activity_my_page_email);
        if(email != null) {
            if(ServerClient.getInstance().getMemberGubun().equals(OnLoginSuccessListener.KAKAO) && ServerClient.getInstance().getUserEmail().equals("")) {
                email.setText("이메일 수정");
            } else {
                email.setText(ServerClient.getInstance().getUserEmail());
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        try {
            Context context = MyPageActivity.this;
            RelativeLayout background = (RelativeLayout) v;
            TextView text = (TextView) ((RelativeLayout) v).getChildAt(0);

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                background.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_1));
                text.setTextColor(ContextCompat.getColor(context, R.color.WHITE));
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                background.setBackground(ContextCompat.getDrawable(context, R.drawable.box_edit));
                text.setTextColor(ContextCompat.getColor(context, R.color.gray_for_text));
            } else {
                if(event.getAction() != MotionEvent.ACTION_MOVE)
                    Log.e("MotionEvent", event.getAction() + "");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e("ERROR","Unknown ERROR");
        }
        return false;
    }
}
