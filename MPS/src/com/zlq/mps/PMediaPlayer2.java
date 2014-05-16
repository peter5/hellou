package com.zlq.mps;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.R;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;

public class PMediaPlayer2 extends Activity {

	VideoView videoView;
	SurfaceHolder hholder;
	int v_width;
	int v_height;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		setContentView(R.layout.my_videoview);
		videoView = (VideoView) this.findViewById(R.id.myVideoView);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		v_width = dm.widthPixels - 150;
		v_height = dm.heightPixels - 44;
		Intent intent = getIntent();
		String path = intent.getStringExtra("url");

		videoView.setVideoPath(path);
		videoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_LOW);
		hholder = videoView.getHolder();
		hholder.setSizeFromLayout();
//		hholder.setFixedSize(v_width, v_height);
//		videoView.set
		videoView.setMediaController(new MediaController(this));

		//
		// mPreview = (SurfaceView) findViewById(R.id.myVideoView);
		// holder = mPreview.getHolder();
		// holder.addCallback(this);
	}

}
