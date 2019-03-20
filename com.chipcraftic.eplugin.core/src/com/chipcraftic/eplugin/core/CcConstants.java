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
* File Name : CcConstants.java
* Author    : Rafal Harabien
* ******************************************************************************
* $Date: 2019-03-19 12:27:38 +0100 (Tue, 19 Mar 2019) $
* $Revision: 397 $
*H*****************************************************************************/

package com.chipcraftic.eplugin.core;

public final class CcConstants {
	
	private CcConstants() {
		// denied
	}
	
	// Environment variables
	public static final String ENV_SDK_HOME = "CCSDK_HOME";
	public static final String ENV_BOARD = "CCSDK_BOARD";
	public static final String ENV_DBG_PORT = "CCSDK_DBG_PORT";
	public static final String ENV_UART_PORT = "CCSDK_UART_PORT";
	public static final String ENV_QUIET = "QUIET";
	
	// Preferences
	public static final String WIZARD_LAST_SDK_LOCATION = "wizard.sdk.location";
	public static final String WIZARD_LAST_BOARD = "wizard.board";
	public static final String WIZARD_LAST_DEBUG_PORT = "wizard.debug.port";
	public static final String WIZARD_LAST_UART_PORT = "wizard.uart.port";
	
	public static final String TOOLCHAIN_TRIPLET = "mips-cc-elf";
}
