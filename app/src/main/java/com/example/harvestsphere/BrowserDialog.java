package com.example.harvestsphere;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.NonNull;


public class BrowserDialog extends Dialog {

    private WebView browser;
    private String search_query;

    public BrowserDialog(@NonNull Context context, String search_query) {
        super(context);
        this.search_query = search_query != null ? search_query : ""; // Ensure the search query is not null
    }

    public BrowserDialog(@NonNull Context context, int themeResId, String search_query) {
        super(context, themeResId);
        this.search_query = search_query != null ? search_query : ""; // Ensure the search query is not null
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser_dialog);
        browser = findViewById(R.id.browser);
        setupWebView();
    }

    private void setupWebView() {
        browser.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        browser.getSettings().setJavaScriptEnabled(true);
        loadSearchQuery();
    }

    private void loadSearchQuery() {
        String url = "https://www.google.com/search?q=" + search_query;
        browser.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        if (browser.canGoBack()) {
            browser.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
