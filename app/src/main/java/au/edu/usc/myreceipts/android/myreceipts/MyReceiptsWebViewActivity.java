package au.edu.usc.myreceipts.android.myreceipts;


import android.os.Bundle;


import android.support.v7.app.AppCompatActivity;

import android.webkit.WebView;
import android.webkit.WebViewClient;


public class MyReceiptsWebViewActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        webView = findViewById(R.id.myReceipts_webview);
        webView.setWebViewClient(new WebViewClient()); // opens url
        webView.loadUrl("https://en.wikipedia.org/wiki/Receipt");
    }
}