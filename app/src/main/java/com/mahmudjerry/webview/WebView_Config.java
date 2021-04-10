package com.mahmudjerry.webview;

public class WebView_Config {

    public static String HomeWebView(){
        final  String URL="http://notes.mahmud-jerry.com/";
        return URL;
    }
    //Error Page When Network Connection Fail
    public static String ConnectionErrorView(){
        final String URL="file:///android_asset/connection_fail/error.html";
        return URL;
    }
    // WebView Settings Start Form Here....
    public  static boolean JavaScriptEnable(){
        final boolean JavaScript=true;
        return JavaScript;
    }
    public static boolean DomStorageEnabled(){
        final boolean DomStorage=true;
        return DomStorage;
    }
    public static boolean SwipRefreshing(){
        final boolean Refreshing=true;
        return Refreshing;
    }
    public static boolean SavePassword(){
        final boolean SavePass=true;
        return SavePass;
    }
    public  static boolean SaveFormData(){
        final boolean SaveForm=true;
        return SaveForm;
    }
    public static boolean AppCache(){
        final boolean CacheYesNot=true;
        return CacheYesNot;
    }
    public static boolean SupportZoom(){
        final boolean ZoomYesNot=true;
        return ZoomYesNot;
    }

}
