package com.howell.utils;

import org.json.JSONException;
import org.json.JSONObject;

import bean.TurnConnectBean;
import bean.TurnSubScribe;

public class TurnJsonUtil {
	public static String getTurnConnectJsonStr(TurnConnectBean bean){
		JSONObject object = null;
		
		object = new JSONObject();
		
		try {
			object.put("type", bean.getType());
			object.put("device_id", bean.getDeviceId());
			object.put("username", bean.getUserName());
			object.put("password", bean.getPassWord());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object.toString();
	}
	
	public static String getTurnSubScribe(TurnSubScribe bean){
		JSONObject object = null;
		object = new JSONObject();
		try {
			object.put("session_id", bean.getSessionId());
			object.put("topic", bean.getTopic());
			JSONObject media = new JSONObject();
			media.put("dialog_id", bean.getDialogId());
			JSONObject meta = new JSONObject();
			meta.put("device_id", bean.getDeviceId());
			meta.put("mode", bean.getMode());
			meta.put("channel", bean.getChannel());
			meta.put("stream", bean.getStream());
			media.put("meta", meta);
			object.put("media", media);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object.toString();
	}
	
}
