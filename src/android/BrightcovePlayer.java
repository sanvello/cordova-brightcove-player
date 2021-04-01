package com.brightcove.player;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;

public class BrightcovePlayer extends CordovaPlugin {

    private String brightcovePolicyKey = null;
    private String brightcoveAccountId = null;
    private CordovaWebView appView;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        appView = webView;
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("play")) {
            String accountId = args.getString(0);
            String policyKey = args.getString(1);
            String videoId = args.getString(2);
            this.play(accountId, policyKey, videoId, callbackContext);
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
            context.startActivity(intent);

            callbackContext.success("Playing now with Brightcove ID: " + videoId);
        } else {
            callbackContext.error("Empty video ID!");
        }
    }
}
