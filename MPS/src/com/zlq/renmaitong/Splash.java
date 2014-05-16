package com.zlq.renmaitong;

import com.zlq.mps.MainActivity;
import io.vov.vitamio.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends Activity{
	
	private final int SPLASH_DISPLAY_LENGHT = 1000; // �ӳٰ���

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);

		new Handler().postDelayed(new Runnable() {
			public void run() {
				Intent mainIntent = new Intent(Splash.this,
						MainActivity.class);
				Splash.this.startActivity(mainIntent);
				Splash.this.finish();
			}

		}, SPLASH_DISPLAY_LENGHT);

	}
}
