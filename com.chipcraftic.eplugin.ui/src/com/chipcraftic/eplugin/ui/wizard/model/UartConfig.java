package com.chipcraftic.eplugin.ui.wizard.model;

public class UartConfig {
	
	private int index;
	private long baudrate = 9600;
	private boolean transmitter = true;
	private boolean receiver = true;
	private boolean rts = false;
	private boolean cts = false;
	private boolean bigEndian = false;
	private int stopBits = 1;
	private String parity = "NONE";

	public UartConfig(int index) {
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
	
	public long getBaudrate() {
		return baudrate;
	}
	
	public void setBaudrate(long baudrate) {
		this.baudrate = baudrate;
	}

	public boolean isTransmitter() {
		return transmitter;
	}

	public void setTransmitter(boolean transmitter) {
		this.transmitter = transmitter;
	}

	public boolean isReceiver() {
		return receiver;
	}

	public void setReceiver(boolean receiver) {
		this.receiver = receiver;
	}

	public boolean isRts() {
		return rts;
	}

	public void setRts(boolean rts) {
		this.rts = rts;
	}

	public boolean isCts() {
		return cts;
	}

	public void setCts(boolean cts) {
		this.cts = cts;
	}

	public boolean isBigEndian() {
		return bigEndian;
	}

	public void setBigEndian(boolean bigEndian) {
		this.bigEndian = bigEndian;
	}

	public int getStopBits() {
		return stopBits;
	}

	public void setStopBits(int stopBits) {
		this.stopBits = stopBits;
	}

	public String getParity() {
		return parity;
	}

	public void setParity(String parity) {
		this.parity = parity;
	}
}
