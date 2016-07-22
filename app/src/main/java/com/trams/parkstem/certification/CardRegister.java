package com.trams.parkstem.certification;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.trams.parkstem.server.ServerClient;

/**
 * Created by monc2 on 2016-07-22.
 */
public class CardRegister extends AppCompatActivity {
    ServerClient serverClient = new ServerClient();
    String clause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView webview = new WebView(this);
        setContentView(webview);

        webview = new WebView(this);
        setContentView(webview);
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
