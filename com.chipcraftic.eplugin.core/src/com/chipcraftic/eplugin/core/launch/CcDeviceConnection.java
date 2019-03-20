package com.chipcraftic.eplugin.core.launch;

import java.util.Collection;

import org.eclipse.cdt.debug.gdbjtag.core.IGDBJtagConnection;
import org.eclipse.cdt.debug.gdbjtag.core.jtagdevice.DefaultGDBJtagConnectionImpl;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.VariablesPlugin;

import com.chipcraftic.eplugin.core.CcPlugin;

public class CcDeviceConnection extends DefaultGDBJtagConnectionImpl implements IGDBJtagConnection {
	
	@Override
	public void doRemote(String connection, Collection<String> commands) {
		String cmd = ""; //$NON-NLS-1$
		if (connection != null) {
			// The CLI version (target remote) does not let us know
			// that we have properly connected.  For older GDBs (<= 6.8)
			// we need this information for a DSF session.
			// The MI version does tell us, which is why we must use it
			// Bug 348043
			try {
				connection = VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution(connection);
			} catch (CoreException e) {
				CcPlugin.log(e);
			}
			cmd = "-target-select remote " + connection; //$NON-NLS-1$
			addCmd(commands, cmd);
		}
	}
	
	@Override
	public void doLoadImage(String imageFileName, String imageOffset, Collection<String> commands) {
		super.doLoadImage(imageFileName, imageOffset, commands);
		// After programming reset is required or processor fails running program in startup file
		addCmd(commands, "monitor reset halt");
		addCmd(commands, "monitor gdb_sync");
		addCmd(commands, "stepi");
	}
}