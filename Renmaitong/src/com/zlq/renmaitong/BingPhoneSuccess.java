package com.zlq.renmaitong;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;

public class BingPhoneSuccess extends Activity{

	private ImageButton back;
	private ImageView backImageView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_bindphonenumber_success);
		
		back = (ImageButton) findViewById(R.id.bindSuccessback);
		backImageView = (ImageView) findViewById(R.id.bindSuccessBackMV);
		backImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
