package com.howell.action;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.security.auth.login.LoginException;

import org.kobjects.util.Util;

import com.howell.jni.JniUtil;
import com.howell.utils.IConst;
import com.howell.utils.JsonUtil;
import com.howell.utils.SDcardUtil;
import com.howell.utils.Utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.util.Log;
import bean.Subscribe;

public class PlayerManager implements IConst{
	Handler handler;
	private PlayerManager(){}
	private static PlayerManager mInstance = null;
	public static PlayerManager getInstance(){
		if(mInstance==null){
			mInstance = new PlayerManager();
		}
		return mInstance;
	}
	public void setHandler(Handler handler){
		this.handler = handler;
	}
	private long dialogId = 0;
	private String turnServiceIP = null;
	private int turnServicePort = -1;
	private String sessionID = null;
	private Context context;
	public void setContext(Context context){
		this.context = context;
	}
	
	public void onConnect(String sessionId){
		Log.i("123", "session id = "+sessionId);
		sessionID = sessionId;
		handler.sendEmptyMessage(MSG_LOGIN_CAM_OK);
	}
	public long getDialogId(){
		this.dialogId++;
		return dialogId;
	}
	

	
	
	public void	loginCam(){
//		turnServiceIP = PlatformAction.getInstance().getTurnServerIP();
		turnServiceIP = TEST_IP;
		turnServicePort = PlatformAction.getInstance().getTurnServerPort();
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				
				JniUtil.netInit();
				
				JniUtil.transInit(turnServiceIP, TEST_TURN_SERCICE_PORT);//FIXME 8862 test
				JniUtil.transSetCallBackObj(PlayerManager.this, 0);
				JniUtil.transSetCallbackMethodName("onConnect", 0);
				int type = 101;
				
				InputStream ca = getClass().getResourceAsStream("/assets/ca.crt");
				InputStream client = getClass().getResourceAsStream("/assets/client.crt");
				InputStream key = getClass().getResourceAsStream("/assets/client.key");
				
				String castr = new String(SDcardUtil.saveCreateCertificate(ca, "ca.crt"));
				String clstr = new String(SDcardUtil.saveCreateCertificate(client, "client.crt"));
				String keystr = new String(SDcardUtil.saveCreateCertificate(key, "client.key"));
//				JniUtil.transSetCrt(castr, clstr, keystr);
				JniUtil.transSetCrtPaht(castr, clstr, keystr);	
					
			
				
				
				String id = Utils.getPhoneUid(context);
				
				JniUtil.transConnect(type, id, TEST_ACCOUNT, TEST_PASSWORD);
				
				return null;
			};
			
		}.execute();
	}
	
    private byte[] InputStreamToByte(InputStream is) throws IOException {
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
        int ch;
        while ((ch = is.read()) != -1) {
            bytestream.write(ch);
        }
        byte imgdata[] = bytestream.toByteArray();
        bytestream.close();
        return imgdata;
    }
	
	
	
	public void logoutCam(){
		JniUtil.loginOut();
	}
	
	
	
	
	
	
	
	public void playViewCam(){
		
		
		if(JniUtil.readyPlayLive()){
			Log.i("123", "play view cam");
			Subscribe s = new Subscribe(sessionID, (int)getDialogId(), PlatformAction.getInstance().getDeviceId(), "live");
			String jsonStr = JsonUtil.subScribeJson(s);
			Log.i("123", "jsonStr="+jsonStr);
			JniUtil.transSubscribe(jsonStr, jsonStr.length());
			
			JniUtil.playView();
		}else{
			Log.e("123", "ready play live error");
		}
		
		
		
		
		
//		new AsyncTask<Void, Void, Void>(){
//			@Override
//			protected Void doInBackground(Void... params) {
//				
//				Log.i("123", "play view cam");
//				Subscribe s = new Subscribe(sessionID, (int)getDialogId(), PlatformAction.getInstance().getDeviceId(), "live");
//				String jsonStr = JsonUtil.subScribeJson(s);
//				Log.i("123", "jsonStr="+jsonStr);
//				JniUtil.transSubscribe(jsonStr, jsonStr.length());
//				
//				
//				
////				if(JniUtil.readyPlayLive()){
////					Log.i("123", "ready play live ok");
////					JniUtil.playView();
////				}else{
////					Log.i("123", "ready play error");
////				}
//				
//				
//				return null;
//			}
//			
//		}.execute();
		
		
		
	}
	
	public void stopViewCam(){
		JniUtil.stopView();
		JniUtil.transUnsubscribe();
	}
	
	
	
	public void transInit(String ip,int port){
		JniUtil.transInit(ip, port);
	}
	
	public void transConnect(int type,String id,String name,String pwd){
		JniUtil.transConnect(type, id, name, pwd);
	}
	
	public void stransSubscribe(String jsonStr,int jsonLen){
		JniUtil.transSubscribe(jsonStr, jsonLen);
	}
	
}
