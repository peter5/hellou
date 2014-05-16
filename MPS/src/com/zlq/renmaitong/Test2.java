package com.zlq.renmaitong;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.vov.vitamio.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class Test2 extends Activity{

    private static final String ImageView = null;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testcamera);
        Button btnCamera = (Button)findViewById(R.id.take);
        btnCamera.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                letCamera();
//                takePicture();
                System.out.println("tacke=========================");
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
        case 1:// ����
            if (resultCode == RESULT_OK) {
            	Bundle bundle = data.getExtras();
            	Bitmap bitmap = (Bitmap) bundle.get("data");// ��ȡ���ص���ݣ���ת��ΪBitmapͼƬ��ʽ
                Toast.makeText(this, "����ɹ�", Toast.LENGTH_SHORT).show();
                ((ImageView)findViewById(R.id.submit)).setImageBitmap(bitmap);
            }
            break;
        default:
            break;
        }
    }
    
    private void takePicture(){
    	Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    	startActivityForResult(intent, 1);
    }
    
	  protected void letCamera() {
	        // TODO Auto-generated method stub
	        Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	        String strImgPath = Environment.getExternalStorageDirectory()
	                .toString() + "/dlion/";// �����Ƭ���ļ���
	        String fileName = new SimpleDateFormat("yyyyMMddHHmmss")
	                .format(new Date()) + ".jpg";// ��Ƭ����
	        File out = new File(strImgPath);
	        if (!out.exists()) {
	            out.mkdirs();
	        }
	        out = new File(strImgPath, fileName);
	        strImgPath = strImgPath + fileName;// ����Ƭ�ľ��·��
	        System.out.println(strImgPath);
	        
	        Uri uri = Uri.fromFile(out);
	        imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
	        imageCaptureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
	        startActivityForResult(imageCaptureIntent, 1);
	    }
}
