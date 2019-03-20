package com.chipcraftic.eplugin.ui.launch;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class DummyLaunchConfigurationDialog implements ILaunchConfigurationDialog {

	@Override
	public void run(boolean fork, boolean cancelable, IRunnableWithProgress runnable)
			throws InvocationTargetException, InterruptedException {
		// Nothing to do
	}

	@Override
	public void updateButtons() {
		// Nothing to do

	}

	@Override
	public void updateMessage() {
		// Nothing to do

	}

	@Override
	public void setName(String name) {
		// Nothing to do
	}

	@Override
	public String generateName(String name) {
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		if (name == null) {
			return launchManager.generateLaunchConfigurationName("");
		}
		return launchManager.generateLaunchConfigurationName(name);
	}

	@Override
	public ILaunchConfigurationTab[] getTabs() {
		// Nothing to do
		return null;
	}

	@Override
	public ILaunchConfigurationTab getActiveTab() {
		// Nothing to do
		return null;
	}

	@Override
	public String getMode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setActiveTab(ILaunchConfigurationTab tab) {
		// Nothing to do
	}

	@Override
	public void setActiveTab(int index) {
		// Nothing to do
	}

}
