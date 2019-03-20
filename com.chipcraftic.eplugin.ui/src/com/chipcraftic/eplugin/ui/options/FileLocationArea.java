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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * File location area consisting of label, text field and button opening File Dialog.
 * 
 * See org.eclipse.ui.internal.ide.dialogs.ProjectContentsLocationArea
 */
public class FileLocationArea implements InputArea<String> {

	private final int SIZING_TEXT_FIELD_WIDTH = 100;
	
	private Text locationField;
	private String baseDir;
	
	public FileLocationArea(String labelText, String baseDir, Composite composite, Listener modifyListener) {
		this.baseDir = baseDir;
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
		FileDialog dialog = new FileDialog(locationField.getShell(), SWT.OPEN);
		dialog.setFilterPath(baseDir);

		String locationPath = dialog.open();
		
		if (locationPath != null) {
			locationField.setText(locationPath);
		}
	}
	
	@Override
	public void setValue(String location) {
		locationField.setText(location);
	}
	
	@Override
	public String getValue() {
		return locationField.getText().trim();
	}
}
