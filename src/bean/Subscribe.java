package bean;

public class Subscribe {
	String sessionId;
	int dialogId;
	String deviceId;
	String mode;
	
	public Subscribe(){}
	
	public Subscribe(String sessionId,int dialogId,String deviceId,String mode){
		this.sessionId = sessionId;
		this.dialogId = dialogId;
		this.deviceId = deviceId;
		this.mode = mode;
	}
	
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public int getDialogId() {
		return dialogId;
	}
	public void setDialogId(int dialogId) {
		this.dialogId = dialogId;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	
}
