package com.howell.activity;



import com.howell.action.AudioAction;
import com.howell.action.PlayerManager;
import com.howell.action.YV12Renderer;
import com.howell.hiplaydemo.R;
import com.howell.utils.IConst;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.hardware.camera2.params.MeteringRectangle;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;

public class PlayerActivity extends Activity implements Callback,IConst{


	private GLSurfaceView mGlView;
	private PlayerManager playMgr = PlayerManager.getInstance();
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case MSG_LOGIN_CAM_OK:
				Log.i("123", "msg get login cam ok");
				playMgr.playViewCam();
				AudioAction.getInstance().playAudio();
				break;
			case MSG_CONNECT_OK:
				
				
				
				break;
				
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);
		init();
		//TODO 连接 播放。。。。
		playMgr.setHandler(handler);
		playMgr.setContext(this);
		playMgr.loginCam();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		playMgr.stopViewCam();
		AudioAction.getInstance().stopAudio();
		playMgr.logoutCam();
		super.onDestroy();
	}
	
	public void init(){
		mGlView = (GLSurfaceView)findViewById(R.id.glsurface_view);
		mGlView.setEGLContextClientVersion(2);
		mGlView.setRenderer(new YV12Renderer(this,mGlView,handler));
		mGlView.getHolder().addCallback((Callback) this);
		mGlView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.i("123", "suface create");
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// TODO Auto-generated method stub
		Log.i("123", "surface change");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.i("123", "surface destroy");
	}

}
