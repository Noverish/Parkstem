package com.trams.parkstem.webview;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseBackSearchActivity;

/**
 * Created by monc2 on 2016-07-22.
 */
public class CardRegister extends BaseBackSearchActivity {
    String clause;
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
        setContentView(R.layout.webview_card_register);
        setBackEnable(true);
        webview = (WebView) findViewById(R.id.webview_card_register);

        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (url.contains("card_reg.php")) {
                    Toast.makeText(CardRegister.this, "success", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });


        String url = "https://inilite.inicis.com/inibill/inibill_card.jsp";
        String data = "returnurl=http://app.parkstem.com/api/card_reg.php&mid=hotelvey11&goodname=certification&price=1&type=1&orderid=AAAAA&notice=good&timestamp=20160427171717&period=0&hashdata=0c4b70d28e3dfbdf6561d3aff631f8355a3991c965223bd88285a8d9f8c0e935&p_not i=card_name";
        try {
            byte[] postdata = data.getBytes("UTF-8");
            webview.postUrl(url, postdata);
        } catch (Exception ex) {

        }
    }
}
