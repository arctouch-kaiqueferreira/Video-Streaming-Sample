package com.example.videostreamingsample;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    public static final String VIDEO_URL = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    private CustomVideoView videoView;

    private int displayWidth;
    private int smallHeight;
    private int displayHeight;
    private int actionBarHeight;

    private Button buttonPlayVideo;
    private ProgressDialog progressDialog;
    private MediaController mediaController;
    private TextView txtDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = (CustomVideoView) findViewById(R.id.playvideo_content);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        displayHeight = metrics.heightPixels;
        displayWidth = metrics.widthPixels;
        smallHeight = displayHeight/3;

        TypedValue typedValue = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(typedValue.data, getResources().getDisplayMetrics());
        }


        buttonPlayVideo = (Button) findViewById(R.id.button_play_video);
        buttonPlayVideo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                playVideoStream(VIDEO_URL);
            }
        });

        txtDescription = (TextView) findViewById(R.id.txtDescription);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void playVideoStream(String url) {
        if (videoView.isPlaying() && videoView.canPause()) {
            videoView.stopPlayback();
            buttonPlayVideo.setText(getResources().getString(R.string.load_video_button));
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            videoView.setDimensions(displayWidth, smallHeight);

            mediaController = new MediaController(this);

            Uri videoUri = Uri.parse(url);
            videoView.setMediaController(mediaController);
            videoView.setVideoURI(videoUri);

            buttonPlayVideo.setText(getResources().getString(R.string.stop_video_button));
            progressDialog = ProgressDialog.show(this, "", "Loading video", true);
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                public void onPrepared(MediaPlayer mediaPlayer) {
                    progressDialog.dismiss();
                    videoView.start();
                    mediaController.setAnchorView(videoView);
                }
            });
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) videoView.getLayoutParams();
            videoView.setLayoutParams(params);
            getActionBar().hide();

            videoView.setDimensions(displayHeight + actionBarHeight, displayWidth);
            videoView.getHolder().setFixedSize(displayHeight + actionBarHeight, displayWidth);

            buttonPlayVideo.setVisibility(View.GONE);
            txtDescription.setVisibility(View.GONE);
        } else {
            getActionBar().show();
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) videoView.getLayoutParams();
            videoView.setLayoutParams(params);

            videoView.setDimensions(displayWidth, smallHeight);
            videoView.getHolder().setFixedSize(displayWidth, smallHeight);

            buttonPlayVideo.setVisibility(View.VISIBLE);
            txtDescription.setVisibility(View.VISIBLE);
        }
    }

}
