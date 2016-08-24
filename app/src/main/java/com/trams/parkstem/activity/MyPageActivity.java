package com.trams.parkstem.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseBackSearchActivity;
import com.trams.parkstem.login.OnLoginSuccessListener;
import com.trams.parkstem.server.ServerClient;

/**
 * Created by Noverish on 2016-08-24.
 */
public class MyPageActivity extends BaseBackSearchActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);
        setTitle("마이페이지");
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
    }
}
