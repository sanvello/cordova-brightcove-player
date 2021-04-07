package com.brightcove.player;

import android.content.Context;
import android.content.Intent;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

public class BrightcovePlayer extends CordovaPlugin {
  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);
  }

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    if (action.equals("playById")) {
      String accountId = args.getString(0);
      String policyKey = args.getString(1);
      String videoId = args.getString(2);
      this.play(accountId, policyKey, videoId, callbackContext);
      return true;
    } else if (action.equals("playByUrl")) {
      String url = args.getString(0);
      this.play(url, callbackContext);
      return true;
    }

    return false;
  }

  private void play(String accountId, String policyKey, String videoId, CallbackContext callbackContext) {
    if (accountId == null || policyKey == null || videoId == null) {
      callbackContext.error("Wrong input parameters");
      return;
    }

    if (videoId != null && videoId.length() > 0) {
      Context context = this.cordova.getActivity().getApplicationContext();

      Intent intent = new Intent(context, BrightcoveActivity.class);
      intent.putExtra("video-id", videoId);
      intent.putExtra("brightcove-policy-key", policyKey);
      intent.putExtra("brightcove-account-id", accountId);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

      CordovaBrightcoveCallbackUtil.getInstance().setCallbackContext(callbackContext);

      context.startActivity(intent);
    } else {
      callbackContext.error("Empty video ID!");
    }
  }

  private void play(String url, CallbackContext callbackContext) {
    if (url == null || url.isEmpty()) {
      callbackContext.error("Wrong input parameters");
      return;
    }

    if (!url.contains("https")) {
      callbackContext.error("Url needs to contain https");
      return;
    }
    Context context = this.cordova.getActivity().getApplicationContext();

    Intent intent = new Intent(context, BrightcoveActivity.class);
    intent.putExtra("video-url", url);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

    CordovaBrightcoveCallbackUtil.getInstance().setCallbackContext(callbackContext);

    context.startActivity(intent);
  }
}
