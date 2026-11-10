package com.lazy.schedule;

import android.os.Bundle;
import android.webkit.WebView;

import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView webView = getBridge().getWebView();
        if (webView != null) {
            webView.getSettings().setJavaScriptEnabled(true);
            webView.addJavascriptInterface(new ScheduleBridge(this), "ScheduleBridge");
        }
    }
}
