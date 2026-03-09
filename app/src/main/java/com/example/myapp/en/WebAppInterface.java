package com.example.myapp.en;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

/**
 * JavaScript interface exposed to the WebView as "Android".
 * 
 * Usage from JavaScript:
 *   Android.showToast("Hello from web!");
 *   Android.showVideoAd();
 */
public class WebAppInterface {

    private Context mContext;
    private Runnable showAdCallback;

    public WebAppInterface(Context context, Runnable showAdCallback) {
        this.mContext = context;
        this.showAdCallback = showAdCallback;
    }

    /**
     * Show a native Android Toast message.
     * Called from JS: Android.showToast("message")
     */
    @JavascriptInterface
    public void showToast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Trigger an interstitial/video ad.
     * Called from JS: Android.showVideoAd()
     */
    @JavascriptInterface
    public void showVideoAd() {
        if (showAdCallback != null) {
            ((android.app.Activity) mContext).runOnUiThread(showAdCallback);
        }
    }
}
