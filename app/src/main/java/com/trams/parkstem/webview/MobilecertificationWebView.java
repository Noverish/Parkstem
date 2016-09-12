package com.trams.parkstem.webview;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseBackSearchActivity;
import com.trams.parkstem.server.ServerClient;

import org.json.JSONException;
import org.json.JSONObject;

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
        webview.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.i("url",url);
                if (url.contains("kmcis_mobile.php")) {
                    view.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                }
            }
        });

        webview.loadUrl("http://app.parkstem.com/api/kmcis_start.php?uniqueID=" + serverClient.getUniqueID());
    }


    /* An instance of this class will be registered as a JavaScript interface */
    private class MyJavaScriptInterface
    {
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processHTML(String html) {
            html = html.replaceAll("<[^>]*>", "");

            try {
                JSONObject result = new JSONObject(html);
                if(result.getInt("res") == 1) {
                    Toast.makeText(MobilecertificationWebView.this, "휴대폰 인증에 성공했습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MobilecertificationWebView.this, "휴대폰 인증에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException ex) {
                Toast.makeText(MobilecertificationWebView.this, "휴대폰 인증하는데 오류가 발생했습니다. - JSON ERROR", Toast.LENGTH_SHORT).show();
            } finally {
                finish();
                try {
                    ServerClient.getInstance().memberInfo();
                } catch (ServerClient.ServerErrorException ex) {

                }
            }
        }
    }
}
