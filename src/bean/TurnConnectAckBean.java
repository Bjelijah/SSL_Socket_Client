package bean;

public class TurnConnectAckBean {
	int code;
	String sessionId;
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public TurnConnectAckBean(int code, String sessionId) {
		super();
		this.code = code;
		this.sessionId = sessionId;
	}
	
}
