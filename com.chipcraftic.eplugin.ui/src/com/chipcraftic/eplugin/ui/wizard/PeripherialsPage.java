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
* File Name : CcsdkProjectOptionsPage.java
* Author    : Rafal Harabien
* ******************************************************************************
* $Date: 2019-03-19 12:27:38 +0100 (Tue, 19 Mar 2019) $
* $Revision: 397 $
*H*****************************************************************************/

package com.chipcraftic.eplugin.ui.wizard;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import com.chipcraftic.eplugin.ui.wizard.model.PeripherialsModel;
import com.chipcraftic.eplugin.ui.wizard.model.SpiConfig;
import com.chipcraftic.eplugin.ui.wizard.model.UartConfig;

public class PeripherialsPage extends WizardPage {

	private static final int SPI_COUNT = 2;
	private static final int UART_COUNT = 2;
	
	private Tree fTree;
	private TreeItem fSpiRootItem;
	private TreeItem fUartRootItem;
	private AbstractPeriphComposite<?> fPeriphOptionsComp;
	private ScrolledComposite fPeriphOptionsContainer;
	private PeripherialsModel pageModel;
	private IPeriphTreeItemHandler selectedHandler;
	
	/**
	 * Create the wizard.
	 */
	public PeripherialsPage() {
		super("peripherialsPage");
		setTitle("Peripherials");
		setDescription("Configure peripherials for code generation");
		pageModel = new PeripherialsModel();
	}
	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);

		setControl(container);
		container.setLayout(new GridLayout(2, false));
		
		fTree = new Tree(container, SWT.BORDER|SWT.CHECK);
		fTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		TreeColumn column = new TreeColumn(fTree, SWT.NONE);
		
		column.setWidth(100);
		column.setText("Peripherial");
		
		fPeriphOptionsContainer = new ScrolledComposite(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		fPeriphOptionsContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		fPeriphOptionsContainer.setExpandHorizontal(true);
		fPeriphOptionsContainer.setExpandVertical(true);
		
		fTree.addSelectionListener(new PeriphTreeSelectionListener());
		
		populateTree();
	}
	
	private void populateTree() {
		fSpiRootItem = new TreeItem(fTree, SWT.NONE);
		fSpiRootItem.setText("SPI");
		fSpiRootItem.setData(new PeriphGroupHandler(fSpiRootItem));
		for (int i = 0; i < SPI_COUNT; ++i) {
			TreeItem spiItem = new TreeItem(fSpiRootItem, SWT.NONE);
			spiItem.setText("SPI " + i);
			SpiConfig itemModel = new SpiConfig(i);
			spiItem.setData(new PeriphHandler<>(itemModel, pageModel.getSpiList(), () -> new SpiConfigComposite(fPeriphOptionsContainer, SWT.NONE)));
		}
		fSpiRootItem.setExpanded(true);
		
		fUartRootItem = new TreeItem(fTree, SWT.NONE);
		fUartRootItem.setText("UART");
		for (int i = 0; i < UART_COUNT; ++i) {
			TreeItem treeItem = new TreeItem(fUartRootItem, SWT.NONE);
			treeItem.setText("UART " + i);
			UartConfig itemModel = new UartConfig(i);
			treeItem.setData(new PeriphHandler<>(itemModel, pageModel.getUartList(), () -> new UartConfigComposite(fPeriphOptionsContainer, SWT.NONE)));
		}
		fUartRootItem.setExpanded(true);
	}
	
	public PeripherialsModel getModel() {
		if (selectedHandler != null) {
			selectedHandler.handleUnselected();
			selectedHandler = null;
		}
		
		pageModel.getSpiList().clear();
		pageModel.getUartList().clear();
		
		for (TreeItem rootItem: fTree.getItems()) {
			for (TreeItem item: rootItem.getItems()) {
				if (item.getChecked()) {
					IPeriphTreeItemHandler handler = (IPeriphTreeItemHandler) item.getData();
					handler.appendToModelList();
				}
			}
		}
		
		return pageModel;
	}
	
	interface IPeriphTreeItemHandler {
		Composite handleSelected(Composite parent);
		default void handleUnselected() {}
		default void appendToModelList() {}
	}
	
	class PeriphGroupHandler implements IPeriphTreeItemHandler {
		
		private TreeItem treeItem;
		
		PeriphGroupHandler(TreeItem treeItem) {
			this.treeItem = treeItem;
		}
		
		@Override
		public Composite handleSelected(Composite parent) {
			treeItem.setChecked(false);
			return null;
		}
	}
	
	class PeriphHandler<T> implements IPeriphTreeItemHandler {
		private T model;
		private Collection<T> modelList;
		private Supplier<AbstractPeriphComposite<T>> viewSupplier;
		private AbstractPeriphComposite<T> view;
		
		PeriphHandler(T model, List<T> modelList, Supplier<AbstractPeriphComposite<T>> viewSupplier) {
			this.model = model;
			this.modelList = modelList;
			this.viewSupplier = viewSupplier;
		}
		
		@Override
		public Composite handleSelected(Composite parent) {
			view = viewSupplier.get();
			view.setModel(model);
			return view;
		}
		
		@Override
		public void handleUnselected() {
			view.updateModel();
		}
		
		@Override
		public void appendToModelList() {
			if (!modelList.contains(model)) {
				modelList.add(model);
			}
		}
	}
	
	class PeriphTreeSelectionListener implements SelectionListener {
		@Override
		public void widgetSelected(SelectionEvent e) {
			// check if anything is selected
			if (fTree.getSelection().length == 0) {
				return;
			}
			
			// get selected item handler
			TreeItem selectedItem = fTree.getSelection()[0];
			IPeriphTreeItemHandler handler = (IPeriphTreeItemHandler) selectedItem.getData();
			if (selectedHandler == handler) {
				// the same handler - nothing to do
				return;
			}
			
			// notify old handler about switching
			if (selectedHandler != null) {
				selectedHandler.handleUnselected();
			}
			selectedHandler = handler;
			
			// switch right panel content
			Control newContent = handler.handleSelected(fPeriphOptionsContainer);
			Control oldContent = fPeriphOptionsContainer.getContent();
			fPeriphOptionsContainer.setContent(newContent);
			fPeriphOptionsContainer.setMinSize(fPeriphOptionsComp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			
			if (oldContent != null) {
				oldContent.dispose();
			}
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			// nothing to do
		}
	}
}
