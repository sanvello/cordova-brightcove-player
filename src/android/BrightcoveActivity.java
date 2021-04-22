package com.brightcove.player;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.mediacontroller.BrightcoveMediaControlRegistry;
import com.brightcove.player.mediacontroller.BrightcoveMediaController;
import com.brightcove.player.mediacontroller.buttons.AudioTracksButtonController;
import com.brightcove.player.mediacontroller.buttons.ButtonController;
import com.brightcove.player.mediacontroller.buttons.FullScreenButtonController;
import com.brightcove.player.model.DeliveryType;
import com.brightcove.player.model.SourceCollection;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcoveExoPlayerVideoView;
import com.brightcove.player.view.BrightcovePlayer;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrightcoveActivity extends BrightcovePlayer {

    private static final String BRIGHTCOVE_ACTIVITY_NAME = "player";
    private static final String BRIGHTCOVE_VIEW_NAME = "brightcove_video_view";

    private static final String LAYOUT_VIEW_KEY = "layout";
    private static final String ID_VIEW_KEY = "id";

    private static final String BACK_STATUS = "closed";
    private static final String COMPLETE_STATUS = "completed";
    private static final String OFFLINE_STATUS = "OFFLINE";

    private ProgressBar progressBar;
    private Button onScreenBackButton;

    private CountDownTimer timeout;
    private boolean offline = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(this.getIdFromResources(BRIGHTCOVE_ACTIVITY_NAME, LAYOUT_VIEW_KEY));
        brightcoveVideoView = (BrightcoveExoPlayerVideoView) findViewById(this.getIdFromResources(BRIGHTCOVE_VIEW_NAME, ID_VIEW_KEY));
        BrightcoveMediaController mediaController = new BrightcoveMediaController(brightcoveVideoView);
        BrightcoveMediaControlRegistry registry = mediaController.getMediaControlRegistry();
        List<ButtonController> controllers = registry.getButtonControllers();
        registry.clear();

        for (ButtonController buttonController : controllers) {
            if (!buttonController.getClass().equals(FullScreenButtonController.class)) {
                registry.register(buttonController);
            }
        }

        this.progressBar = findViewById(this.getIdFromResources("progressBar", ID_VIEW_KEY));
        this.onScreenBackButton = findViewById(this.getIdFromResources("button1", ID_VIEW_KEY));
        this.onScreenBackButton.setOnClickListener(e -> {
            this.onPlayerFinish(BACK_STATUS);
        });

        brightcoveVideoView.addListener("completed",
                (e -> {
                    this.onPlayerFinish(COMPLETE_STATUS);
                }));

        brightcoveVideoView.addListener("didPlay",
                (e -> {
                    this.progressBar.setVisibility(View.GONE);
                    if (this.offline) {
                        this.offline = false;
                        this.timeout.cancel();
                    }
                }));

        brightcoveVideoView.getBrightcoveMediaController().addListener("hideMediaControls", e -> {
            this.onScreenBackButton.setVisibility(View.GONE);
        });
        brightcoveVideoView.getBrightcoveMediaController().addListener("showMediaControls", e -> {
            this.onScreenBackButton.setVisibility(View.VISIBLE);
        });
        brightcoveVideoView.getBrightcoveMediaController().addListener("didHideMediaControls", e -> {
            this.onScreenBackButton.setVisibility(View.GONE);
        });
        brightcoveVideoView.getBrightcoveMediaController().addListener("didShowMediaControls", e -> {
            this.onScreenBackButton.setVisibility(View.VISIBLE);
        });
        brightcoveVideoView.getBrightcoveMediaController().addListener("bufferingCompleted", event -> {
            this.progressBar.setVisibility(View.GONE);
        });

        brightcoveVideoView.getBrightcoveMediaController().addListener("bufferingStarted", event -> {
            this.progressBar.setVisibility(View.VISIBLE);
        });

        brightcoveVideoView.getBrightcoveMediaController().addListener("error", event -> {
            if (!this.offline) {
                this.offline = true;
                this.progressBar.setVisibility(View.VISIBLE);
                this.timeout = new CountDownTimer(60000, 5000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        brightcoveVideoView.start();
                    }

                    @Override
                    public void onFinish() {
                        onPlayerFinish(OFFLINE_STATUS);
                    }
                }.start();
            }
        });

        super.onCreate(savedInstanceState);
        super.fullScreen();
        super.setImmersive(true);


        Intent intent = getIntent();

        if (intent.hasExtra("video-url")) {
            String videoUrl = intent.getStringExtra("video-url");
            this.playByUrl(videoUrl);
        } else {
            String brightcovePolicyKey = intent.getStringExtra("brightcove-policy-key");
            String brightcoveAccountId = intent.getStringExtra("brightcove-account-id");
            String videoId = intent.getStringExtra("video-id");
            this.playById(brightcovePolicyKey, brightcoveAccountId, videoId);
        }

    }

    private int getIdFromResources(String name, String defType) {
        String packageName = getApplication().getPackageName();
        Resources resources = getApplication().getResources();
        return resources.getIdentifier(name, defType, packageName);
    }

    private void playByUrl(String videoUrl) {
        Video video = Video.createVideo(videoUrl);
        this.play(video);
    }

    private void playById(String brightcovePolicyKey, String brightcoveAccountId, String videoId) {
        EventEmitter eventEmitter = brightcoveVideoView.getEventEmitter();
        Catalog catalog = new Catalog(eventEmitter, brightcoveAccountId, brightcovePolicyKey);
        catalog.findVideoByID(videoId, new VideoListener() {

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
        this.onPlayerFinish(BACK_STATUS);
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

    public void onPlayerFinish(String finishStatus) {
        if (this.offline) {
            this.timeout.cancel();
        }
        this.sendCallback(finishStatus);
        this.finish();
    }
}
