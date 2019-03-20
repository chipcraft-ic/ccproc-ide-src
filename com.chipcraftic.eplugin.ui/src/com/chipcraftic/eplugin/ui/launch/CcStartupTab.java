package com.chipcraftic.eplugin.ui.launch;

import org.eclipse.cdt.debug.gdbjtag.core.IGDBJtagConstants;
import org.eclipse.cdt.debug.gdbjtag.ui.GDBJtagStartupTab;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

import com.chipcraftic.eplugin.core.CcPlugin;
import com.chipcraftic.eplugin.core.launch.SimLaunchDelegate;

public class CcStartupTab extends GDBJtagStartupTab {
	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy wc) {
		super.setDefaults(wc);

		wc.setAttribute(IGDBJtagConstants.ATTR_DELAY, 0);
		wc.setAttribute(IGDBJtagConstants.ATTR_DO_RESET, true);
		wc.setAttribute(IGDBJtagConstants.ATTR_DO_HALT, true);
		if (isSimLaunchConfigType(wc)) {
			wc.setAttribute(IGDBJtagConstants.ATTR_LOAD_IMAGE, false);
		} else {
			wc.setAttribute(IGDBJtagConstants.ATTR_LOAD_IMAGE, true);
		}
		wc.setAttribute(IGDBJtagConstants.ATTR_SET_STOP_AT, true);
		wc.setAttribute(IGDBJtagConstants.ATTR_STOP_AT, "main");
		wc.setAttribute(IGDBJtagConstants.ATTR_SET_RESUME, true);
	}
	
	private boolean isSimLaunchConfigType(ILaunchConfiguration launchConfig) {
		String launchConfigTypeId = null;
		try {
			launchConfigTypeId = launchConfig.getType().getIdentifier();
		} catch (CoreException e) {
			CcPlugin.log(e);
		}
		return SimLaunchDelegate.CONFIG_TYPE_ID.equals(launchConfigTypeId);
	}
}
