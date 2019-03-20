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
* File Name : TermConsolePageParticipant.java
* Author    : Rafal Harabien
* ******************************************************************************
* $Date: 2019-03-19 12:27:38 +0100 (Tue, 19 Mar 2019) $
* $Revision: 397 $
*H*****************************************************************************/

package com.chipcraftic.eplugin.ui.console;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsolePageParticipant;
import org.eclipse.ui.part.IPageBookViewPage;

import com.chipcraftic.eplugin.ui.actions.StopTermAction;

public class TermConsolePageParticipant implements IConsolePageParticipant {

	private StopTermAction stopAction;
	
	@Override
	public <T> T getAdapter(Class<T> adapter) {
		return null;
	}

	@Override
	public void init(IPageBookViewPage page, IConsole console) {
		ProcessConsoleContext consoleContext = ((TermConsole)console).getContext();
		IToolBarManager manager = page.getSite().getActionBars().getToolBarManager();
	    stopAction = new StopTermAction(consoleContext);
	    manager.appendToGroup(IConsoleConstants.LAUNCH_GROUP, stopAction);
	    consoleContext.addListener(stopAction);
	}

	@Override
	public void dispose() {
		if (stopAction.isEnabled()) {
			stopAction.run();
		}
	}

	@Override
	public void activated() {
		// empty
	}

	@Override
	public void deactivated() {
		// empty
	}
}
