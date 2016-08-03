package com.howell.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.howell.action.AudioAction;
import com.howell.action.PlatformAction;
import com.howell.action.TurnProtocolMgr;
import com.howell.hiplaydemo.R;
import com.howell.jni.JniUtil;
import com.howell.utils.IConst;
import com.howell.utils.SSlSocketUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class MainActivity extends ActionBarActivity implements IConst{

	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case MSG_LOGIN_OK:
				Intent intent = new Intent(MainActivity.this,PlayerActivity.class);
				MainActivity.this.startActivity(intent);
				
				//https test
				break;
			case MSG_HTTPS_TEST:
				
				httpsTest();
				
				break;
				
			case MSG_SOCK_CONNECT:
			
				TurnProtocolMgr.getInstance().setContext(MainActivity.this)
				.setHander(handler)
				.connect2TurnService();
			
				break;
				
			case MSG_TURN_CONNECT_OK:
				
				TurnProtocolMgr.getInstance().subScribeCamStream();
				
				break;
			case MSG_TURN_CONNECT_FAIL:
				
				Log.e("123", "turn connect error");
				httpsExit();
				break;
			default:
				break;
			}
			
			super.handleMessage(msg);
		}
		
	};
	
	
	Button btn , btn1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mInit();
		PlatformAction.getInstance().setHandler(handler);
		btn = (Button)findViewById(R.id.main_btn_test);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				Intent intent = new Intent(MainActivity.this,PlayerActivity.class);
//				MainActivity.this.startActivity(intent);
				
//				JniUtil.getHI265Version();
//				JniUtil.transTest();
				PlatformAction.getInstance().loginPlatform(MainActivity.this);	//FIXME
				

			
				
				
				
//				File file = new File(MainActivity.this.getDir("assets", MODE_WORLD_READABLE).toString()+"/ca.crt");
//				Log.i("123", "path:"+file.getAbsolutePath());
//				if (!file.exists()) {
//					Log.e("123", "no exists");
//				}
				
				
			}
		});
		
		btn1 = (Button)findViewById(R.id.button1);
		btn1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//test https	
			}
		});	
	}

	

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	public void mInit(){
		//memory init
		JniUtil.YUVInit();
		JniUtil.netInit();
		JniUtil.nativeAudioInit();
		AudioAction.getInstance().initAudio();
	}
	
	/**
	 * https test just
	 */
	public void httpsTest(){
		SSlSocketUtil.getInstance().setHandler(handler);
		SSlSocketUtil.getInstance().init(this, TEST_IP, TEST_TURN_SERCICE_PORT);
		SSlSocketUtil.getInstance().connectSocket();
		
	}

	public void httpsExit(){
		SSlSocketUtil.getInstance().disConnectSocket();
	}
}
