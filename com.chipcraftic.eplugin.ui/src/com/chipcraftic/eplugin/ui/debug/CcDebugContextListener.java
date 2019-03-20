package com.chipcraftic.eplugin.ui.debug;

import org.eclipse.cdt.dsf.datamodel.IDMContext;
import org.eclipse.cdt.dsf.debug.service.IStack.IFrameDMContext;
import org.eclipse.cdt.dsf.mi.service.IMIContainerDMContext;
import org.eclipse.cdt.dsf.mi.service.IMIExecutionDMContext;
import org.eclipse.cdt.dsf.service.DsfServicesTracker;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.contexts.DebugContextEvent;
import org.eclipse.debug.ui.contexts.IDebugContextListener;

import com.chipcraftic.eplugin.core.dsf.ICcFocusService;
import com.chipcraftic.eplugin.ui.CcUIPlugin;


/**
 * Based on GdbDebugContextSyncManager.
 * @author rafalh
 *
 */
public class CcDebugContextListener implements IDebugContextListener {
	
	@Override
	public void debugContextChanged(DebugContextEvent event) {
		// Make sure that it's a change of selection that caused the event
		if ((event.getFlags() != DebugContextEvent.ACTIVATED)) {
			return;
		}

		// Get selected element in the Debug View
		IAdaptable context = DebugUITools.getDebugContext();

		if (context != null) {
			final IDMContext dmc = context.getAdapter(IDMContext.class);

			if (dmc instanceof IMIContainerDMContext ||
					dmc instanceof IMIExecutionDMContext || 
					dmc instanceof IFrameDMContext) {
				// A process, thread or stack frame was selected. In each case, have GDB switch to the new
				// corresponding thread, if required.

				// Resolve the debug session
				String eventSessionId = dmc.getSessionId();
				if (!(DsfSession.isSessionActive(eventSessionId))) {
					return;
				}

				DsfSession session = DsfSession.getSession(eventSessionId);

				// order GDB to switch thread
				session.getExecutor().execute(new Runnable() {
					@Override
					public void run() {
						DsfServicesTracker tracker = new DsfServicesTracker(CcUIPlugin.getBundleContext(), eventSessionId);
						ICcFocusService threadTracker = tracker.getService(ICcFocusService.class);
						
						if (threadTracker != null) {
							threadTracker.setCurrentContext(dmc);
						}
						tracker.dispose();
					}
				});
			}
		}
	}
}
