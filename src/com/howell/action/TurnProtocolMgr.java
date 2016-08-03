package com.howell.action;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.json.JSONException;

import com.howell.utils.IConst;
import com.howell.utils.JsonUtil;
import com.howell.utils.SSlSocketUtil;
import com.howell.utils.TurnJsonUtil;
import com.howell.utils.Utils;

import android.R.anim;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import bean.HWTransmissionHead;
import bean.HWTransmissonHeadEx;
import bean.TurnConnectAckBean;
import bean.TurnConnectBean;
import bean.TurnSubScribe;
import struct.JavaStruct;
import struct.StructException;

public class TurnProtocolMgr implements IConst{
	private static TurnProtocolMgr mInstance = null;
	public static TurnProtocolMgr getInstance(){
		if(mInstance == null){
			mInstance = new TurnProtocolMgr();
		}
		return mInstance;
	}
	private TurnProtocolMgr(){}
	int m_seq = 0;
	int headLen = 0;
	Context context;
	Handler handler;
	String sessionID;
	int dialogId = 0;
	public int getNewDialogId(){
		return dialogId++;
	}
	public TurnProtocolMgr setHander(Handler handler){
		this.handler = handler;
		return this;
	}
	public TurnProtocolMgr setContext(Context context){
		this.context = context;
		return this;
	}
	ByteBuffer readbuf = ByteBuffer.allocate(2*1024*1024);
	public void connect2TurnService(){
		readbuf.clear();
		int type = 101;
		String deviceId =Utils.getPhoneUid(context);
		
		TurnConnectBean bean = new TurnConnectBean(type, deviceId, TEST_ACCOUNT, TEST_PASSWORD);
		String jsonStr = TurnJsonUtil.getTurnConnectJsonStr(bean);
		Log.i("123", "json str="+jsonStr+"   size="+jsonStr.length());
		
		byte flag = 0;
		short kmd = (short) KMD.kCmdConnect.getVal();
		send(kmd,flag,jsonStr,jsonStr.length());
	}
	
	public void subScribeCamStream(){
		readbuf.clear();
		
		String deviceId = PlatformAction.getInstance().getDeviceId();
		TurnSubScribe bean = new TurnSubScribe(sessionID, "media", getNewDialogId(), deviceId, "live", 0, 0);
		String jsonStr = TurnJsonUtil.getTurnSubScribe(bean);
		byte flag = 0;
		short kmd = (short) KMD.kCmdSubscribe.getVal();
		send(kmd, flag, jsonStr, jsonStr.length());
	}
	
	
	
	private byte [] buildHead(HWTransmissonHeadEx head,short cmd,byte flag,int len) throws StructException{
		if(head == null) return null;
		Log.i("123", "sync:"+String.format("0x%x", head.getSync()));
		Log.i("123", "cmd="+cmd);
		head.setVersion((byte)1);
		head.setFlag(flag);
		try {
			head.setCommand(cmd);
			head.setSeq((short)m_seq++);
			head.setPayload_len(len);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return struct.JavaStruct.pack(head);
	}
	
	
	private void send(short cmd,byte flag,String json,int jsonLen){
//		HWTransmissionHeadEx head = new HWTransmissionHeadEx();
		HWTransmissonHeadEx head = new HWTransmissonHeadEx();
		byte[] headBuf = null;
		try {
			headBuf = buildHead(head, cmd, flag, jsonLen);
		} catch (StructException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		headLen = headBuf.length;
		sendMsg(headBuf, json.getBytes());
	}
	
	private synchronized void sendMsg(byte[] head,byte [] body){
		if(head==null || body == null){
			Log.e("123", "null");
			return;
		}
		String string = new String(body);
		Log.i("123", "string="+string);
	
		
		Log.i("123", "head.len="+head.length+" body.len="+body.length);
		byte [] buf = new byte[head.length+body.length];
		System.arraycopy(head, 0, buf, 0,head.length);
		System.arraycopy(body, 0, buf, head.length, body.length);
		SSlSocketUtil.getInstance().write(buf);
//		buf = null;
	}

	public synchronized boolean processMsg(byte [] buf){
		if(buf==null){
			Log.e("123", "buf = null");
			return false;
		}
		
		readbuf.put(buf);
		Log.i("123", "pos="+readbuf.position()+"  limit="+readbuf.limit());
		if(readbuf.position()<headLen){
			Log.e("123","pos < headlen");
			return false;
		}
		
		byte [] readBufArray = readbuf.array();
		Log.i("123", "readbufArray len="+readBufArray.length);
		int dataLen = 0;
		byte [] head = new byte[headLen];
		System.arraycopy(readBufArray, 0, head, 0, headLen);
		HWTransmissonHeadEx headObj = new HWTransmissonHeadEx();
		try {
			JavaStruct.unpack(headObj, head);
			dataLen = headObj.getPayload_len();
		} catch (StructException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		short cmd = 0;
		try {
			cmd =  headObj.getCommand();
			Log.i("123", "sync="+String.format("0x%x", headObj.getSync())+"  dataLen="+dataLen+" command="+String.format("0x%x", headObj.getCommand()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		if(headObj.getSync()!=  (byte) (0xa5) ){
			Log.e("123", "sync!=0xa5");
			readbuf.clear();
			return false;
		}
		
		
		byte [] body = new byte[dataLen];
		System.arraycopy(readBufArray, headLen, body, 0, dataLen);
		String jsonStr = new String(body);
		Log.i("123", "jsonStr="+jsonStr);
		phaseMsg(cmd, jsonStr);
		readbuf.clear();
		return true;
	}
	
	
	private void phaseMsg(final short cmd,final String jsonStr){
		new Thread(){
			public void run() {
				switch (cmd) {
				case (short) 0x11:
				
					TurnConnectAckBean bar = JsonUtil.getTurnConnectAckFromJsonStr(jsonStr);
					if(bar!=null){
						doConnectAck(bar);
					}
					break;

				default:
					break;
				}
				
				
			};
			
		}.start();
	}
	
	private void doConnectAck(TurnConnectAckBean bean){
		Log.i("123", "code ="+bean.getCode()+" sessionid="+bean.getSessionId());
		if(bean.getCode()!=200){
			//fail
			handler.sendEmptyMessage(MSG_TURN_CONNECT_FAIL);
		}else{
			//success
			sessionID = bean.getSessionId();
			handler.sendEmptyMessage(MSG_TURN_CONNECT_OK);
		}
	}
	
	
	
	public enum KMD{
		kCmdConnect(0x10),
		kCmdConnectAck(0x11),
		kCmdDisconnect(0x12),
		kCmdDisconnectAck(0x13),
		kCmdSubscribe(0x14),
		kCmdSubscribeAck(0x15),
		kCmdUnsubscribe(0x16),
		kCmdUnsubscribeAck(0x17),
		kCmdPushReq(0x18),
		kCmdPushReqAck(0x19),
		kCmdPush(0x20),
		kCmdGetCamrea(0x101),
		kCmdGetCamreaAck(0x102),
		kCmdGetRecordedFiles(0x103),
		kCmdGetRecordedFilesAck(0x104),
		kCmdPtzCtrl(0x105),
		kCmdPtzCtrlAck(0x106)
		;
		private final int val;		
		private KMD(int val) {
			this.val = val;
		}		
		public int getVal(){
			return val;
		}		
	}
	
	
}
