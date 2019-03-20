package com.chipcraftic.eplugin.ui.wizard;

import org.eclipse.swt.widgets.Composite;

public abstract class AbstractPeriphComposite<T> extends Composite {

	protected T model;
	
	public AbstractPeriphComposite(Composite parent, int style) {
		super(parent, style);
	}

	public abstract void updateModel();
	protected abstract void updateView();
	
	protected int findIdInIdDescList(String id, String[] idDescList) {
		for (int i = 0; i < idDescList.length; ++i) {
			if (idDescList[i].startsWith(id)) {
				return i;
			}
		}
		return 0;
	}
	
	public T getModel() {
		return model;
	}

	public void setModel(T model) {
		this.model = model;
		updateView();
	}
}
