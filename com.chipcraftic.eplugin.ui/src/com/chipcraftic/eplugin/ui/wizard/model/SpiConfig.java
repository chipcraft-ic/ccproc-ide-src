package com.chipcraftic.eplugin.ui.wizard.model;

public class SpiConfig {
	
	private int index;
	private boolean master = false;
	private long baudrate = 200000;
	private String transmissionMode = "MODE0";
	private String frameLength = "FLEN8";
	private boolean msbFirst = false;
	
	public SpiConfig(int index) {
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
	
	public boolean isMaster() {
		return master;
	}
	
	public void setMaster(boolean master) {
		this.master = master;
	}
	
	public long getBaudrate() {
		return baudrate;
	}
	
	public void setBaudrate(long baudrate) {
		this.baudrate = baudrate;
	}
	
	public String getTransmissionMode() {
		return transmissionMode;
	}
	
	public void setTransmissionMode(String transmissionMode) {
		this.transmissionMode = transmissionMode;
	}
	
	public String getFrameLength() {
		return frameLength;
	}
	
	public void setFrameLength(String frameLength) {
		this.frameLength = frameLength;
	}

	public boolean isMsbFirst() {
		return msbFirst;
	}

	public void setMsbFirst(boolean msbFirst) {
		this.msbFirst = msbFirst;
	}
}
