package com.chipcraftic.eplugin.ui.wizard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import com.chipcraftic.eplugin.ui.wizard.model.SpiConfig;

public class SpiConfigComposite extends AbstractPeriphComposite<SpiConfig> {

	private static String[] TRANSMISSION_MODES = new String[] {
		"MODE0 - sampling at first edge, first edge is rising",
		"MODE1 - sampling at second edge, first edge is rising",
		"MODE2 - sampling at first edge, first edge is falling",
		"MODE3 - sampling at second edge, first edge is falling",
	};
	
	private static String[] FRAME_LEN_ITEMS = new String[] {
		"FLEN8 - 8 Bits",
		"FLEN16 - 16 Bits",
		"FLEN24 - 24 Bits",
		"FLEN32 - 32 Bits",
	};
	
	private Spinner baudrateSpinner;
	private Button masterCheckButton;
	private CCombo transModeCombo;
	private CCombo frameLenCombo;
	private Button msbFirstCheckButton;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public SpiConfigComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));
		
		Label lblNewLabel = new Label(this, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("Baudrate:");
		
		baudrateSpinner = new Spinner(this, SWT.BORDER);
		baudrateSpinner.setMaximum(Integer.MAX_VALUE);
		
		new Label(this, SWT.NONE);
		masterCheckButton = new Button(this, SWT.CHECK);
		masterCheckButton.setText("Master");
		
		Label transModeLabel = new Label(this, SWT.NONE);
		transModeLabel.setText("Transmission Mode:");
		
		transModeCombo = new CCombo(this, SWT.BORDER);
		transModeCombo.setItems(TRANSMISSION_MODES);
		transModeCombo.setEditable(false);
		transModeCombo.select(0);
		
		Label frameLenLabel = new Label(this, SWT.NONE);
		frameLenLabel.setText("Frame Length:");
		
		frameLenCombo = new CCombo(this, SWT.BORDER);
		frameLenCombo.setItems(FRAME_LEN_ITEMS);
		frameLenCombo.setEditable(false);
		frameLenCombo.select(0);
		
		new Label(this, SWT.NONE);
		msbFirstCheckButton = new Button(this, SWT.CHECK);
		msbFirstCheckButton.setText("MSB First");
	}

	@Override
	protected void updateView() {
		baudrateSpinner.setSelection((int) model.getBaudrate());
		masterCheckButton.setSelection(model.isMaster());
		transModeCombo.select(findIdInIdDescList(model.getTransmissionMode(), TRANSMISSION_MODES));
		frameLenCombo.select(findIdInIdDescList(model.getFrameLength(), FRAME_LEN_ITEMS));
		msbFirstCheckButton.setSelection(model.isMsbFirst());
	}
	
	@Override
	public void updateModel() {
		model.setBaudrate(baudrateSpinner.getSelection());
		model.setMaster(masterCheckButton.getSelection());
		model.setTransmissionMode(transModeCombo.getText().replaceAll(" .*", ""));
		model.setFrameLength(frameLenCombo.getText().replaceAll(" .*", ""));
		model.setMsbFirst(msbFirstCheckButton.getSelection());
	}
}
