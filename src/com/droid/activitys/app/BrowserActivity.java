package com.droid.activitys.app;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import com.droid.R;

public class BrowserActivity extends Activity {

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browser);

        mWebView = (WebView) findViewById(R.id.webView);
        // включаем поддержку JavaScript
        mWebView.getSettings().setJavaScriptEnabled(true);
        // указываем страницу загрузки
        mWebView.loadUrl("http://www.mvideo.ru/");
    }

    }