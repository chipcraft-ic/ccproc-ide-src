package com.chipcraftic.eplugin.core.launch;

import com.chipcraftic.eplugin.core.CcPlugin;

public final class CcLaunchAttributes {
	private CcLaunchAttributes() {
		// disable construction
	}

	public static final String DBG_SERVER_OPTS = CcPlugin.PLUGIN_ID + ".dbgserver.options";
	
	public static final String DBG_SERVER_PYTHON = CcPlugin.PLUGIN_ID + ".dbgserver.python";
}
