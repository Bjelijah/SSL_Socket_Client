package bean;

import javolution.io.Struct;




public class HWTransmissionHead extends Struct {
	Unsigned8 sync = new Unsigned8(0xa5);
	Unsigned8 version = new Unsigned8();
	Unsigned8 flag = new Unsigned8();
	Unsigned8 reserved = new Unsigned8(0);
	Unsigned16 command = new Unsigned16();
	Unsigned16 seq = new Unsigned16();
	Unsigned32 payload_len = new Unsigned32();
	Unsigned32 reserved2 = new Unsigned32(0);
	public Unsigned8 getSync() {
		return sync;
	}
	public void setSync(Unsigned8 sync) {
		this.sync = sync;
	}
	public Unsigned8 getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = new Unsigned8(version);
	}
	public Unsigned8 getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = new Unsigned8(flag);
	}
	public Unsigned8 getReserved() {
		return reserved;
	}
	public void setReserved(Unsigned8 reserved) {
		this.reserved = reserved;
	}
	public Unsigned16 getCommand() {
		return command;
	}
	public void setCommand(int command) {
		this.command = new Unsigned16(command);
	}
	public Unsigned16 getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = new Unsigned16(seq);
	}
	public Unsigned32 getPayload_len() {
		return payload_len;
	}
	public void setPayload_len(int payload_len) {
		this.payload_len = new Unsigned32(payload_len);
	}
	public Unsigned32 getReserved2() {
		return reserved2;
	}
	public void setReserved2(Unsigned32 reserved2) {
		this.reserved2 = reserved2;
	}

}
