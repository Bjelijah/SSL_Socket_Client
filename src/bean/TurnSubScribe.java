package bean;

public class TurnSubScribe {
	String SessionId;
	String topic;
	int dialogId;
	String deviceId;
	String mode;
	int channel;
	int stream;
	public String getSessionId() {
		return SessionId;
	}
	public void setSessionId(String sessionId) {
		SessionId = sessionId;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
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
	public int getChannel() {
		return channel;
	}
	public void setChannel(int channel) {
		this.channel = channel;
	}
	public int getStream() {
		return stream;
	}
	public void setStream(int stream) {
		this.stream = stream;
	}
	public TurnSubScribe(String sessionId, String topic, int dialogId, String deviceId, String mode, int channel,
			int stream) {
		super();
		SessionId = sessionId;
		this.topic = topic;
		this.dialogId = dialogId;
		this.deviceId = deviceId;
		this.mode = mode;
		this.channel = channel;
		this.stream = stream;
	}
	
}
