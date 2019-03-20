package com.chipcraftic.eplugin.ui.launch;

import org.eclipse.cdt.debug.gdbjtag.core.IGDBJtagConstants;
import org.eclipse.cdt.debug.gdbjtag.ui.GDBJtagDSFDebuggerTab;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.chipcraftic.eplugin.core.CcPlugin;
import com.chipcraftic.eplugin.core.launch.CcLaunchAttributes;

public class CcDebuggerTab extends GDBJtagDSFDebuggerTab {

	protected Text fDbgServerOptsText;
	protected Text fDbgServerPythonText;
	
	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy wc) {
		super.setDefaults(wc);
		
		String property = "java.io.tmpdir";
		String tempDir = System.getProperty(property);
		
		wc.setAttribute(IGDBJtagConstants.ATTR_JTAG_DEVICE, "CC Device");
		wc.setAttribute(IGDBJtagConstants.ATTR_CONNECTION, "gdb:dummy");
		wc.setAttribute(CcLaunchAttributes.DBG_SERVER_OPTS, "--log INFO --log-file " +
				new Path(tempDir + "/dbgserver.log").toOSString());
		
		if (Platform.getOS().equals(Platform.OS_WIN32)) {
			wc.setAttribute(CcLaunchAttributes.DBG_SERVER_PYTHON, "python");
		} else {
			wc.setAttribute(CcLaunchAttributes.DBG_SERVER_PYTHON, "python2");
		}
	}
	
	@Override
	public void createControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout());
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		setControl(comp);
		
		Group group = new Group(comp, SWT.NONE);
		GridLayout layout = new GridLayout();
		group.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		group.setLayoutData(gd);
		group.setText("Debug Server");
		
		Label label = new Label(group, SWT.NONE);
		label.setText("Additional Options:");

		fDbgServerOptsText = new Text(group, SWT.BORDER);
		fDbgServerOptsText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fDbgServerOptsText.addModifyListener(e -> scheduleUpdateJob());
		
		label = new Label(group, SWT.NONE);
		label.setText("Python Executable:");

		fDbgServerPythonText = new Text(group, SWT.BORDER);
		fDbgServerPythonText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fDbgServerPythonText.addModifyListener(e -> scheduleUpdateJob());
	}
	
	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			fDbgServerOptsText.setText(configuration.getAttribute(CcLaunchAttributes.DBG_SERVER_OPTS, ""));
			fDbgServerPythonText.setText(configuration.getAttribute(CcLaunchAttributes.DBG_SERVER_PYTHON, "python"));
		} catch (CoreException e) {
			CcPlugin.log(e);
		}
	}
	
	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(CcLaunchAttributes.DBG_SERVER_OPTS, fDbgServerOptsText.getText());
		configuration.setAttribute(CcLaunchAttributes.DBG_SERVER_PYTHON, fDbgServerPythonText.getText());
	}
}
