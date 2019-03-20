package com.chipcraftic.eplugin.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IDynamicVariable;
import org.eclipse.core.variables.IDynamicVariableResolver;

public class SdkLocResolver implements IDynamicVariableResolver {

	@Override
	public String resolveValue(IDynamicVariable variable, String argument) throws CoreException {
		return System.getProperty("user.home") + "/chipcraft/ccsdk"; // FIXME
	}

}
