package com.example.myapp.en;

import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import androidx.annotation.NonNull;

/**
 * Main activity that hosts a WebView loading the target website.
 * Features:
 * - JavaScript & DOM storage enabled
 * - Pull-to-refresh via SwipeRefreshLayout
 * - JavaScript interface "Android" for native bridge
 * - Banner ad at bottom of screen
 * - Interstitial ad triggered via JS interface
 */
public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private InterstitialAd mInterstitialAd;

    // Banner Ad Unit ID
    private static final String BANNER_AD_UNIT_ID = "ca-app-pub-0000000000000000/0000000000";
    // Interstitial/Video Ad Unit ID
    private static final String INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-0000000000000000/0000000000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Mobile Ads SDK
        MobileAds.initialize(this, initializationStatus -> {});

        // Setup WebView
        webView = findViewById(R.id.webView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        webView.setWebChromeClient(new WebChromeClient());

        // Add JavaScript interface for native communication
        webView.addJavascriptInterface(
            new WebAppInterface(this, this::showInterstitialAd),
            "Android"
        );

        // Load the website
        webView.loadUrl("https://visual-melody.lovable.app");

        // Pull-to-refresh
        swipeRefreshLayout.setOnRefreshListener(() -> webView.reload());

        // === Banner Ad ===
        // Banner ads are small ads displayed at the bottom of the screen.
        // They auto-refresh and don't interrupt the user experience.
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        // === Interstitial Ad ===
        // Interstitial ads are full-screen ads shown at natural transition points.
        // They must be pre-loaded and shown when triggered (e.g., via JS interface).
        loadInterstitialAd();
    }

    /**
     * Pre-loads an interstitial ad so it's ready when triggered.
     * Must be called again after each display to reload the next ad.
     */
    private void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, INTERSTITIAL_AD_UNIT_ID, adRequest,
            new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    mInterstitialAd = interstitialAd;
                    // Set callback to reload ad after it's dismissed
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            mInterstitialAd = null;
                            loadInterstitialAd(); // Reload for next use
                        }
                    });
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    mInterstitialAd = null;
                }
            });
    }

    /**
     * Shows the pre-loaded interstitial ad if available.
     * Called from WebAppInterface when JS triggers showVideoAd().
     */
    public void showInterstitialAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(this);
        }
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
