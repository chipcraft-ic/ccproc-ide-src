package com.chipcraftic.eplugin.core.launch.gdb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.gdb.launching.GdbLaunch;
import org.eclipse.cdt.dsf.gdb.launching.ServicesLaunchSequence;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.IProgressMonitor;

import com.chipcraftic.eplugin.core.dsf.ICcFocusService;

public class CcServicesLaunchSequence extends ServicesLaunchSequence {

	private DsfSession fSession;
    private GdbLaunch fLaunch;
	
	public CcServicesLaunchSequence(DsfSession session, GdbLaunch launch, IProgressMonitor pm) {
		super(session, launch, pm);
		fSession = session;
        fLaunch = launch;
	}
	
	@Override
	public Step[] getSteps() {
		List<Step> steps = new ArrayList<>(Arrays.asList(super.getSteps()));
		steps.add(new Step() {
			@Override
	        public void execute(RequestMonitor requestMonitor) {
				fLaunch.getServiceFactory().createService(ICcFocusService.class, fSession, fLaunch.getLaunchConfiguration()).initialize(requestMonitor);
	        }
		});
		return steps.toArray(new Step[0]);
	}
}
