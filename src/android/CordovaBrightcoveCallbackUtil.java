package com.brightcove.player;

import org.apache.cordova.CallbackContext;

public class CordovaBrightcoveCallbackUtil {
    public final static CordovaBrightcoveCallbackUtil INSTANCE = new CordovaBrightcoveCallbackUtil();
    private CallbackContext callbackContext;

    private CordovaBrightcoveCallbackUtil() {
    }
    public static final CordovaBrightcoveCallbackUtil getInstance() {
        return INSTANCE;
    }
    public CallbackContext getCallbackContext() {
        return callbackContext;
    }
    public void setCallbackContext(CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
    }
}