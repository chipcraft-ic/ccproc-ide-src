package com.chipcraftic.eplugin.ui.wizard;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import com.chipcraftic.eplugin.ui.wizard.model.SpiConfig;
import com.chipcraftic.eplugin.ui.wizard.model.UartConfig;

public class UartConfigComposite extends AbstractPeriphComposite<UartConfig> {

	private static final String[] PARITY_OPTIONS = new String[] {"NONE", "EVEN", "ODD", "FORCED0", "FORCED1"};
	
	private Spinner spinnerBaudrate;
	private Button btnTransmitter;
	private CCombo comboStopBits;
	private Button btnReceiver;
	private Button btnRts;
	private Button btnCts;
	private Label lblParity;
	private CCombo comboParity;
	private Button btnBigEndian;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public UartConfigComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));
		
		Label lblBaudrate = new Label(this, SWT.NONE);
		lblBaudrate.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblBaudrate.setText("Baudrate:");
		
		spinnerBaudrate = new Spinner(this, SWT.BORDER);
		spinnerBaudrate.setMaximum(1000000);
		spinnerBaudrate.setSelection(9600);
		new Label(this, SWT.NONE);
		
		btnTransmitter = new Button(this, SWT.CHECK);
		btnTransmitter.setText("Transmitter");
		new Label(this, SWT.NONE);
		
		btnReceiver = new Button(this, SWT.CHECK);
		btnReceiver.setText("Receiver");
		new Label(this, SWT.NONE);
		
		btnRts = new Button(this, SWT.CHECK);
		btnRts.setText("RTS");
		new Label(this, SWT.NONE);
		
		btnCts = new Button(this, SWT.CHECK);
		btnCts.setText("CTS");
		new Label(this, SWT.NONE);
		
		btnBigEndian = new Button(this, SWT.CHECK);
		btnBigEndian.setText("Big Endian");
		
		Label lblStopBits = new Label(this, SWT.NONE);
		lblStopBits.setText("Stop Bits:");
		
		comboStopBits = new CCombo(this, SWT.BORDER);
		comboStopBits.setEditable(false);
		comboStopBits.setItems(new String[] {"1 Stop Bit", "2 Stop Bits"});
		comboStopBits.select(0);
		
		lblParity = new Label(this, SWT.NONE);
		lblParity.setText("Parity:");
		
		comboParity = new CCombo(this, SWT.BORDER);
		comboParity.setItems(PARITY_OPTIONS);
		comboParity.setEditable(false);
		comboParity.select(0);
	}

	@Override
	public void updateModel() {
		model.setBaudrate(spinnerBaudrate.getSelection());
		model.setTransmitter(btnTransmitter.getSelection());
		model.setReceiver(btnReceiver.getSelection());
		model.setCts(btnCts.getSelection());
		model.setRts(btnRts.getSelection());
		model.setBigEndian(btnBigEndian.getSelection());
		model.setStopBits(comboStopBits.getSelectionIndex() + 1);
		model.setParity(comboParity.getText());
	}
	
	@Override
	protected void updateView() {
		spinnerBaudrate.setSelection((int) model.getBaudrate());
		btnTransmitter.setSelection(model.isTransmitter());
		btnReceiver.setSelection(model.isReceiver());
		btnCts.setSelection(model.isCts());
		btnRts.setSelection(model.isRts());
		btnBigEndian.setSelection(model.isBigEndian());
		comboStopBits.select(model.getStopBits() - 1);
		comboParity.select(Arrays.asList(PARITY_OPTIONS).indexOf(model.getParity()));
	}
}
