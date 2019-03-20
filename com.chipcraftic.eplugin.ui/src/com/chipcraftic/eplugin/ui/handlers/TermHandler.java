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
* File Name : TermHandler.java
* Author    : Rafal Harabien
* ******************************************************************************
* $Date: 2019-03-19 12:27:38 +0100 (Tue, 19 Mar 2019) $
* $Revision: 397 $
*H*****************************************************************************/

package com.chipcraftic.eplugin.ui.handlers;

import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IOConsole;

import com.chipcraftic.eplugin.ui.console.TermConsole;

public class TermHandler extends AbstractMakeBasedHandler {
	
	@Override
	public String getMakeTarget() {
		return "term";
	}
	
	@Override
	protected boolean isReadOnly() {
		return false;
	}
	
	@Override
	protected boolean isConsoleApplicable(IConsole console) {
		return console instanceof TermConsole;
	}
	
	@Override
	protected IOConsole createConsole() {
		return new TermConsole();
	}
}
