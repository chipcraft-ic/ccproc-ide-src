package com.chipcraftic.eplugin.ui.wizard;

import java.util.Map;

import org.eclipse.tools.templates.freemarker.FMGenerator;
import org.osgi.framework.Bundle;

import com.chipcraftic.eplugin.ui.CcUIPlugin;
import com.chipcraftic.eplugin.ui.wizard.model.ProjectGeneratorModel;

public class CcProjectGenerator extends FMGenerator {
	
	private ProjectGeneratorModel data;
	
	public CcProjectGenerator(String manifestPath, ProjectGeneratorModel data) {
		super(manifestPath);
		this.data = data;
	}

	@Override
	protected Bundle getSourceBundle() {
		return CcUIPlugin.getDefault().getBundle();
	}
	
	@Override
	protected void populateModel(Map<String, Object> model) {
		super.populateModel(model);
		model.put("projectName", data.getProjectName());
		model.put("spiList", data.getPeripherials().getSpiList());
		model.put("uartList", data.getPeripherials().getUartList());
	}
}
