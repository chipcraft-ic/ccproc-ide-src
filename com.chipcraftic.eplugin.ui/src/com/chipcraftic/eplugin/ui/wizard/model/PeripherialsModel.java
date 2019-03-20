package com.chipcraftic.eplugin.ui.wizard.model;

import java.util.ArrayList;
import java.util.List;

public class PeripherialsModel {
	
	private List<SpiConfig> spiList = new ArrayList<>();
	private List<UartConfig> uartList = new ArrayList<>();

	public List<SpiConfig> getSpiList() {
		return spiList;
	}

	public void setSpiList(List<SpiConfig> spiList) {
		this.spiList = spiList;
	}

	public List<UartConfig> getUartList() {
		return uartList;
	}

	public void setUartList(List<UartConfig> uartList) {
		this.uartList = uartList;
	}
};
