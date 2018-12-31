package com.xajist.webview.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.xajist.webview.BuildConfig;
import com.xajist.webview.R;

public class Home extends Fragment {
    private static final String URL = "url";

    private String mUrl;
    private String mTitle = "";

    WebView webView;

    public Home() {
        // Biarin aja kosong
    }

    public static Home newInstance(String url) {
        Home fragment = new Home();
        Bundle args = new Bundle();
        args.putString(URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            mUrl = getArguments().getString(URL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.home, container, false);

        AdView mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest;
        if (BuildConfig.DEBUG){
            adRequest = new AdRequest.Builder()
                    .addTestDevice("81B9AB1B3C7C619CD8623D3A6E3E6170") // ID Device
                    .build();
        } else{
            adRequest = new AdRequest.Builder()
                    .build();
        }
        mAdView.loadAd(adRequest);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                intent.putExtra(Intent.EXTRA_SUBJECT, mTitle);
                intent.putExtra(Intent.EXTRA_TEXT, mTitle + "" + mUrl);
                Home.this.startActivity(Intent.createChooser(intent, "Bagikan Ke Teman"));
            }
        });

        webView = view.findViewById(R.id.webViewHome);

        // Enable Javascript and Other
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setBuiltInZoomControls(false);

        CookieManager.getInstance().setAcceptCookie(true);

        if (Build.VERSION.SDK_INT >= 21) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        }

        webView.setWebViewClient(new WebViewClient(){

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                view.loadUrl("file:///android_asset/no-internet-connection.html");
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url){
                if ((url.contains("mailto")) || (url.contains("sms")) || (url.contains("tel"))) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    Home.this.startActivity(intent);
                } else
                    webView.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (view.getTitle() != null )
                    mTitle = view.getTitle() + ", ";
            }
        });


        webView.loadUrl(mUrl);

        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction()!=KeyEvent.ACTION_DOWN)
                    return true;

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (webView.canGoBack())
                        webView.goBack();
                    return true;
                }
                return false;
            }
        });

        return view;
    }


}
