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
* File Name : SimShortcut.java
* Author    : Rafal Harabien
* ******************************************************************************
* $Date: 2019-03-19 12:27:38 +0100 (Tue, 19 Mar 2019) $
* $Revision: 397 $
*H*****************************************************************************/

package com.chipcraftic.eplugin.ui.launch;

import java.io.File;

import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchConfigurationPresentationManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTabGroup;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

import com.chipcraftic.eplugin.core.CcPlugin;

/**
 * Based on CApplicationLaunchShortcut.
 * @author Rafał Harabień
 *
 */
public abstract class AbstractCcLaunchShortcut implements ILaunchShortcut {

	protected static final String BUILD_DIR = "build";
	
	@Override
	public void launch(ISelection selection, String mode) {
		launch(getProject(selection), mode);
	}
	
	@Override
	public void launch(IEditorPart editor, String mode) {
		launch(getProject(editor), mode);
	}
	
	private void launch(IProject project, String mode) {
		if (project == null) {
			MessageDialog.openInformation(null, "Cannot find project", "Cannot find project for launch!");
			return;
		}
		
		ILaunchConfiguration config = findLaunchConfiguration(project, mode);
		if (config == null) {
			config = createConfiguration(project, mode);
		}
		
		DebugUITools.launch(config, mode);
	}
	
	protected boolean isLaunchConfigMatching(ILaunchConfiguration config, IProject project) throws CoreException {
		IResource mappedResource = null;
		if (config.getMappedResources() != null && config.getMappedResources().length > 0) {
			mappedResource = config.getMappedResources()[0];
		}
		if (config.getType() == getLaunchConfigurationType() && mappedResource == project) {
			return true;
		}
		return false;
	}
	
	protected ILaunchConfiguration findLaunchConfiguration(IProject project, String mode) {
		try {
			ILaunchConfiguration[] configs = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations();
			for (int i = 0; i < configs.length; i++) {
				ILaunchConfiguration config = configs[i];
				if (isLaunchConfigMatching(config, project)) {
					return config;
				}
			}
		} catch (CoreException e) {
		    CcPlugin.log(e);
		}
		return null;
	}

	private IProject getProject(ISelection selection) {
		// Take the selection and determine which project is intended to
		// be used for the launch.
		if (selection instanceof IStructuredSelection) {
			for (Object element : ((IStructuredSelection) selection).toList()) {
				IResource resource = Adapters.adapt(element, IResource.class);
				if (resource != null) {
					return resource.getProject();
				}
			}
		}
		return null;
	}
	
	private IProject getProject(IEditorPart editor) {
		IEditorInput input = editor.getEditorInput();
		IFileEditorInput fileInput = Adapters.adapt(input, IFileEditorInput.class);
		if (fileInput != null) {
			return fileInput.getFile().getProject();
		}
		return null;
	}

	protected abstract String getLaunchConfigurationTypeId();
	
	protected ILaunchConfigurationType getLaunchConfigurationType() {
		return DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType(getLaunchConfigurationTypeId());
	}
	
	protected abstract String getLaunchConfigurationNamePrefix(IProject project);
	
	protected void setDefaultAttributes(ILaunchConfigurationWorkingCopy wc, IProject project, String mode) throws CoreException {
		// Set default attributes using tab group
		// This uses internal class but the alternative is to set all attributes by hand...
		ILaunchConfigurationTabGroup tabGroup = LaunchConfigurationPresentationManager.getDefault().getTabGroup(wc, mode);
		ILaunchConfigurationDialog dummyDialog = new DummyLaunchConfigurationDialog();
		tabGroup.createTabs(new DummyLaunchConfigurationDialog(), mode);
		for (ILaunchConfigurationTab tab: tabGroup.getTabs()) {
			tab.setLaunchConfigurationDialog(dummyDialog);
		}
		tabGroup.setDefaults(wc);
	}
	
	protected ILaunchConfiguration createConfiguration(IProject project, String mode) {
		try {
			ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
			String namePrefix = getLaunchConfigurationNamePrefix(project);
			String name = launchManager.generateLaunchConfigurationName(namePrefix);
			ILaunchConfigurationType confType = getLaunchConfigurationType();
			ILaunchConfigurationWorkingCopy wc = confType.newInstance(null, name);
			
			wc.setMappedResources(new IResource[] { project });
			wc.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROJECT_NAME, project.getName());
			
			setDefaultAttributes(wc, project, mode);
			
			// Set default program name
			if (wc.getAttribute(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_NAME, "").isEmpty()) {
				// ATTR_PROGRAM_NAME is empty only if Managed Build is disabled
				String programPath = BUILD_DIR + File.separator + project.getName();
				wc.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_NAME, programPath);
			}
			
			return wc.doSave();
		} catch (CoreException e) {
			CcPlugin.log(e);
		}
		
		return null;
	}

}
