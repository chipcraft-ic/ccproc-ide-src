/*H*****************************************************************************
*
* Copyright (c) 2018 ChipCraft Sp. z o.o. All rights reserved
*
* This software is subject to the terms of the Eclipse Public License v2.0
* You must accept the terms of that agreement to use this software.
* A copy of the License is available at
* https://www.eclipse.org/legal/epl-2.0
*
* ******************************************************************************
* File Name : FileLocationArea.java
* Author    : Rafal Harabien
* ******************************************************************************
* $Date: 2019-03-19 12:27:38 +0100 (Tue, 19 Mar 2019) $
* $Revision: 397 $
*H*****************************************************************************/

package com.chipcraftic.eplugin.ui.options;

import java.io.IOException;

import org.eclipse.cdt.serial.SerialPort;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.environment.Constants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import com.chipcraftic.eplugin.core.CcPlugin;

/**
 * Port select area consisting of label and text field.
 */
public class SerialPortArea implements InputArea<String> {

	private final int SIZING_TEXT_FIELD_WIDTH = 100;
	
	private Combo comboField;
	
	public SerialPortArea(String labelText, Composite composite, Listener modifyListener) {
		createContents(labelText, composite);
		comboField.addListener(SWT.Modify, modifyListener);
	}

	private void createContents(String labelText, Composite composite) {
		Composite group = new Composite(composite, SWT.NONE);
        GridLayout layout = new GridLayout();
        boolean isWin32 = Platform.getOS().equals(Constants.OS_WIN32);
        
        layout.numColumns = isWin32 ? 2 : 3;
        group.setLayout(layout);
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // Create label
        Label label = new Label(group, SWT.NONE);
        label.setText(labelText);
        label.setFont(composite.getFont());

        // Create combo-box for port selection
        comboField = new Combo(group, SWT.BORDER);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.widthHint = SIZING_TEXT_FIELD_WIDTH;
        comboField.setLayoutData(data);
        
        // Add available serial ports to combo-box list
        try {
			String[] serialPorts = SerialPort.list();
			for (String port: serialPorts) {
	        	comboField.add(port);
	        }
		} catch (IOException e) {
			CcPlugin.log(e);
		}
        
        // On non-windows platforms add 'Browse' button
        if (!isWin32) {
        	Button browseButton = new Button(group, SWT.PUSH);
    		browseButton.setText("Browse...");
    		browseButton.addSelectionListener(new SelectionAdapter() {
    			@Override
    			public void widgetSelected(SelectionEvent event) {
    				handleBrowseButtonPressed();
    			}
    		});
        }
	}
	
	@Override
	public void setValue(String value) {
		comboField.setText(value);
	}

	@Override
	public String getValue() {
		return comboField.getText().trim();
	}
	
	private void handleBrowseButtonPressed() {
		FileDialog dialog = new FileDialog(comboField.getShell(), SWT.OPEN);
		dialog.setFilterPath("/dev");

		String locationPath = dialog.open();
		if (locationPath != null) {
			comboField.setText(locationPath);
		}
	}
}
