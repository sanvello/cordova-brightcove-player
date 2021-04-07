package com.brightcove.player;

import org.apache.cordova.CallbackContext;

public class CordovaBrightcoveCallbackUtil {
  private CallbackContext callbackContext;

  private static class CordovaBrightcoveCallbackUtilHolder {
    public final static CordovaBrightcoveCallbackUtil INSTANCE = new CordovaBrightcoveCallbackUtil();
  }

  private CordovaBrightcoveCallbackUtil() {

  }

  public static final CordovaBrightcoveCallbackUtil getInstance() {
    return CordovaBrightcoveCallbackUtilHolder.INSTANCE;
  }

  public CallbackContext getCallbackContext() {
    return callbackContext;
  }

  public void setCallbackContext(CallbackContext callbackContext) {
    this.callbackContext = callbackContext;
  }
}
