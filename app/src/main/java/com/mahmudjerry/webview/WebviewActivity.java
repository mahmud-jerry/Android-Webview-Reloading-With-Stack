package com.mahmudjerry.webview;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.Stack;

public class WebviewActivity extends AppCompatActivity {
    private WebView webView ;
    private SwipeRefreshLayout swipe ;
    private AlertDialog builder;
    String mainUrl  = "http://notes.mahmud-jerry.com/";
    private Stack<String> urlStack ;
    private Boolean loadStatus = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_webview);
        urlStack = new Stack<>();
        urlStack.push("http://notes.mahmud-jerry.com/");

        swipe = findViewById(R.id.web_swipe_layout_id);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    LoadWeb();
                }catch (Exception e){
                    Toast.makeText(WebviewActivity.this, "Something went wrong! Restart App", Toast.LENGTH_SHORT).show();
                }

            }
        });

        LoadWeb();
    }

    public void LoadWeb(){
        loadStatus = true ;
        webView=findViewById(R.id.main_webview_id);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(WebView_Config.JavaScriptEnable());
        webSettings.setDomStorageEnabled(WebView_Config.DomStorageEnabled());
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webView.setWebViewClient(new UriWebViewClient());
        webView.setWebChromeClient(new UriChromeClient());
        webSettings.setSavePassword(WebView_Config.SavePassword());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(webView,true);
        }
        webSettings.setSaveFormData(WebView_Config.SaveFormData());
        webView.getSettings().setJavaScriptEnabled(WebView_Config.JavaScriptEnable());
        webView.getSettings().setAppCacheEnabled(WebView_Config.AppCache());
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportMultipleWindows(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setSupportZoom(WebView_Config.SupportZoom());
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        try{
            webView.loadUrl(urlStack.peek());
        }catch (Exception e){

        }
        swipe.setRefreshing(WebView_Config.SwipRefreshing());
        webView.setWebViewClient(new WebViewClient(){
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

                Log.e("log",description);
                loadStatus = false ;
                new AlertDialog.Builder(WebviewActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("No Internet Connection!")
                        .setMessage("Please check your connection and try again. ")
                        .setPositiveButton("RETRY", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try{
                                    LoadWeb();
                                }catch (Exception e){

                                }
                            }

                        })
                        .setNegativeButton("CANCEL", null)
                        .show();
                        webView.setVisibility(View.GONE);
                //webView.loadUrl(WebView_Config.ConnectionErrorView());
            }
            public  void  onPageFinished(WebView view, String url){
                if (loadStatus){
                    try{
                        webView.setVisibility(View.VISIBLE);
                    }catch (Exception e){

                    }
                }
                swipe.setRefreshing(false);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                try{
                    urlStack.push(url);
                }catch (Exception e){

                }
                try {
                    if(url.equals("mailto:mgstysf@gmail.com")){
                        LoadWeb();
                        redirectUsingCustomTab(url);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        try{
                            urlStack.pop();
                        }catch (Exception e){

                        }
                        webView.goBack();
                    } else {
                        new AlertDialog.Builder(this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Closing app")
                                .setMessage("Are you sure you want to close this app?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }

                                })
                                .setNegativeButton("No", null)
                                .show();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {

    }

    class UriChromeClient extends WebChromeClient {

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog,
                                      boolean isUserGesture, Message resultMsg) {
            webView = new WebView(WebviewActivity.this);
            webView.setVerticalScrollBarEnabled(false);
            webView.setHorizontalScrollBarEnabled(false);
            webView.setWebViewClient(new UriWebViewClient());
            webView.setWebChromeClient(new UriChromeClient());
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setSavePassword(true);

            webView.getSettings().setSaveFormData(true);


            builder = new AlertDialog.Builder(WebviewActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).create();


            builder.setTitle("");
            builder.setView(webView);

            builder.setButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    webView.destroy();
                    dialog.dismiss();

                }
            });

            builder.show();
            builder.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE| WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cookieManager.setAcceptThirdPartyCookies(webView,true);
            }

            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(webView);
            resultMsg.sendToTarget();

            return true;
        }

        @Override
        public void onCloseWindow(WebView window) {

            try {
                webView.destroy();
            } catch (Exception e) {

            }

            try {
                builder.dismiss();

            } catch (Exception e) {

            }


        }

    }

    private class UriWebViewClient extends WebViewClient {
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                       SslError error) {

        }
    }

    private void redirectUsingCustomTab(String url){
        Uri uri = Uri.parse(url);

        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();

        // set desired toolbar colors
        intentBuilder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));


        CustomTabsIntent customTabsIntent = intentBuilder.build();

        customTabsIntent.launchUrl(WebviewActivity.this, uri);
    }
}