package com.chipcraftic.eplugin.core.launch.gdb;

import java.util.Hashtable;

import org.eclipse.cdt.core.IAddress;
import org.eclipse.cdt.dsf.concurrent.DataRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.ImmediateRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.datamodel.DMContexts;
import org.eclipse.cdt.dsf.datamodel.IDMContext;
import org.eclipse.cdt.dsf.debug.service.IMemory;
import org.eclipse.cdt.dsf.debug.service.IRunControl.IContainerDMContext;
import org.eclipse.cdt.dsf.debug.service.command.ICommandControlService;
import org.eclipse.cdt.dsf.debug.service.command.IEventListener;
import org.eclipse.cdt.dsf.gdb.service.GDBMemory;
import org.eclipse.cdt.dsf.gdb.service.IGDBMemory;
import org.eclipse.cdt.dsf.gdb.service.IGDBMemory2;
import org.eclipse.cdt.dsf.mi.service.IMIExecutionDMContext;
import org.eclipse.cdt.dsf.mi.service.IMIProcesses;
import org.eclipse.cdt.dsf.mi.service.MIMemory;
import org.eclipse.cdt.dsf.mi.service.command.output.MIConst;
import org.eclipse.cdt.dsf.mi.service.command.output.MINotifyAsyncOutput;
import org.eclipse.cdt.dsf.mi.service.command.output.MIOOBRecord;
import org.eclipse.cdt.dsf.mi.service.command.output.MIOutput;
import org.eclipse.cdt.dsf.mi.service.command.output.MIResult;
import org.eclipse.cdt.dsf.mi.service.command.output.MIValue;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.cdt.utils.Addr64;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.model.MemoryByte;

import com.chipcraftic.eplugin.core.CcPlugin;
import com.chipcraftic.eplugin.core.dsf.ICcFocusService;

/**
 * Based on GDBMemory_7_6. Entire hierarchy of classes has to be overriden to change behaviour of readMemoryBlock from
 * GDBMemory_7_0 without copying implementation from MIMemory etc.
 * Note: it would be much simpler if super-super method could be called but it is not possible in Java.
 * 
 * @author rafalh
 *
 */
public class CcGDBMemory extends GDBMemory implements IEventListener {

	private ICommandControlService fConnection;
	
	public CcGDBMemory(DsfSession session) {
		super(session);
	}
	
	// Based on implementation in GDBMemory_7_6
	@Override
	public void initialize(final RequestMonitor requestMonitor) {
		super.initialize(
				new ImmediateRequestMonitor(requestMonitor) { 
					@Override
					public void handleSuccess() {
						doInitialize(requestMonitor);
					}});
	}

	// Based on implementation in GDBMemory_7_6
	private void doInitialize(final RequestMonitor requestMonitor) {
		register(new String[] { MIMemory.class.getName(), 
				                IMemory.class.getName(), 
				                IGDBMemory.class.getName(),
								IGDBMemory2.class.getName(),
				                GDBMemory.class.getName()}, 
				 new Hashtable<String, String>());
		
		fConnection = getServicesTracker().getService(ICommandControlService.class);
		if (fConnection == null) {
			requestMonitor.done(new Status(IStatus.ERROR, CcPlugin.PLUGIN_ID, "CommandControl Service is not available")); //$NON-NLS-1$
			return;
		}
		fConnection.addEventListener(this);
		
		requestMonitor.done();
	}

	// Based on implementation in GDBMemory_7_6
	@Override
	public void shutdown(final RequestMonitor requestMonitor) {
		fConnection.removeEventListener(this);
		unregister();
		super.shutdown(requestMonitor);
	}
	
	@Override
	protected void readMemoryBlock(IDMContext dmc, IAddress address, long offset, int word_size, int word_count,
			DataRequestMonitor<MemoryByte[]> drm) {
		// Make sure context given to super method contains execution context (thread).
		// This is needed because some memory regions in ChipCraft micro-controller are different for each core (simulated as thread).
		// For example SPRAM.
		IMIExecutionDMContext executionCtx = DMContexts.getAncestorOfType(dmc, IMIExecutionDMContext.class);
		if (executionCtx == null) {
			ICcFocusService focusService = getServicesTracker().getService(ICcFocusService.class);
			executionCtx = focusService.getCurrentExecutionContext();
			if (executionCtx != null) {
				dmc = executionCtx;
			}
		}
		
		super.readMemoryBlock(dmc, address, offset, word_size, word_count, drm);
	}
	
	@Override
	protected void writeMemoryBlock(IDMContext dmc, IAddress address, long offset, int wordSize, int wordCount,
			byte[] buffer, RequestMonitor rm) {
		// Make sure context given to super method contains execution context (thread).
		// This is needed because some memory regions in ChipCraft micro-controller are different for each core (simulated as thread).
		// For example SPRAM.
		IMIExecutionDMContext executionCtx = DMContexts.getAncestorOfType(dmc, IMIExecutionDMContext.class);
		if (executionCtx == null) {
			ICcFocusService focusService = getServicesTracker().getService(ICcFocusService.class);
			executionCtx = focusService.getCurrentExecutionContext();
			if (executionCtx != null) {
				dmc = executionCtx;
			}
		}
		
		super.writeMemoryBlock(dmc, address, offset, wordSize, wordCount, buffer, rm);
	}

	// Copied from GDBMemory_7_6
	// Not sure if CCSDK GDB integration can generate async memory-changed events...
	@Override
	public void eventReceived(Object output) {
		if (output instanceof MIOutput) {
			MIOOBRecord[] records = ((MIOutput)output).getMIOOBRecords();
			for (MIOOBRecord r : records) {
				if (r instanceof MINotifyAsyncOutput) {
					MINotifyAsyncOutput notifyOutput = (MINotifyAsyncOutput)r;
					String asyncClass = notifyOutput.getAsyncClass();
					// These events have been added with GDB 7.6
					if ("memory-changed".equals(asyncClass)) { //$NON-NLS-1$
	    				String groupId = null;
	    				String addr = null;
	    				int count = 0;

		   				MIResult[] results = notifyOutput.getMIResults();
	    				for (int i = 0; i < results.length; i++) {
	    					String var = results[i].getVariable();
	    					MIValue val = results[i].getMIValue();
	    					if (var.equals("thread-group")) { //$NON-NLS-1$
	    						if (val instanceof MIConst) {
	    							groupId = ((MIConst)val).getString();
	    						}
	    					} else if (var.equals("addr")) { //$NON-NLS-1$
	    		    			if (val instanceof MIConst) {
	    		    				addr = ((MIConst)val).getString();
	    		    			}
	    					} else if (var.equals("len")) { //$NON-NLS-1$
	    						if (val instanceof MIConst) {
	    							try {
	    								String lenStr = ((MIConst)val).getString().trim();
	    								// count is expected in addressable units
	    								if (lenStr.startsWith("0x")) { //$NON-NLS-1$
	    									count = Integer.parseInt(lenStr.substring(2), 16);	    									
	    								} else {
	    									count = Integer.parseInt(lenStr);
	    								}
	    			                } catch (NumberFormatException e) {
	    			                	assert false;
	    			                }
	    						}
	    					} else if (var.equals("type")) { //$NON-NLS-1$
	    						if (val instanceof MIConst) {
	    							if ("code".equals(((MIConst)val).getString())) { //$NON-NLS-1$
	    							}
	    						}
	    					}
	    				}
	    				
	    		    	IMIProcesses procService = getServicesTracker().getService(IMIProcesses.class);
	    		    	if (procService != null && groupId != null && addr != null && count > 0) {
	    		    		IContainerDMContext containerDmc = 
	    		    				procService.createContainerContextFromGroupId(fConnection.getContext(), groupId);
	    		    		
	    		    		// Now refresh our memory cache, it case it contained this address.  Don't have
	    		    		// it send the potential IMemoryChangedEvent as we will send it ourselves (see below).
	    		    		final IMemoryDMContext memoryDMC = DMContexts.getAncestorOfType(containerDmc, IMemoryDMContext.class);
	    		    		
	    		    		final IAddress address = new Addr64(addr);
	    		    		getMemoryCache(memoryDMC).refreshMemory(memoryDMC, address, 0, getAddressableSize(memoryDMC), count, false,
	    		    				new RequestMonitor(getExecutor(), null) {
	    		    			@Override
	    		    			protected void handleCompleted() {
	    		    				// Only once the memory cache is updated, we send the IMemoryChangedEvent.  If we were to do it
	    		    				// earlier, the memory view may not show the updated value.
	    		    				//
	    		    				// We must always send this event when GDB reports a memory change because it can mean that
	    		    				// an expression or register has changed, and therefore we must notify the different views 
	    		    				// and services of it.  We cannot rely on this event to be sent by the memory cache after being
	    		    				// refreshed, because if the memory cache does not contain this address, it will not send
	    		    				// the event.
	    		    				getSession().dispatchEvent(new MemoryChangedEvent(memoryDMC, new IAddress[] { address }), getProperties());
	    		    			}	    		    			
	    		    		});
	    		    	}
					}
				}
			}
		}
	}
}
