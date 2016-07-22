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
public class Mobilecertification extends AppCompatActivity {
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
                if (url.contains("mobile.php")) {
                    Toast.makeText(Mobilecertification.this, "success", Toast.LENGTH_LONG).show();
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


        webview.loadUrl("http://app.parkstem.com/api/kmcis_start.php?uniqueID=" + serverClient.uniqueID);
    }
}
