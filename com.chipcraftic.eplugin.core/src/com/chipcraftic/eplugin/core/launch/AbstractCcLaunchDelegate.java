package com.chipcraftic.eplugin.core.launch;

import org.eclipse.cdt.debug.gdbjtag.core.GDBJtagDSFLaunchConfigurationDelegate;
import org.eclipse.cdt.dsf.concurrent.Sequence;
import org.eclipse.cdt.dsf.debug.service.IDsfDebugServicesFactory;
import org.eclipse.cdt.dsf.gdb.launching.GdbLaunch;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ISourceLocator;

import com.chipcraftic.eplugin.core.launch.gdb.CcGdbDebugServicesFactory;
import com.chipcraftic.eplugin.core.launch.gdb.CcGdbLaunch;
import com.chipcraftic.eplugin.core.launch.gdb.CcServicesLaunchSequence;

public class AbstractCcLaunchDelegate extends GDBJtagDSFLaunchConfigurationDelegate {
	
	@Override
	protected GdbLaunch createGdbLaunch(ILaunchConfiguration configuration, String mode, ISourceLocator locator)
			throws CoreException {
		return new CcGdbLaunch(configuration, mode, locator);
	}
	
	@Override
	protected IDsfDebugServicesFactory newServiceFactory(ILaunchConfiguration config, String version) {
		return new CcGdbDebugServicesFactory(version, config);
	}
	
	@Override
	protected Sequence getServicesSequence(DsfSession session, ILaunch launch, IProgressMonitor rm) {
		return new CcServicesLaunchSequence(session, (GdbLaunch)launch, rm);
	}
	
	
}
