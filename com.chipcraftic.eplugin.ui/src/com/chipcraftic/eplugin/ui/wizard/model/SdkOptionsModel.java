package com.chipcraftic.eplugin.ui.wizard.model;

public class SdkOptionsModel {
	private String sdkLocation;
	private String boardName;
	private String uartPort;
	private String debugPort;
	
	public String getSdkLocation() {
		return sdkLocation;
	}
	
	public void setSdkLocation(String sdkLocation) {
		this.sdkLocation = sdkLocation;
	}
	
	public String getBoardName() {
		return boardName;
	}
	
	public void setBoardName(String boardName) {
		this.boardName = boardName;
	}
	
	public String getUartPort() {
		return uartPort;
	}
	
	public void setUartPort(String uartPort) {
		this.uartPort = uartPort;
	}
	
	public String getDebugPort() {
		return debugPort;
	}
	
	public void setDebugPort(String debugPort) {
		this.debugPort = debugPort;
	}
}
