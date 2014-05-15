package com.zlq.renmaitong;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.cn.rtmp.Main;
import com.zlq.util.SystemConfig;

import android.media.MediaRecorder;
import android.os.Environment;
import android.view.SurfaceView;

public class MovieRecorder {
	private MediaRecorder mediarecorder;
	boolean isRecording;

	public void startRecording(SurfaceView surfaceView) {
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
		mediarecorder.setMaxDuration(Integer.parseInt(SystemConfig.videosecond)*1000);
		mediarecorder.setPreviewDisplay(surfaceView.getHolder().getSurface());
		// 设置视频文件输出的路径
		lastFileName = Environment.getExternalStorageDirectory().getPath()+"/myImage/"+Main.fileName;
		mediarecorder.setOutputFile(lastFileName);
		
		try {
			// 准备录制
			mediarecorder.prepare();
			// 开始录制
			mediarecorder.start();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		isRecording = true;
	}



	private String lastFileName;

	public void stopRecording() {
		if (mediarecorder != null) {
			// 停止
			mediarecorder.stop();
			mediarecorder.release();
			mediarecorder = null;
			isRecording = false;
//			timer.cancel();
		}
	}

	public void release() {
		if (mediarecorder != null) {
			// 停止
			mediarecorder.stop();
			mediarecorder.release();
			mediarecorder = null;
		}
	}
}
