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
* File Name : CcsdkProjectOptionsPage.java
* Author    : Rafal Harabien
* ******************************************************************************
* $Date: 2019-03-19 12:27:38 +0100 (Tue, 19 Mar 2019) $
* $Revision: 397 $
*H*****************************************************************************/

package com.chipcraftic.eplugin.ui.options;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.osgi.service.environment.Constants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import com.chipcraftic.eplugin.core.board.BoardInfoResolver;
import com.chipcraftic.eplugin.ui.wizard.model.SdkOptionsModel;

/**
 * Second page of New CCSDK Project wizard.
 * 
 * @author Rafał Harabień
 *
 */
public class CcsdkProjectOptionsComposite {
	
	private static final String BOARDS_DIR = "boards";
	private static final String INCLUDE_DIR = "include";
	private static final String DEFAULT_BOARD = "sim";
	
	private final int SIZING_TEXT_FIELD_WIDTH = 100;
	
	private DirectoryLocationArea sdkLocationArea;
	private Combo boardNameField;
	private InputArea<String> debugPortArea;
	private InputArea<String> uartPortArea;
	private Listener modifyListener;

	public CcsdkProjectOptionsComposite(Listener modifyListener) {
		this.modifyListener = modifyListener;
	}
	
	public Composite createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout());

        sdkLocationArea = new DirectoryLocationArea("CCSDK location:", composite, e -> {
		    populateBoardsList();
		    modifyListener.handleEvent(e);
        });
        createBoardNameArea(composite);
        
        debugPortArea = new SerialPortArea("Debug Port:", composite, modifyListener);
        uartPortArea = new SerialPortArea("UART Port:", composite, modifyListener);

        populateBoardsList();
        
        Dialog.applyDialogFont(composite);
        return composite;
	}
	
	private void createBoardNameArea(Composite composite) {
		Composite group = new Composite(composite, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        group.setLayout(layout);
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // new project label
        Label label = new Label(group, SWT.NONE);
        label.setText("Board:");
        label.setFont(composite.getFont());

        // new project name entry field
        boardNameField = new Combo(group, SWT.BORDER);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.widthHint = SIZING_TEXT_FIELD_WIDTH;
        boardNameField.setLayoutData(data);
        boardNameField.addListener(SWT.Modify, modifyListener);
	}
	
	private void populateBoardsList() {
		// Remove all items without clearing selection
		if (boardNameField.getItemCount() > 0) {
			boardNameField.remove(0, boardNameField.getItemCount() - 1);
		}
		// Find boards sub-directory in SDK
		String sdkLocation = getSdkLocation();
		List<String> boards = BoardInfoResolver.listBoards(sdkLocation);
		if (!boards.isEmpty()) {
			for (String board: boards) {
				boardNameField.add(board);
			}
		} else {
			// fall-back in case of invalid SDK location
			boardNameField.add(DEFAULT_BOARD);
		}
	}
	
	public String getSdkLocation() {
		return sdkLocationArea.getLocation();
	}
	
	public String getBoardName() {
		return boardNameField.getText().trim().toLowerCase();
	}
	
	public String getDebugPort() {
		return debugPortArea.getValue();
	}
	
	public String getUartPort() {
		return uartPortArea.getValue();
	}
	
	public void setSdkLocation(String value) {
		sdkLocationArea.setLocation(value);
	}
	
	public void setBoardName(String value) {
		boardNameField.setText(value);
	}
	
	public void setDebugPort(String value) {
		debugPortArea.setValue(value);
	}
	
	public void setUartPort(String value) {
		uartPortArea.setValue(value);
	}
	
	public SdkOptionsModel buildModel() {
		SdkOptionsModel model = new SdkOptionsModel();
		model.setSdkLocation(getSdkLocation());
		model.setBoardName(getBoardName());
		model.setDebugPort(getDebugPort());
		model.setUartPort(getUartPort());
		return model;
	}
	
	public boolean validatePage(DialogPage page) {
		String sdkLocation = getSdkLocation();
		if (sdkLocation.isEmpty()) {
			page.setErrorMessage("SDK location must be specified");
			return false;
		}
		
		if (!isValidSdkLocation(sdkLocation)) {
			page.setErrorMessage("SDK location is invalid");
			return false;
		}
		
		String boardName = getBoardName();
		if (boardName.isEmpty()) {
			page.setErrorMessage("Board name must be specified");
			return false;
		}
		if (!BoardInfoResolver.isValidBoard(boardName, sdkLocation)) {
			page.setErrorMessage("Invalid board");
			return false;
		}
		
		String debugPort = getDebugPort();
		if (!debugPort.isEmpty()) {
			if (!isValidPort(debugPort)) {
				page.setErrorMessage("Debug port is not a valid device");
				return false;
			}
		}
		
		String uartPort = getUartPort();
		if (!uartPort.isEmpty()) {
			if (!isValidPort(uartPort)) {
				page.setErrorMessage("UART port is not a valid device");
				return false;
			}
		}
		
		page.setErrorMessage(null);
		return true;
	}
	
	private boolean isValidPort(String port) {
		if (Platform.getOS().equals(Constants.OS_WIN32)) {
			// Check prefix
			return port.startsWith("COM");
		} else {
			// On UNIX device is normal file
			File portFile = new File(port);
			return portFile.exists() && !portFile.isDirectory();
		}
	}
	
	public boolean isValidSdkLocation(String sdkLocation) {
		return new File(sdkLocation + File.separator + BOARDS_DIR).isDirectory() &&
				new File(sdkLocation + File.separator + INCLUDE_DIR).isDirectory();
	}
}
