package com.trams.parkstem.others;

/**
 * Created by Noverish on 2016-07-22.
 */
public interface OnLoginSuccessListener {
    int NAVER = 0;
    int FACEBOOK = 1;
    int KAKAO = 2;
    int PARKSTEM = 3;

    void onLoginSuccess(
            int gubun, String name, String email, String mobile, String nickName,
            String kakaoID, String facebookID, String naverID, String parkstemID, String parkstemPW);
}
