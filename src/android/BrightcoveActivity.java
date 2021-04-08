package com.brightcove.player;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcoveExoPlayerVideoView;
import com.brightcove.player.view.BrightcovePlayer;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;

public class BrightcoveActivity extends BrightcovePlayer {

  private static final String BRIGHTCOVE_ACTIVITY_NAME = "player";
  private static final String BRIGHTCOVE_VIEW_NAME = "brightcove_video_view";

  private static final String LAYOUT_VIEW_KEY = "layout";
  private static final String ID_VIEW_KEY = "id";

  private static final String BACK_STATUS = "closed";
  private static final String COMPLETE_STATUS = "completed";

  private String brightcovePolicyKey;
  private String brightcoveAccountId;
  private String videoId;

  private String videoUrl;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    setContentView(this.getIdFromResources(BRIGHTCOVE_ACTIVITY_NAME, LAYOUT_VIEW_KEY));
    brightcoveVideoView = (BrightcoveExoPlayerVideoView) findViewById(this.getIdFromResources(BRIGHTCOVE_VIEW_NAME, ID_VIEW_KEY));

    brightcoveVideoView.addListener("completed",
      (e -> {
        this.sendCallback(COMPLETE_STATUS);
        this.finish();
      }));

    super.onCreate(savedInstanceState);
    super.fullScreen();
    super.setImmersive(true);

    Intent intent = getIntent();

    if (intent.hasExtra("video-url")) {
      this.videoUrl = intent.getStringExtra("video-url");
      this.playByUrl();
    } else {
      this.brightcovePolicyKey = intent.getStringExtra("brightcove-policy-key");
      this.brightcoveAccountId = intent.getStringExtra("brightcove-account-id");
      this.videoId = intent.getStringExtra("video-id");
      this.playById();
    }

  }

  private int getIdFromResources(String name, String defType) {
    String packageName = getApplication().getPackageName();
    Resources resources = getApplication().getResources();
    return resources.getIdentifier(name, defType, packageName);
  }

  private void playByUrl() {
    Video video = Video.createVideo(this.videoUrl);
    this.play(video);
  }

  private void playById() {
    EventEmitter eventEmitter = brightcoveVideoView.getEventEmitter();
    Catalog catalog = new Catalog(eventEmitter, this.brightcoveAccountId, this.brightcovePolicyKey);
    catalog.findVideoByID(this.videoId, new VideoListener() {

      @Override
      public void onVideo(Video video) {
        brightcoveVideoView.add(video);
        brightcoveVideoView.start();
      }
    });
  }

  private void play(Video video) {
    brightcoveVideoView.add(video);
    brightcoveVideoView.start();
  }

  @Override
  public void onBackPressed() {
    this.sendCallback(BACK_STATUS);
    this.finish();
  }

  private void sendCallback(String status) {
    CallbackContext callbackContext = CordovaBrightcoveCallbackUtil.getInstance().getCallbackContext();
    try {
      callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, this.callbackBuilder(status)));
    } catch (JSONException exception) {
      callbackContext.error("Error building callback");
    }
  }


  private JSONObject callbackBuilder(String status) throws JSONException {

    JSONObject jsonObject = new JSONObject();
    jsonObject.put("status", status);

    if (status.equals(COMPLETE_STATUS)) {
      // When completed current position gets set to 0
      jsonObject.put("percentage", 100);
    } else {
      jsonObject.put("percentage", this.getPlaybackPercentage());
    }

    return jsonObject;
  }

  private int getPlaybackPercentage() {
    return (brightcoveVideoView.getCurrentPosition() * 100) / brightcoveVideoView.getDuration();
  }
}
