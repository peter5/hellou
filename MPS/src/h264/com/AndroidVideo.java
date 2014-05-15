package h264.com;

import io.vov.vitamio.R;

import java.io.IOException;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.cn.rtmp.Main;
import com.cn.rtmp.RTMPConnectionUtil;
import com.cn.rtmp.UltraNetStream;
import com.smaxe.uv.client.INetStream;
import com.smaxe.uv.client.NetStream;
import com.smaxe.uv.client.camera.AbstractCamera;

public class AndroidVideo extends Activity {
	private TextView hour; // Сʱ
	private TextView minute; // ����
	private TextView second; // ��
	private Button mStart; // ��ʼ��ť
	private Button mStop; // ����ť
	private Button mReturn; // ���ذ�ť
	private static MyCamera aCamera;
	private boolean streaming;
	private boolean isTiming = false;
	
	//�Ƴ�
	public static int width;
	public static int height;

	public static boolean init;

	public static int blockWidth;
	public static int blockHeight;
	public static int timeBetweenFrames; // 1000 / frameRate
	public static int frameCounter;
	public static byte[] previous;
	
	
	static {
		System.loadLibrary("H264Android");
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// ������ĻΪȫ��
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.video);

		init();

	}

	private void init() {
		aCamera = new MyCamera(AndroidVideo.this);
		hour = (TextView) findViewById(R.id.mediarecorder_TextView01);
		minute = (TextView) findViewById(R.id.mediarecorder_TextView03);
		second = (TextView) findViewById(R.id.mediarecorder_TextView05);
		mStart = (Button) findViewById(R.id.mediarecorder_VideoStartBtn);
		mStop = (Button) findViewById(R.id.mediarecorder_VideoStopBtn);
		mReturn = (Button) findViewById(R.id.mediarecorder_VideoReturnBtn);
		// ��ʼ¼��
		mStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (streaming == false) {
					aCamera.start();
				}
				aCamera.startVideo();
				isTiming = true;
				handler.postDelayed(task, 1000);
				// ���ð�ť״̬
				mStart.setEnabled(false);
				mReturn.setEnabled(false);
				mStop.setEnabled(true);
			}
		});
		mReturn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (RTMPConnectionUtil.netStream != null) {
					RTMPConnectionUtil.netStream.close();
					RTMPConnectionUtil.netStream = null;
				}
				if (aCamera != null) {
					aCamera = null;

				}
				finish();
			}
		});
		mStop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				aCamera.stop();
				// ���ð�ť״̬
				mStart.setEnabled(true);
				mReturn.setEnabled(true);
				mStop.setEnabled(false);
				isTiming = false;
			}
		});
	}

	
	public class MyCamera extends AbstractCamera implements
			SurfaceHolder.Callback {

		public SurfaceView surfaceView;
		public SurfaceHolder surfaceHolder;
		public Camera camera;

//		public int width;
//		public int height;
//
//		public boolean init;
//
//		public int blockWidth;
//		public int blockHeight;
//		public int timeBetweenFrames; // 1000 / frameRate
//		public int frameCounter;
//		public byte[] previous;
		

		public MyCamera(Context context) {

			surfaceView = (SurfaceView) findViewById(R.id.mediarecorder_Surfaceview);
			surfaceView.setVisibility(View.VISIBLE);
			surfaceHolder = surfaceView.getHolder();
			surfaceHolder.addCallback(MyCamera.this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

			width = 352 / 2;
			height = 288 / 2;

			
			init = false;
			System.out.println("MyCamera()");
		}

		private void startVideo() {
			System.out.println( "startVideo()");

			RTMPConnectionUtil.ConnectRed5(AndroidVideo.this);
			RTMPConnectionUtil.netStream = new UltraNetStream(
					RTMPConnectionUtil.connection);
			RTMPConnectionUtil.netStream
					.addEventListener(new NetStream.ListenerAdapter() {

						@Override
						public void onNetStatus(final INetStream source,
								final Map<String, Object> info) {
							System.out.println("Publisher#NetStream#onNetStatus: "+ info);
							
							final Object code = info.get("code");
							if (NetStream.PUBLISH_START.equals(code)) {
								if (AndroidVideo.aCamera != null) {
									RTMPConnectionUtil.netStream.attachCamera(aCamera, -1 /* snapshotMilliseconds */);
									
									System.out.println("aCamera.start()");
									aCamera.start();
								} else {
									System.out.println("camera == null");
								}
							}
						}
					});

			RTMPConnectionUtil.netStream.publish(Main.fileName, NetStream.RECORD);
		}

		public void start() {
			camera.startPreview();
			streaming = true;
		}

		public void stop() {
			camera.stopPreview();
			streaming = false;
		}

		public void printHexString(byte[] b) {
			for (int i = 0; i < b.length; i++) {
				String hex = Integer.toHexString(b[i] & 0xFF);
				if (hex.length() == 1) {
					hex = '0' + hex;
				}

			}
		}

//		@Override
//		public void onPreviewFrame(byte[] arg0, Camera arg1) {
//			// TODO Auto-generated method stub
//			// if (!active) return;
//			if (!init) {
//				blockWidth = 32;
//				blockHeight = 32;
//				timeBetweenFrames = 100; // ֡���ʱ��
//				frameCounter = 0;		//֡����
//				previous = null;		//serfaceViewԤ��RGB���
//				init = true;
//			}
//			final long ctime = System.currentTimeMillis();
//
////			/** ���ɼ���YUV420SP���ת��ΪRGB��ʽ */
////			byte[] current = RemoteUtil.decodeYUV420SP2RGB(arg0, width, height);
////			try {
////				//
////
////				final byte[] packet = RemoteUtil.encode(current, previous,
////						blockWidth, blockHeight, width, height);
//				
//				//תΪh264����
//				int result=CompressBuffer(encoder, -1, arg0, arg0.length, h264Buff);
//				
//				fireOnVideoData(new MediaDataByteArray(timeBetweenFrames, new ByteArray(h264Buff)));
//				
//				previous = h264Buff;
//				if (++frameCounter % 10 == 0)
//					previous = null;
//
//			
//			//���ֹͣ��֡���ʱ��
//			final int spent = (int) (System.currentTimeMillis() - ctime);
//			try {
//
//				Thread.sleep(Math.max(0, timeBetweenFrames - spent));
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// TODO Auto-generated method stub
			// camera.startPreview();
			// camera.unlock();

			// Log.d("DEBUG", "surfaceChanged()");

			// startVideo();
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			camera = Camera.open();
			try {
				camera.setPreviewDisplay(surfaceHolder);
				camera.setPreviewCallback(new H264Encoder(width, height));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				camera.release();
				camera = null;
			}

			Log.d("DEBUG", "surfaceCreated()");
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			if (camera != null) {
				camera.stopPreview();
				camera.release();
				camera = null;
			}

		}
	}

	/* ��ʱ�����ã�ʵ�ּ�ʱ */
	private Handler handler = new Handler();
	int s, h, m, s1, m1, h1;
	private Runnable task = new Runnable() {
		public void run() {
			if (isTiming) {
				handler.postDelayed(this, 1000);
				s++;
				if (s < 60) {
					second.setText(format(s));
				} else if (s < 3600) {
					m = s / 60;
					s1 = s % 60;
					minute.setText(format(m));
					second.setText(format(s1));
				} else {
					h = s / 3600;
					m1 = (s % 3600) / 60;
					s1 = (s % 3600) % 60;
					hour.setText(format(h));
					minute.setText(format(m1));
					second.setText(format(s1));
				}
			}
		}
	};

	/* ��ʽ��ʱ�� */
	public String format(int i) {
		String s = i + "";
		if (s.length() == 1) {
			s = "0" + s;
		}
		return s;
	}
	
	
}

class H264Encoder implements Camera.PreviewCallback {
	long encoder=0;
	byte[] h264Buff =null;
	
	public H264Encoder(int width, int height) {
		encoder = CompressBegin(width, height);
		h264Buff = new byte[width * height *8];
	};
	
	//����
	protected void finalize()
    {
		CompressEnd(encoder);
		try {
			super.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	//���ط���
	private native long CompressBegin(int width,int height);
	private native int CompressBuffer(long encoder, int type,byte[] in, int insize,byte[] out);
	private native int CompressEnd(long encoder);
	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {	
		if (!AndroidVideo.init) {
			AndroidVideo.blockWidth = 32;
			AndroidVideo.blockHeight = 32;
			AndroidVideo.timeBetweenFrames = 100; // ֡���ʱ��
			AndroidVideo.frameCounter = 0;		//֡����
			AndroidVideo.previous = null;		//serfaceViewԤ��RGB���
			AndroidVideo.init = true;
		}
		final long ctime = System.currentTimeMillis();

		int result=CompressBuffer(encoder, -1, data, data.length, h264Buff);
		
//		fireOnVideoData(new MediaDataByteArray(timeBetweenFrames, new ByteArray(h264Buff)));
		
		AndroidVideo.previous = h264Buff;
		if (++AndroidVideo.frameCounter % 10 == 0)
			AndroidVideo.previous = null;

	
	//���ֹͣ��֡���ʱ��
	final int spent = (int) (System.currentTimeMillis() - ctime);
	try {
		Thread.sleep(Math.max(0, AndroidVideo.timeBetweenFrames - spent));
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
}

