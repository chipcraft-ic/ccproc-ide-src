/*H*****************************************************************************
*
* Copyright (c) 2018 ChipCraft Sp. z o.o. All rights reserved
*
* This software is subject to the terms of the Eclipse Public License v2.0
* You must accept the terms of that agreement to use this software.
* A copy of the License is available at
* https://www.eclipse.org/legal/epl-2.0
*
* ******************************************************************************
* File Name : NewCcsdkProjectWizard.java
* Author    : Rafal Harabien
* ******************************************************************************
* $Date: 2019-03-19 12:27:38 +0100 (Tue, 19 Mar 2019) $
* $Revision: 397 $
*H*****************************************************************************/

package com.chipcraftic.eplugin.ui.wizard;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.cdt.core.CCProjectNature;
import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.envvar.IContributedEnvironment;
import org.eclipse.cdt.core.envvar.IEnvironmentVariable;
import org.eclipse.cdt.core.envvar.IEnvironmentVariableManager;
import org.eclipse.cdt.core.language.settings.providers.ILanguageSettingsProvider;
import org.eclipse.cdt.core.language.settings.providers.ILanguageSettingsProvidersKeeper;
import org.eclipse.cdt.core.language.settings.providers.LanguageSettingsManager;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.settings.model.CIncludePathEntry;
import org.eclipse.cdt.core.settings.model.CLibraryFileEntry;
import org.eclipse.cdt.core.settings.model.CMacroEntry;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICLanguageSetting;
import org.eclipse.cdt.core.settings.model.ICLanguageSettingEntry;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescriptionManager;
import org.eclipse.cdt.core.settings.model.ICSettingEntry;
import org.eclipse.cdt.core.settings.model.extension.CConfigurationData;
import org.eclipse.cdt.make.core.IMakeTarget;
import org.eclipse.cdt.make.core.IMakeTargetManager;
import org.eclipse.cdt.make.core.MakeCorePlugin;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IBuildObjectProperties;
import org.eclipse.cdt.managedbuilder.core.IBuilder;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.internal.core.Configuration;
import org.eclipse.cdt.managedbuilder.internal.core.ManagedProject;
import org.eclipse.cdt.managedbuilder.internal.core.ToolChain;
import org.eclipse.cdt.managedbuilder.language.settings.providers.AbstractBuildCommandParser;
import org.eclipse.cdt.managedbuilder.language.settings.providers.GCCBuildCommandParser;
import org.eclipse.cdt.managedbuilder.ui.wizards.NewMakeProjFromExisting;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.tools.templates.core.IGenerator;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import com.chipcraftic.eplugin.core.CcConstants;
import com.chipcraftic.eplugin.core.CcPlugin;
import com.chipcraftic.eplugin.core.ProjectNature;
import com.chipcraftic.eplugin.ui.wizard.model.ProjectGeneratorModel;
import com.chipcraftic.eplugin.ui.wizard.model.SdkOptionsModel;

/**
 * New CCSDK project wizard. Based on {@link NewMakeProjFromExisting} and
 * {@link BasicNewProjectResourceWizard}.
 * 
 * @author Rafał Harabień
 *
 */
public class NewCcsdkProjectWizard extends Wizard implements IImportWizard, INewWizard, IExecutableExtension {

	private static final String TOOLCHAIN_TARGET = "mips-cc-elf";
	private static final String TOOLCHAIN_PREFIX = TOOLCHAIN_TARGET + "-";

	private static final String GNU_CROSS_TOOLCHAIN_ID = "cdt.managedbuild.toolchain.gnu.cross.base";
	private static final String GNU_CROSS_TOOLCHAIN_OPTION_PREFIX = "cdt.managedbuild.option.gnu.cross.prefix";
	private static final String GNU_CROSS_TOOLCHAIN_OPTION_PATH = "cdt.managedbuild.option.gnu.cross.path";
	private static final String GCC_BUILD_COMMAND_PARSER = "org.eclipse.cdt.managedbuilder.core.GCCBuildCommandParser";
	private static final String MBS_LANG_SETTINGS_PROVIDER = "org.eclipse.cdt.managedbuilder.core.MBSLanguageSettingsProvider";

	private static final String CONFIG_NAME = "Default";

	private WizardNewProjectCreationPage mainPage;
	private CcsdkProjectOptionsPage configPage;
	private PeripherialsPage periphPage;
	private IConfigurationElement configElement;

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle("Create CCSDK Project...");
	}

	@Override
	public void addPages() {
		super.addPages();

		mainPage = new WizardNewProjectCreationPage("ccsdkNewProjectPage");
		mainPage.setTitle("CCSDK Project");
		mainPage.setDescription("Create a new CCSDK project");
		addPage(mainPage);

		configPage = new CcsdkProjectOptionsPage("ccsdkProjectConfigPage");
		configPage.setTitle("CCSDK Project");
		configPage.setDescription("Configure CCSDK project environment variables...");
		addPage(configPage);
		
		periphPage = new PeripherialsPage();
		addPage(periphPage);
	}

	@Override
	public boolean performFinish() {
		final String projectName = mainPage.getProjectName();
		final URI location = mainPage.useDefaults() ? null : mainPage.getLocationURI();
		final SdkOptionsModel projConfig = configPage.getSdkOptions();
		final boolean isCPP = configPage.isCppEnabled();
		final boolean managedBuildEnabled = configPage.isManagedBuildEnabled();

		configPage.saveLastValues();
		
		ProjectGeneratorModel generatorModel = new ProjectGeneratorModel();
		generatorModel.setPeripherials(periphPage.getModel());
		generatorModel.setProjectName(projectName);

		IRunnableWithProgress runnable = new WorkspaceModifyOperation() {
			@Override
			protected void execute(IProgressMonitor monitor)
					throws CoreException, InvocationTargetException, InterruptedException {
				// Create Project
				createProject(projectName, location, projConfig, isCPP, managedBuildEnabled, generatorModel, monitor);
			}
		};

		try {
			getContainer().run(true, true, runnable);
		} catch (InvocationTargetException e) {
			CcPlugin.log(e);
			return false;
		} catch (InterruptedException e) {
			CcPlugin.log(e);
			return false;
		}

		BasicNewProjectResourceWizard.updatePerspective(configElement);
		try {
			openSourceCode(projectName);
		} catch (CoreException e) {
			CcPlugin.log(e);
		}

		return true;
	}
	
	private void createProject(String projectName, URI location, SdkOptionsModel projConfig, boolean isCPP, 
			boolean managedBuildEnabled, ProjectGeneratorModel generatorModel, IProgressMonitor monitor) throws CoreException {
		
		SubMonitor subMonitor = SubMonitor.convert(monitor, "Creating CCSDK Project", 100);
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject project = workspace.getRoot().getProject(projectName);

		IProjectDescription description = workspace.newProjectDescription(projectName);
		description.setLocationURI(location);
		CCorePlugin.getDefault().createCDTProject(description, project, subMonitor.split(10));

		// Optionally C++ natures
		if (isCPP)
			CCProjectNature.addCCNature(project, subMonitor.split(10));
		else
			subMonitor.worked(10);

		// Refresh project to fix problems with creating resources in case of existing
		// project
		project.refreshLocal(IResource.DEPTH_INFINITE, subMonitor.split(10));

		// Set up build information
		ICProjectDescriptionManager pdMgr = CoreModel.getDefault().getProjectDescriptionManager();
		ICProjectDescription projDesc = pdMgr.createProjectDescription(project, false);
		IManagedBuildInfo buildInfo = ManagedBuildManager.createBuildInfo(project);

		// Create Managed Project
		// IProjectType projectType =
		// ManagedBuildManager.getProjectType("cdt.managedbuild.target.gnu.cross.exe");
		// ManagedProject mProj = (ManagedProject)
		// ManagedBuildManager.createManagedProject(project, projectType);
		ManagedProject mProj = new ManagedProject(projDesc);
		buildInfo.setManagedProject(mProj);
		subMonitor.worked(10);

		// Use Cross toolchain for now
		IToolChain toolChain = ManagedBuildManager.getExtensionToolChain(GNU_CROSS_TOOLCHAIN_ID);
		if (toolChain == null) {
			throw new IllegalStateException("Cross toolchain not found");
		}
		
		// Create configuration
		String configId = ManagedBuildManager.calculateChildId(toolChain.getId(), null); // $NON-NLS-1$
		IConfiguration config = new Configuration(mProj, (ToolChain) toolChain, configId, CONFIG_NAME);
		try {
			config.setBuildArtefactType(ManagedBuildManager.BUILD_ARTEFACT_TYPE_PROPERTY_EXE);
		} catch (BuildException e) {
			CcPlugin.log(e);
		}
		config.setArtifactName("${ProjName}");
		IBuildObjectProperties props = config.getBuildProperties();
		props.setProperty(ManagedBuildManager.BUILD_TYPE_PROPERTY_ID, ManagedBuildManager.BUILD_TYPE_PROPERTY_RELEASE);
		final String artifactNameVar = "${BuildArtifactFileName}";
		String postBuildStep = CcConstants.TOOLCHAIN_TRIPLET + "-size " + artifactNameVar + " && " +
				CcConstants.TOOLCHAIN_TRIPLET + "-objcopy -O srec " + artifactNameVar + " " + artifactNameVar + ".srec";
		config.setPostbuildStep(postBuildStep);
		//config.setManagedBuildOn(false);

		// Configure builder
		IBuilder builder = config.getEditableBuilder();
		builder.setManagedBuildOn(managedBuildEnabled);

		// Configure cross toolchain
		setToolchainOptions(config, projConfig.getSdkLocation());

		// Create configuration description
		CConfigurationData data = config.getConfigurationData();
		ICConfigurationDescription cConfDesc = projDesc
				.createConfiguration(ManagedBuildManager.CFG_DATA_PROVIDER_ID, data);
		subMonitor.worked(10);

		// Setup language providers
		setupLanguageSettings(cConfDesc, toolChain);
		subMonitor.worked(10);

		// Set environment variables used by Makefile
		setProjectEnvVariables(projDesc, projConfig);

		// Save CDT Project Description
		pdMgr.setProjectDescription(project, projDesc);
		subMonitor.worked(10);

		// Add CCSDK nature
		addCcsdkNature(project, subMonitor.split(10));

		// Add sample code if project is empty
		addSampleCode(project, generatorModel);

		// Add custom make targets
		addMakeTargets(project);
		
		// Link common resources
		addPathVariables(project, projConfig);
		linkCommonResources(project, generatorModel);
		
		subMonitor.worked(10);
		monitor.done();
	}
	
	private void setupLanguageSettings(ICConfigurationDescription cConfDesc, IToolChain toolChain) {
		// Add GCC Build Command Parser language settings provider
		ILanguageSettingsProvidersKeeper providersKeeper = (ILanguageSettingsProvidersKeeper) cConfDesc;
		List<String> providerIdsList = new ArrayList<>(Arrays.asList(
				toolChain.getDefaultLanguageSettingsProviderIds().split(";")));
		providerIdsList.add(MBS_LANG_SETTINGS_PROVIDER);
		String[] providerIds = providerIdsList.toArray(new String[0]);
		List<ILanguageSettingsProvider> providers = LanguageSettingsManager
				.createLanguageSettingsProviders(providerIds);
		providersKeeper.setDefaultLanguageSettingsProvidersIds(providerIds);
		providersKeeper.setLanguageSettingProviders(providers);
		for (ILanguageSettingsProvider provider : providersKeeper.getLanguageSettingProviders()) {
			if (provider.getId().equals(GCC_BUILD_COMMAND_PARSER)) {
				// Configure GCC command parser language setting provider
				GCCBuildCommandParser gccParser = (GCCBuildCommandParser) provider;
				gccParser.setCompilerPattern(
						"\"?.*" + TOOLCHAIN_PREFIX + "((g?cc)|([gc]\\+\\+)|(clang))\"?");
				gccParser.setResourceScope(AbstractBuildCommandParser.ResourceScope.PROJECT);
			}
		}
		// Update language settings for the project root folder
		ICLanguageSetting[] langSettings = cConfDesc.getRootFolderDescription().getLanguageSettings();
		for (ICLanguageSetting langSetting: langSettings) {
			//if ("org.eclipse.cdt.core.gcc".equals(langSetting.getLanguageId())) {
				// Update include paths
				List<ICLanguageSettingEntry> entries = langSetting.getSettingEntriesList(ICSettingEntry.INCLUDE_PATH);
				entries.add(new CIncludePathEntry("${CCSDK_HOME}/include", ICSettingEntry.NONE));
				entries.add(new CIncludePathEntry("${CCSDK_HOME}/common/include", ICSettingEntry.NONE));
				entries.add(new CIncludePathEntry("${CCSDK_HOME}/boards/${CCSDK_BOARD}/include", ICSettingEntry.NONE));
				entries.add(new CIncludePathEntry("${CCSDK_HOME}/drivers/include", ICSettingEntry.NONE));
				langSetting.setSettingEntries(ICSettingEntry.INCLUDE_PATH, entries);
				
				// Add preprocessor definitions
				entries = langSetting.getSettingEntriesList(ICSettingEntry.MACRO);
				entries.add(new CMacroEntry("BOARD", "${CCSDK_BOARD}", ICSettingEntry.NONE));
				entries.add(new CMacroEntry("BOARD_ML605", "", ICSettingEntry.NONE));
				langSetting.setSettingEntries(ICSettingEntry.MACRO, entries);
				
				// Add libraries
				entries = langSetting.getSettingEntriesList(ICSettingEntry.LIBRARY_FILE);
				entries.add(new CLibraryFileEntry("c", ICSettingEntry.NONE));
				langSetting.setSettingEntries(ICSettingEntry.LIBRARY_FILE, entries);
			//}
		}
	}
	
	private void addSampleCode(IProject project, ProjectGeneratorModel generatorModel)
			throws CoreException {
		// Generate source code only if Makefile does not exist
		IFile makefile = project.getFile("Makefile");
		if (!makefile.exists()) {
			IGenerator generator = getGenerator(project, generatorModel);
			generator.generate(new NullProgressMonitor());
		}
	}

	protected IGenerator getGenerator(IProject project, ProjectGeneratorModel model) {
		String manifest = "templates/CcsdkProject/manifest.xml"; //$NON-NLS-1$
		CcProjectGenerator generator = new CcProjectGenerator(manifest, model);
		return generator;
	}
	
	private void setToolchainOptions(IConfiguration config, String sdkLocation) {
		IToolChain toolChain = config.getToolChain();

		// Toolchain prefix
		IOption option = toolChain.getOptionBySuperClassId(GNU_CROSS_TOOLCHAIN_OPTION_PREFIX);
		ManagedBuildManager.setOption(config, toolChain, option, TOOLCHAIN_PREFIX);

		// Toolchain path
		final String toolchainPath = sdkLocation + "/toolchain/" + TOOLCHAIN_TARGET + "/bin";
		option = toolChain.getOptionBySuperClassId(GNU_CROSS_TOOLCHAIN_OPTION_PATH);
		ManagedBuildManager.setOption(config, toolChain, option, toolchainPath);
		
		// Compiler flags
		ITool tool = toolChain.getToolsBySuperClassId("cdt.managedbuild.tool.gnu.c.compiler")[0];
		option = tool.getOptionBySuperClassId("gnu.c.compiler.option.optimization.level");
		ManagedBuildManager.setOption(config, tool, option, "gnu.c.optimization.level.size");
		option = tool.getOptionBySuperClassId("gnu.c.compiler.option.optimization.flags");
		ManagedBuildManager.setOption(config, tool, option, "-fdata-sections -ffunction-sections");
		option = tool.getOptionBySuperClassId("gnu.c.compiler.option.debugging.level");
		ManagedBuildManager.setOption(config, tool, option, "gnu.c.debugging.level.max");
		
		// Assembler flags and command
		ITool assemblerTool = toolChain.getToolsBySuperClassId("cdt.managedbuild.tool.gnu.assembler")[0];
		option = assemblerTool.getOptionBySuperClassId("gnu.both.asm.option.flags");
		ManagedBuildManager.setOption(config, assemblerTool, option, "-c");
		ManagedBuildManager.setToolCommand(config, assemblerTool, "gcc");
		
		// Linker flags
		ITool linkerTool = toolChain.getToolsBySuperClassId("cdt.managedbuild.tool.gnu.c.linker")[0];
		option = linkerTool.getOptionBySuperClassId("gnu.c.link.option.ldflags");
		ManagedBuildManager.setOption(config, linkerTool, option, "-T\"${CCSDK_HOME}/linker/ccproc.ld\" -Wl,--gc-sections");
	}
	
	private void setProjectEnvVariables(ICProjectDescription projectDescription, SdkOptionsModel projConfig)
			throws CoreException {
		// See:
		// https://stackoverflow.com/questions/6186754/eclipse-cdt-how-to-save-project-wide-environment-variables-programmatically-pe

		final IEnvironmentVariableManager envMgr = CCorePlugin.getDefault().getBuildEnvironmentManager();

		IContributedEnvironment environment = envMgr.getContributedEnvironment();

		// Add variable to project configuration
		String sdkLocation = projConfig.getSdkLocation();
		String boardName = projConfig.getBoardName();
		String dbgPort = projConfig.getDebugPort();
		String uartPort = projConfig.getUartPort();

		for (ICConfigurationDescription config : projectDescription.getConfigurations()) {
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

	private void addCcsdkNature(IProject project, IProgressMonitor monitor) throws CoreException {
		IProjectDescription projectDescription = project.getDescription();
		String[] natureIds = projectDescription.getNatureIds();
		if (!Arrays.asList(natureIds).contains(ProjectNature.NATURE_ID)) {
			natureIds = Arrays.copyOf(natureIds, natureIds.length + 1);
			natureIds[natureIds.length - 1] = ProjectNature.NATURE_ID;
			projectDescription.setNatureIds(natureIds);
			project.setDescription(projectDescription, monitor);
		}
	}

	private void addMakeTarget(IProject project, String targetName, String targetCode) throws CoreException {
		IMakeTargetManager targetManager = MakeCorePlugin.getDefault().getTargetManager();
		String[] targetBuilders = targetManager.getTargetBuilders(project);
		if (targetBuilders.length == 0) {
			throw new IllegalStateException("no target builders found");
		}
		String targetBuilderID = targetBuilders[0];

		IMakeTarget target = targetManager.createTarget(project, targetName, targetBuilderID);
		target.setBuildAttribute(IMakeTarget.BUILD_TARGET, targetCode);
		target.setUseDefaultBuildCmd(true);
		targetManager.addTarget(project, target);
	}

	private void addMakeTargets(IProject project) throws CoreException {
		addMakeTarget(project, "Write RAM Memory", "ram-write");
		addMakeTarget(project, "Write Flash Memory", "flash-write");
		addMakeTarget(project, "Reset MCU", "reset");
	}

	@Override
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
			throws CoreException {
		configElement = config;
	}
	
	private void addPathVariables(IProject project, SdkOptionsModel projConfig) {
		try {
			project.getPathVariableManager().setURIValue("CCSDK_LOC",
					new File(projConfig.getSdkLocation()).toURI());
			project.getPathVariableManager().setURIValue("CCSDK_BOARD_LOC",
					new File(projConfig.getSdkLocation() + "/boards/" + projConfig.getBoardName()).toURI());
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void linkCommonResources(IProject project, ProjectGeneratorModel generatorModel) throws CoreException {
		// Create ccsdk-common virtual folder
		IFolder commonFolder = project.getFolder("ccsdk-common");
		if (!commonFolder.exists()) {
			commonFolder.create(IResource.VIRTUAL, true, new NullProgressMonitor());
		}
		// Add links to used common source files in CCSDK
		commonFolder.getFile("startup.S").createLink(new Path("${CCSDK_LOC}/common/startup.S"), IResource.NONE, new NullProgressMonitor());
		commonFolder.getFile("syscalls.c").createLink(new Path("${CCSDK_LOC}/common/syscalls.c"), IResource.NONE, new NullProgressMonitor());
		commonFolder.getFile("board.c").createLink(new Path("${CCSDK_BOARD_LOC}/board.c"), IResource.NONE, new NullProgressMonitor());
		commonFolder.getFile("uart.c").createLink(new Path("${CCSDK_LOC}/drivers/uart.c"), IResource.NONE, new NullProgressMonitor());
		if (!generatorModel.getPeripherials().getSpiList().isEmpty()) {
			commonFolder.getFile("spi.c").createLink(new Path("${CCSDK_LOC}/drivers/spi.c"), IResource.NONE, new NullProgressMonitor());
		}
	}
	
	private void openSourceCode(String projectName) throws CoreException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject project = workspace.getRoot().getProject(projectName);
		// Open main.c after generation
		IFile mainFile = project.getFile("main.c");
		if (mainFile.exists()) {
			IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), mainFile);
		}
	}
}
