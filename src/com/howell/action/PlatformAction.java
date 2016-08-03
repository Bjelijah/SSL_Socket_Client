package com.howell.action;

import java.util.Date;
import java.util.List;

import com.howell.entityclass.Device;
import com.howell.jni.JniUtil;
import com.howell.protocol.GetNATServerReq;
import com.howell.protocol.GetNATServerRes;
import com.howell.protocol.LoginRequest;
import com.howell.protocol.LoginResponse;
import com.howell.protocol.SoapManager;
import com.howell.utils.DecodeUtils;
import com.howell.utils.IConst;
import com.howell.utils.JsonUtil;
import com.howell.utils.TimeTransform;

import android.content.Context;
import android.database.DatabaseUtils;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import bean.GetCamBean;
import bean.GetRecordedFilesBean;

public class PlatformAction implements IConst{
	
	
	private static PlatformAction mInstance = null;
	private PlatformAction() {	}
	public static PlatformAction getInstance(){
		if(mInstance == null){
			mInstance = new PlatformAction();
		}
		return mInstance;
	}
	SoapManager mSoapManager = SoapManager.getInstance();
	private Context context;
	private String turnServerIp = null;
	private int turnServerPort = -1;
	private String device_id = null;
	public String getDeviceId(){
		return device_id;
	}
	
	public String getTurnServerIP(){
		return this.turnServerIp;
	}
	
	public int getTurnServerPort(){
		return turnServerPort;
	}
	
	Handler handler;
	public void setHandler(Handler handler){
		this.handler = handler;
	}
	

	
	
	public void loginPlatform(Context context){
		this.context = context;
		mSoapManager.context = context;
		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				// TODO Auto-generated method stub
				
				String encodedPassword = DecodeUtils.getEncodedPassword(TEST_PASSWORD);
				LoginRequest loginReq = new LoginRequest(TEST_ACCOUNT, "Common",encodedPassword, "1.0.0.1");
				LoginResponse loginRes = mSoapManager.getUserLoginRes(loginReq);
				if(loginRes.getResult().equals("OK")){
					Log.i("123", "login ok");
					List<Device> list = loginRes.getNodeList();
					if(!list.isEmpty()){
						device_id = list.get(0).getDeviceID();
						
					}else{
						device_id = null;
					}
							
					GetNATServerRes res = mSoapManager.getGetNATServerRes(new GetNATServerReq(TEST_ACCOUNT, loginRes.getLoginSession()));
					Log.i("123", res.toString());
					if(res.getResult().equals("OK")){
						turnServerIp = res.getTURNServerAddress();
						turnServerPort = res.getTURNServerPort();
					}else{
						turnServerIp = null;
						turnServerPort = -1;
					}
					
					
					return true;
				}else{
					return false;
				}
				
				
			}
			
			protected void onPostExecute(Boolean result) {
				if(result){
				
//					handler.sendEmptyMessage(MSG_LOGIN_OK);
					handler.sendEmptyMessage(MSG_HTTPS_TEST);
				}else{
					
					handler.sendEmptyMessage(MSG_LOGIN_FAIL);
				}
			};
		}.execute();
		
	}
	
	
	public void getCam(){
		String [] s = new String[2];
		s[0] = TEST_ACCOUNT;
		s[1] = TEST_PASSWORD;
		new AsyncTask<String, Void, Void>(){

			@Override
			protected Void doInBackground(String... params) {
				// TODO Auto-generated method stub
				GetCamBean bean = new GetCamBean(params[0], params[1]);
				
				String strJson = JsonUtil.getCamJson(bean);
				
				
				return null;
			}
			
		}.execute(s);
	}
	
	public void getRecordFiles(String startTime,String endTime){
		String [] timeStr = new String[2];
		timeStr[0] = startTime;
		timeStr[1] = endTime;
		
		new AsyncTask<String, Void, Void>(){

			@Override
			protected Void doInBackground(String... params) {
				// TODO Auto-generated method stub
				GetRecordedFilesBean bean = new GetRecordedFilesBean(device_id, 0, params[0], params[1]);
				
				String strJson = JsonUtil.getRecordFilesJson(bean);
				
				JniUtil.transGetRecordFiles(strJson, strJson.length());
				return null;
			}
			
		}.execute(timeStr);
	}
	
	
	
	
}
