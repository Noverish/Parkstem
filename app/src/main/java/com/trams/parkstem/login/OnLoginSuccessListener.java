package com.trams.parkstem.login;

/**
 * Created by Noverish on 2016-07-22.
 */
public interface OnLoginSuccessListener {
    String NAVER = "naver";
    String FACEBOOK = "fb";
    String KAKAO = "kakao";
    String PARKSTEM = "parkstem";

    void onLoginSuccess(
            String gubun, String name, String email, String mobile, String nickName,
            String kakaoID, String facebookID, String naverID, String parkstemID, String parkstemPW);
}
