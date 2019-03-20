package com.chipcraftic.eplugin.core.board;

import java.util.Properties;

public class BoardInfo {
	private String mcu;
	private String uartBaudrate;
	private String dbgBaudrate;
	
	public BoardInfo(Properties props) {
		mcu = props.getProperty("MCU");
		uartBaudrate = props.getProperty("UART_BAUDRATE");
		dbgBaudrate = props.getProperty("DBG_BAUDRATE");
	}
	
	public String getMcu() {
		return mcu;
	}
	public void setMcu(String mcu) {
		this.mcu = mcu;
	}
	public String getUartBaudrate() {
		return uartBaudrate;
	}
	public void setUartBaudrate(String uartBaudrate) {
		this.uartBaudrate = uartBaudrate;
	}
	public String getDbgBaudrate() {
		return dbgBaudrate;
	}
	public void setDbgBaudrate(String dbgBaudrate) {
		this.dbgBaudrate = dbgBaudrate;
	}
}
