package au.edu.usc.myreceipts.android.myreceipts;


import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class MyReceiptsWebViewActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        webView = findViewById(R.id.myReceipts_webview);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false; //enables activity to handle webView.loadUrl requests

            }
        });

        mProgressBar = findViewById(R.id.progressbar);
        mProgressBar.setMax(100); // report progress range from 0-100
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView webview, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                }
            }

            public void onReceivedTitle(WebView webView, String title) {
                getSupportActionBar().setSubtitle(title);
            }

        });

        webView.loadUrl("https://en.wikipedia.org/wiki/Receipt");
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}