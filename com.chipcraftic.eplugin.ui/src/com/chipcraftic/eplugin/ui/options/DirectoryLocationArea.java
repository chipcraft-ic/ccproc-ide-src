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
* File Name : DirectoryLocationArea.java
* Author    : Rafal Harabien
* ******************************************************************************
* $Date: 2019-03-19 12:27:38 +0100 (Tue, 19 Mar 2019) $
* $Revision: 397 $
*H*****************************************************************************/

package com.chipcraftic.eplugin.ui.options;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * 
 * @see ProjectContentsLocationArea
 */
public class DirectoryLocationArea {

	private final int SIZING_TEXT_FIELD_WIDTH = 100;
	
	private Text locationField;
	
	public DirectoryLocationArea(String labelText, Composite composite, Listener modifyListener) {
		createContents(labelText, composite);
		locationField.addListener(SWT.Modify, modifyListener);
	}

	private void createContents(String labelText, Composite composite) {
		Composite locationGroup = new Composite(composite, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        locationGroup.setLayout(layout);
        locationGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // new project label
        Label label = new Label(locationGroup, SWT.NONE);
        label.setText(labelText);
        label.setFont(composite.getFont());

        // new project name entry field
        locationField = new Text(locationGroup, SWT.BORDER);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.widthHint = SIZING_TEXT_FIELD_WIDTH;
        locationField.setLayoutData(data);
        
        Button browseButton = new Button(locationGroup, SWT.PUSH);
		browseButton.setText("Browse...");
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				handleBrowseButtonPressed();
			}
		});
	}
	
	private void handleBrowseButtonPressed() {
		String dirName = getPathFromLocationField();
		DirectoryDialog dialog = new DirectoryDialog(locationField.getShell(), SWT.SHEET);
		dialog.setMessage("Select CCSDK location");
		dialog.setFilterPath(dirName);

		String selectedDirectory = dialog.open();
		
		if (selectedDirectory != null) {
			locationField.setText(selectedDirectory);
		}
	}
	
	private String getPathFromLocationField() {
		URI fieldURI;
		try {
			fieldURI = new URI(locationField.getText());
		} catch (URISyntaxException e) {
			return locationField.getText();
		}
		String path= fieldURI.getPath();
		return path != null ? path : locationField.getText();
	}
	
	public void setLocation(String location) {
		locationField.setText(location);
	}
	
	public String getLocation() {
		return locationField.getText().trim();
	}
}
