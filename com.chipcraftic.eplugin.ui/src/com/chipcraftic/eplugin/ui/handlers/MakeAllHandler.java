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
* File Name : MakeAllHandler.java
* Author    : Rafal Harabien
* ******************************************************************************
* $Date: 2019-03-19 12:27:38 +0100 (Tue, 19 Mar 2019) $
* $Revision: 397 $
*H*****************************************************************************/

package com.chipcraftic.eplugin.ui.handlers;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.handlers.HandlerUtil;

import com.chipcraftic.eplugin.core.CcPlugin;

public class MakeAllHandler extends AbstractProjectBasedHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IProject project = getActiveProject(event, true);
		if (project == null) {
			return null;
		}
		IRunnableWithProgress runnable = new WorkspaceModifyOperation() {
			@Override
			protected void execute(IProgressMonitor monitor) throws CoreException {
				project.build(IncrementalProjectBuilder.FULL_BUILD, monitor);
			}
		};
		try {
			HandlerUtil.getActiveWorkbenchWindow(event).run(true, true, runnable);
		} catch (InvocationTargetException | InterruptedException e) {
			CcPlugin.log(e);
		}
		return null;
	}
}
