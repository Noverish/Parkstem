package com.trams.parkstem.webview;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseBackSearchActivity;
import com.trams.parkstem.server.ServerClient;

/**
 * Created by monc2 on 2016-07-22.
 */
public class MobilecertificationWebView extends BaseBackSearchActivity {
    ServerClient serverClient = ServerClient.getInstance();
    WebView webview;

    @Override
    public void onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        setBackEnable(true);

        webview = (WebView) findViewById(R.id.webview);
        Button backButton = (Button) findViewById(R.id.werbview_back_button);
        backButton.setVisibility(View.GONE);

        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (url.contains("kmcis_mobile.php")) {
                    try {
                        if(ServerClient.getInstance().memberInfo().certification)
                            Toast.makeText(MobilecertificationWebView.this, "휴대폰 인증에 성공했습니다", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(MobilecertificationWebView.this, "휴대폰 인증에 실패했습니다", Toast.LENGTH_SHORT).show();
                    } catch (ServerClient.ServerErrorException ex) {
                        Toast.makeText(MobilecertificationWebView.this, "휴대폰 인증에 실패했습니다 - " + ex.msg, Toast.LENGTH_SHORT).show();
                    }
                    finish();
                }
            }
        });

        webview.loadUrl("http://app.parkstem.com/api/kmcis_start.php?uniqueID=" + serverClient.getUniqueID());
    }
}
