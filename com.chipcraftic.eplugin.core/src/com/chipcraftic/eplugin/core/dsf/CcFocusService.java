
package com.chipcraftic.eplugin.core.dsf;

import java.math.BigInteger;
import java.util.Hashtable;
import java.util.Objects;

import org.eclipse.cdt.dsf.concurrent.ImmediateRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.datamodel.DMContexts;
import org.eclipse.cdt.dsf.datamodel.IDMContext;
import org.eclipse.cdt.dsf.debug.model.DsfMemoryBlock;
import org.eclipse.cdt.dsf.mi.service.IMIExecutionDMContext;
import org.eclipse.cdt.dsf.mi.service.MIMemory;
import org.eclipse.cdt.dsf.service.AbstractDsfService;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IMemoryBlockManager;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.osgi.framework.BundleContext;

import com.chipcraftic.eplugin.core.CcPlugin;

public class CcFocusService extends AbstractDsfService implements ICcFocusService {

	private IMIExecutionDMContext fCurrentExecutionContext;
	
	public CcFocusService(DsfSession session) {
		super(session);
	}
	
	@Override
	public void initialize(final RequestMonitor requestMonitor) {
		super.initialize(new ImmediateRequestMonitor(requestMonitor) {
			@Override
			protected void handleSuccess() {
				doInitialize(requestMonitor);
			}
		});
	}

	private void doInitialize(RequestMonitor requestMonitor) {
		register(new String[] { ICcFocusService.class.getName()}, new Hashtable<String, String>());
		requestMonitor.done();
	}

	@Override
	public void shutdown(RequestMonitor requestMonitor) {
		unregister();
		super.shutdown(requestMonitor);
	}

	@Override
	public void setCurrentContext(IDMContext context) {
		IMIExecutionDMContext executionContext = DMContexts.getAncestorOfType(context, IMIExecutionDMContext.class);
		if (executionContext != null) {
			if (!Objects.equals(fCurrentExecutionContext, executionContext)) {
				fCurrentExecutionContext = executionContext;
				handleThreadChange(fCurrentExecutionContext);
			}
		}
	}

	@Override
	protected BundleContext getBundleContext() {
		return CcPlugin.getDefault().getBundle().getBundleContext();
	}
	
	void handleThreadChange(IMIExecutionDMContext executionContext) {
		flushMemoryCache(executionContext);
		sendSuspendEvent();
	}

	@Override
	public IMIExecutionDMContext getCurrentExecutionContext() {
		return fCurrentExecutionContext;
	}
	
	private void flushMemoryCache(IDMContext context) {
		// Reset MIMemory cache
		MIMemory memoryService = getServicesTracker().getService(MIMemory.class);
		memoryService.flushCache(context);
		
		// Reset all memory blocks cache
	    IMemoryBlockManager memoryBlockMgr = DebugPlugin.getDefault().getMemoryBlockManager();
	    for (IMemoryBlock memoryBlock: memoryBlockMgr.getMemoryBlocks()) {
	    	if (memoryBlock instanceof DsfMemoryBlock) {
	    		DsfMemoryBlock dsfMemBlock = (DsfMemoryBlock)memoryBlock;
		    	dsfMemBlock.clearCache();
		    	// Note: ZERO has special handling in handleMemoryChange.
		    	// It avoids checking if address belongs to this memory block.
		    	dsfMemBlock.handleMemoryChange(BigInteger.ZERO);
	    	}
	    }
	}
	
	private void sendSuspendEvent() {
		// Force refreshing of MemoryView and EmbSysRegView by fire'ing SUSPEND event.
		// See GDBEventListener class in EmbSysRegView plugin.
		// Looks hacky but EmbSysRegView plugin does similar action in GDBEventProvider after
		// getting ISuspendedDMEvent.
		// Note: sending ISuspendedDMEvent would cause DSF to set focus on the first frame which is unwanted behavior.
		DebugEvent event = new DebugEvent(getSession(), DebugEvent.SUSPEND);
	    DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[]{event});
	}
}
