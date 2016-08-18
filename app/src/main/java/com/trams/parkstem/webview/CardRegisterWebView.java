package com.trams.parkstem.webview;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
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
public class CardRegisterWebView extends BaseBackSearchActivity {
    ServerClient serverClient = ServerClient.getInstance();
    String uid = serverClient.getUniqueID();
    String cardName = "NEW_CARD!"; //얘를 카드이름으로 지정
    WebView webview;


    @Override
    public void onBackPressed() {
        if (webview.canGoBack()) {
            Toast.makeText(this, "카드 등록시 뒤로가기를 하실 수 없습니다", Toast.LENGTH_LONG).show();
            finish();
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
                if (url.contains("card_reg.php")) {
                    Toast.makeText(CardRegisterWebView.this, "카드를 등록하였습니다.", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });

        cardName = getIntent().getStringExtra("cardName");


        Log.e("data","cardName : " + cardName + ", uid : " + uid);

        String url = "https://inilite.inicis.com/inibill/inibill_card.jsp";
        String data = "returnurl=http://app.parkstem.com/api/card_reg.php&mid=hotelvey11&goodname=certification&price=1&type=1&orderid=AAAAA&notice=good&timestamp=20160427171717&buyername=1&period=0&hashdata=0c4b70d28e3dfbdf6561d3aff631f8355a3991c965223bd88285a8d9f8c0e935&p_noti=" + uid + "^" + cardName;
        try {
            byte[] postdata = data.getBytes("UTF-8");
            webview.postUrl(url, postdata);
        } catch (Exception ex) {

        }
    }
}
