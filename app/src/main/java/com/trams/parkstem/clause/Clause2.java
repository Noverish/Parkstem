package com.trams.parkstem.clause;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.trams.parkstem.server.ServerClient;

/**
 * Created by monc2 on 2016-07-21.
 */
public class Clause2 extends AppCompatActivity{
    ServerClient serverClient = ServerClient.getInstance();
    String clause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView webview = new WebView(this);
        setContentView(webview);

        try {
            clause = serverClient.clause("2");
            webview.loadData(clause, "text/html; charset=UTF-8", null);
        } catch (ServerClient.ServerErrorException ex) {
            ex.printStackTrace();
        }
    }
}
