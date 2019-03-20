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
* File Name : AbstractProjectBasedHandler.java
* Author    : Rafal Harabien
* ******************************************************************************
* $Date: 2019-03-19 12:27:38 +0100 (Tue, 19 Mar 2019) $
* $Revision: 397 $
*H*****************************************************************************/

package com.chipcraftic.eplugin.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.handlers.HandlerUtil;

public abstract class AbstractProjectBasedHandler extends AbstractHandler {

	private IResource getActiveResource(ExecutionEvent event) {
		IStructuredSelection selection = HandlerUtil.getCurrentStructuredSelection(event);
		for (Object element : selection.toList()) {
			if (element instanceof IResource) {
				IResource resource = (IResource) element;
				return resource;
			}
		}
		
		IEditorInput editorInput = HandlerUtil.getActiveEditorInput(event);
		if (editorInput instanceof IFileEditorInput) {
			IFileEditorInput fileEditorInput  = (IFileEditorInput)editorInput;
			return fileEditorInput.getFile();
		}
		
		return null;
	}
	
	protected IProject getActiveProject(ExecutionEvent event, boolean showError) {
		IResource resource = getActiveResource(event);
		if (resource == null) {
			if (showError) {
				MessageDialog.openError(HandlerUtil.getActiveShell(event), "Error!", "There is no file selected!");
			}
			return null;
		}
		IProject project = resource.getProject();
		if (project == null && showError) {
			MessageDialog.openError(HandlerUtil.getActiveShell(event), "Error!", "File does not belong to any project!");
		}
		return project;
	}
}
