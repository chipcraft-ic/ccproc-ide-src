package com.chipcraftic.eplugin.ui.options;

import java.io.File;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.envvar.IContributedEnvironment;
import org.eclipse.cdt.core.envvar.IEnvironmentVariable;
import org.eclipse.cdt.core.envvar.IEnvironmentVariableManager;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescriptionManager;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

import com.chipcraftic.eplugin.core.CcConstants;
import com.chipcraftic.eplugin.core.CcPlugin;
import com.chipcraftic.eplugin.core.CcUtils;

public class CcsdkOptionsPropertyPage extends PropertyPage implements IWorkbenchPropertyPage {

	private static final String TOOLCHAIN_TARGET = "mips-cc-elf";
	private static final String TOOLCHAIN_PREFIX = TOOLCHAIN_TARGET + "-";

	private static final String GNU_CROSS_TOOLCHAIN_OPTION_PREFIX = "cdt.managedbuild.option.gnu.cross.prefix";
	private static final String GNU_CROSS_TOOLCHAIN_OPTION_PATH = "cdt.managedbuild.option.gnu.cross.path";
	
	private CcsdkProjectOptionsComposite projectOptionsComposite;
	
	public CcsdkOptionsPropertyPage() {
		noDefaultButton();
		projectOptionsComposite = new CcsdkProjectOptionsComposite(e -> {
			projectOptionsComposite.validatePage(this);
		});
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = projectOptionsComposite.createControl(parent);
		IProject project = Adapters.adapt(getElement(), IProject.class);
		projectOptionsComposite.setSdkLocation(CcUtils.getSdkLocation(project));
		projectOptionsComposite.setBoardName(CcUtils.getBoard(project));
		projectOptionsComposite.setDebugPort(CcUtils.getDebugPort(project));
		projectOptionsComposite.setUartPort(CcUtils.getUartPort(project));
		return composite;
	}
	
	@Override
	public boolean performOk() {
		IProject project = Adapters.adapt(getElement(), IProject.class);
		ICProjectDescriptionManager pdMgr = CoreModel.getDefault().getProjectDescriptionManager();
		ICProjectDescription cProjDesc = pdMgr.getProjectDescription(project);

		// TODO: De-duplicate this and NewCcsdkProjectWizard.
		// TODO: Instead of setting environment variables as user make environment variable contribution
		setEnvVars(cProjDesc);
		updateProjectPathVariables(project);
		
		IManagedBuildInfo info = ManagedBuildManager.getBuildInfo(project);
		for (IConfiguration cfg: info.getManagedProject().getConfigurations()) {
			setToolchainOptions(cfg);
		}
		
		try {
			pdMgr.setProjectDescription(project, cProjDesc);
		} catch (CoreException e) {
			CcPlugin.log(e);
		}
		
		return true;
	}
	
	private void updateProjectPathVariables(IProject project) {
		try {
			project.getPathVariableManager().setURIValue("CCSDK_LOC", new File(projectOptionsComposite.getSdkLocation()).toURI());
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void setEnvVars(ICProjectDescription cProjDesc) {
		IEnvironmentVariableManager envMgr = CCorePlugin.getDefault().getBuildEnvironmentManager();
		IContributedEnvironment environment = envMgr.getContributedEnvironment();
		
		// Add variables to project configuration
		String sdkLocation = projectOptionsComposite.getSdkLocation();
		String boardName = projectOptionsComposite.getBoardName();
		String dbgPort = projectOptionsComposite.getDebugPort();
		String uartPort = projectOptionsComposite.getUartPort();
	
		for (ICConfigurationDescription config : cProjDesc.getConfigurations()) {
			environment.addVariable(CcConstants.ENV_SDK_HOME, sdkLocation, IEnvironmentVariable.ENVVAR_REPLACE,
					null, config);
			environment.addVariable(CcConstants.ENV_BOARD, boardName, IEnvironmentVariable.ENVVAR_REPLACE, null,
					config);
			environment.addVariable(CcConstants.ENV_DBG_PORT, dbgPort, IEnvironmentVariable.ENVVAR_REPLACE, null,
					config);
			environment.addVariable(CcConstants.ENV_UART_PORT, uartPort, IEnvironmentVariable.ENVVAR_REPLACE, null,
					config);
			// Set QUIET to 0 to get full commands from Make
			environment.addVariable(CcConstants.ENV_QUIET, "0", IEnvironmentVariable.ENVVAR_REPLACE, null, config);
		}
	}
	
	private void setToolchainOptions(IConfiguration config) {
		String sdkLocation = projectOptionsComposite.getSdkLocation();
		IToolChain toolChain = config.getToolChain();

		IOption option = toolChain.getOptionBySuperClassId(GNU_CROSS_TOOLCHAIN_OPTION_PREFIX);
		ManagedBuildManager.setOption(config, toolChain, option, TOOLCHAIN_PREFIX);

		String toolchainPath = sdkLocation + "/toolchain/" + TOOLCHAIN_TARGET + "/bin";
		option = toolChain.getOptionBySuperClassId(GNU_CROSS_TOOLCHAIN_OPTION_PATH);
		ManagedBuildManager.setOption(config, toolChain, option, toolchainPath);
	}
}
