package com.zlq.renmaitong;

import io.vov.vitamio.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;

public class RestPasswordSuccess extends Activity{

	private ImageButton resetSuccessback;
	private ImageView resetSuccessbackMV;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_resetpassword_success);
		
		resetSuccessback = (ImageButton) findViewById(R.id.resetPasswordSuccessback);
		resetSuccessbackMV = (ImageView) findViewById(R.id.resetPasswordSuccessBackMV);
		resetSuccessback.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		resetSuccessbackMV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
