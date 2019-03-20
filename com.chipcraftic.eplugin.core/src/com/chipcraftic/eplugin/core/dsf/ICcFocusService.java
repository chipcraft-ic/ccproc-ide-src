package com.chipcraftic.eplugin.core.dsf;

import org.eclipse.cdt.dsf.datamodel.IDMContext;
import org.eclipse.cdt.dsf.mi.service.IMIExecutionDMContext;
import org.eclipse.cdt.dsf.service.IDsfService;

public interface ICcFocusService extends IDsfService {
	void setCurrentContext(IDMContext context);
	
	IMIExecutionDMContext getCurrentExecutionContext();
}
