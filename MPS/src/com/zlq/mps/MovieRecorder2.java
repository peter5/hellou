package com.zlq.mps;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Environment;
import android.view.SurfaceView;

public class MovieRecorder2 {
	private MediaRecorder mediarecorder;
	public boolean isRecording;

	public void startRecording(SurfaceView surfaceView, String videoName) {
		mediarecorder = new MediaRecorder();// 创建mediarecorder对象
		// 设置录制视频源为Camera(相机)
		mediarecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

		mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		// 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4
		mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		// 设置录制的视频编码h263 h264
		mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
		mediarecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
		// 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
//		mediarecorder.setVideoSize(SystemConfig.videoWidth, SystemConfig.videoHeight);
//		mediarecorder.setVideoSize(320, 240);
		// 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
//		mediarecorder.setVideoFrameRate(10);
		//time
//		mediarecorder.setMaxDuration(5*1000);
		mediarecorder.setPreviewDisplay(surfaceView.getHolder().getSurface());
		// 设置视频文件输出的路径
		lastFileName = Environment.getExternalStorageDirectory().getPath()+"/mps/"+videoName;
		File file = new File(lastFileName);
		if(!file.getParentFile().exists()){
			file.getParentFile().mkdirs();
		}
		mediarecorder.setOutputFile(lastFileName);
		
		try {
//			Camera.unlock();
			// 准备录制
			mediarecorder.prepare();
			// 开始录制
			mediarecorder.start();
		} catch (IllegalStateException e) {
			System.out.println("IllegalStateException--"+e.getMessage());
//			e.printStackTrace();
			release();
			return;
		} catch (IOException e) {
			System.out.println("IOException--"+e.getMessage());
//			e.printStackTrace();
			release();
			return;
		}
		isRecording = true;
	}



	private String lastFileName;

	public void stopRecording() {
		try {
			if (mediarecorder != null) {
				// 停止
					mediarecorder.stop();
					mediarecorder.release();
					mediarecorder = null;
					isRecording = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void release() {
		if (mediarecorder != null) {
			// 停止
				try {
					mediarecorder.setOnErrorListener(null);
					mediarecorder.setOnInfoListener(null);
//					mediarecorder.stop();
					mediarecorder.release();
					mediarecorder = null;

				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			
		}
	}
}

//private void releaseMediaRecorder()
//{
//    if (mMediaRecorder != null)
//    {
//        // 内部标识是否正在录像的变量，如果不需要可以去掉
//        if (isRecord)
//        {
//            try
//            {
//                mMediaRecorder.setOnErrorListener(null);
//                mMediaRecorder.setOnInfoListener(null);
//                // 停止
//                mMediaRecorder.stop();
//               
//            }
//            catch (RuntimeException e)
//            {
//                e.printStackTrace();
//            }
//        }}}
