package com.trams.parkstem.webview;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.trams.parkstem.R;
import com.trams.parkstem.base_activity.BaseBackSearchActivity;
import com.trams.parkstem.server.ServerClient;

/**
 * Created by monc2 on 2016-07-22.
 */
public class Mobilecertification extends BaseBackSearchActivity {
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
        setContentView(R.layout.webview_card_register);
        setBackEnable(true);
        webview = (WebView) findViewById(R.id.webview_card_register);

        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (url.contains("mobile.php")) {
                    try {
                        if(ServerClient.getInstance().memberInfo().certification)
                            Toast.makeText(Mobilecertification.this, "휴대폰 인증에 성공했습니다", Toast.LENGTH_SHORT).show();
                    } catch (ServerClient.ServerErrorException ex) {
                        Toast.makeText(Mobilecertification.this, "휴대폰 인증에 실패했습니다 - " + ex.msg, Toast.LENGTH_SHORT).show();
                    }
                    finish();
                }
//                if (Uri.parse(url).getHost().equals("app.parkstem.com/api/kmcis_mobile.php")) {
//                    try{
//                        URL aURL = new URL(url);
//                        URLConnection conn = aURL.openConnection();
//                        conn.connect();
//
//                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//
//                        String line;
//                        jsonStr = "";
//
//                        while ((line = reader.readLine()) != null) {
//                            jsonStr += line + "\n";
//                        }
//                        reader.close();
//
//                        result = new JSONObject(jsonStr);
//
//                        Toast.makeText(mobile_cert.this, "success", Toast.LENGTH_LONG).show();
//
//                    } catch(MalformedURLException ex){
//                        ex.printStackTrace();
//                    } catch(IOException ex){
//                        ex.printStackTrace();
//                    } catch( JSONException ex){
//                        ex.printStackTrace();
//                    }
//                }
            }

//            @Override
//            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request){
//                if (url.contains(".js")) {
//                    return getWebResourceResponseFromString();
//                } else {
//                    return super.shouldInterceptRequest(view, url);
//                }
//            }
//            private WebResourceResponse getWebResourceResponseFromString() {
//                return getUtf8EncodedWebResourceResponse(new StringBufferInputStream("alert('!NO!')"));
//            }
//            private WebResourceResponse getUtf8EncodedWebResourceResponse(InputStream data) {
//                return new WebResourceResponse("text/css", "UTF-8", data);
//            }
        });


        webview.loadUrl("http://app.parkstem.com/api/kmcis_start.php?uniqueID=" + serverClient.getUniqueID());
    }
}
