package com.trams.parkstem.webview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.trams.parkstem.R;
import com.trams.parkstem.server.ServerClient;

/**
 * Created by monc2 on 2016-07-21.
 */
public class ClauseWebView extends AppCompatActivity{
    public static final String CLAUSE_EXTRA = "clause";
    ServerClient serverClient = ServerClient.getInstance();
    String clause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        WebView webview = (WebView) findViewById(R.id.webview);
        Button backButton = (Button) findViewById(R.id.werbview_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClauseWebView.this.finish();
            }
        });

        int clauseNumber = getIntent().getIntExtra(CLAUSE_EXTRA, 0);

        if(clauseNumber != 0) {
            try {
                clause = serverClient.clause(clauseNumber + "");
                webview.loadData(clause, "text/html; charset=UTF-8", null);
            } catch (ServerClient.ServerErrorException ex) {
                ex.printStackTrace();
            }
        } else {
            Toast.makeText(this, "fatal error! - never happen", Toast.LENGTH_SHORT).show();
        }
    }
}
