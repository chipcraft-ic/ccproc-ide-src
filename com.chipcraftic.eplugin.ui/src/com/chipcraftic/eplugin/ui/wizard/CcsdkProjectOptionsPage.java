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

package com.chipcraftic.eplugin.ui.wizard;

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.chipcraftic.eplugin.core.CcConstants;
import com.chipcraftic.eplugin.core.CcPlugin;
import com.chipcraftic.eplugin.ui.CcUIPlugin;
import com.chipcraftic.eplugin.ui.options.CcsdkProjectOptionsComposite;
import com.chipcraftic.eplugin.ui.wizard.model.SdkOptionsModel;

/**
 * Second page of New CCSDK Project wizard.
 * 
 * @author Rafał Harabień
 *
 */
public class CcsdkProjectOptionsPage extends WizardPage {
	
	private CcsdkProjectOptionsComposite projectOptionsComposite;
	private Button cppCheckBtn;
	private Button managedBuildCheckBtn;
	private Button makefileBuildCheckBtn;
	
	protected CcsdkProjectOptionsPage(String pageName) {
		super(pageName);
	    setPageComplete(false);
	    projectOptionsComposite = new CcsdkProjectOptionsComposite(e -> {
	    	setPageComplete(projectOptionsComposite.validatePage(this));
	    });
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout());
		
        createSdkOptionsControl(composite);
        createCppLangControl(composite);
		createBuildTypeControl(composite);
		
		setDefaults();
        initializeDialogUnits(parent);
        setPageComplete(projectOptionsComposite.validatePage(this));
        setControl(composite);
	}
	
	private void createSdkOptionsControl(Composite parent) {
		Group group = new Group(parent, SWT.NULL);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setText("SDK options");
		group.setLayout(new GridLayout());
		Composite composite = projectOptionsComposite.createControl(group);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}
	
	private void createCppLangControl(Composite parent) {
		Composite group = new Composite(parent, SWT.NONE);
        group.setLayout(new GridLayout());
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        cppCheckBtn = new Button(group, SWT.CHECK);
        cppCheckBtn.setText("C++ Support");
	}

	private void createBuildTypeControl(Composite parent) {
        Group group = new Group(parent, SWT.NULL);
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        group.setText("Build Type");
        group.setLayout(new GridLayout());

        managedBuildCheckBtn = new Button(group, SWT.RADIO);
        managedBuildCheckBtn.setText("Managed Build - Makefile is generated automatically");

        makefileBuildCheckBtn = new Button(group, SWT.RADIO);
        makefileBuildCheckBtn.setText("Makefile Build - Makefile is managed by user");
	}
	
	private String getDefaultValue(String preferenceKey, String envVarName) {
		String value = null;
		if (envVarName != null) {
			value = System.getenv(envVarName);
		}
		if (value == null || value.isEmpty()) {
			IPreferenceStore preferenceStore = CcUIPlugin.getDefault().getPreferenceStore();
			value = preferenceStore.getString(preferenceKey);
		}
		return value != null ? value : "";
	}
	
	private void setDefaults() {
		String sdkHome = getDefaultValue(CcConstants.WIZARD_LAST_SDK_LOCATION, CcConstants.ENV_SDK_HOME);
		if (sdkHome.isEmpty()) {
			// Check if SDK is not a parent directory of current directory
			try {
				String possibleSdkLocation = new File("..").getCanonicalPath();
				if (projectOptionsComposite.isValidSdkLocation(possibleSdkLocation)) {
					// It is - use this location as default
					sdkHome = possibleSdkLocation;
				}
			} catch (IOException e) {
				CcPlugin.log(e);
			}
		}
		projectOptionsComposite.setSdkLocation(sdkHome);
		
		String boardName = getDefaultValue(CcConstants.WIZARD_LAST_BOARD, CcConstants.ENV_BOARD);
		projectOptionsComposite.setBoardName(boardName);
		
		String debugPort = getDefaultValue(CcConstants.WIZARD_LAST_DEBUG_PORT, CcConstants.ENV_DBG_PORT);
		projectOptionsComposite.setDebugPort(debugPort);
		
		String uartPort = getDefaultValue(CcConstants.WIZARD_LAST_UART_PORT, CcConstants.ENV_UART_PORT);
		projectOptionsComposite.setUartPort(uartPort);
		
		makefileBuildCheckBtn.setSelection(true);
	}
	
	public void saveLastValues() {
		SdkOptionsModel model = getSdkOptions();
		IPreferenceStore preferenceStore = CcUIPlugin.getDefault().getPreferenceStore();
		preferenceStore.setValue(CcConstants.WIZARD_LAST_SDK_LOCATION, model.getSdkLocation());
		preferenceStore.setValue(CcConstants.WIZARD_LAST_BOARD, model.getBoardName());
		preferenceStore.setValue(CcConstants.WIZARD_LAST_DEBUG_PORT, model.getDebugPort());
		preferenceStore.setValue(CcConstants.WIZARD_LAST_UART_PORT, model.getUartPort());
	}
	
	public SdkOptionsModel getSdkOptions() {
		return projectOptionsComposite.buildModel();
	}
	
	public boolean isCppEnabled() {
		return cppCheckBtn.getSelection();
	}
	
	public boolean isManagedBuildEnabled() {
		return managedBuildCheckBtn.getSelection();
	}
}
