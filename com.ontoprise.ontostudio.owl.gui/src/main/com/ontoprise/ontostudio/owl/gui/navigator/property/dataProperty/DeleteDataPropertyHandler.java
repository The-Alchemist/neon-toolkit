/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.navigator.property.dataProperty;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.PlatformUI;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.gui.navigator.actions.AbstractDeleteHandler;
import org.neontoolkit.refactor.GenericRefactoringExecutionStarter;


/**
 * Action to delete concepts in the tree.
 */
public class DeleteDataPropertyHandler extends AbstractDeleteHandler {
	
	/* (non-Javadoc)
	 * @see com.ontoprise.ontostudio.gui.navigator.actions.AbstractDeleteAction#doDelete(java.lang.Object[], java.lang.Object[])
	 */
	@Override
    public boolean doDelete(Object[] items, Object[] parentItems) {
		assert items.length == parentItems.length;
        int result = 0;
		try {
			DataPropertyTreeElement[][] res = getDataProperties(items, parentItems);
			result = GenericRefactoringExecutionStarter.startRefactoring(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
					"com.ontoprise.ontostudio.owl.gui.refactor.deleteDataProperty",  //$NON-NLS-1$
					res[0], 
					res[1]); 
		} catch (CoreException ce) {
			new NeonToolkitExceptionHandler().handleException(ce);
			return false;
		}
        return result == IDialogConstants.CANCEL_ID ? false : true;
	}
	
	private DataPropertyTreeElement[][] getDataProperties(Object[] items, Object[] parentItems) {
		List<DataPropertyTreeElement> clazzes = new ArrayList<DataPropertyTreeElement>();
		List<DataPropertyTreeElement> parentClazzes = new ArrayList<DataPropertyTreeElement>();
		for (int i=0; i<items.length; i++) {
			if (items[i] instanceof DataPropertyTreeElement) {
				clazzes.add((DataPropertyTreeElement)items[i]);
				parentClazzes.add(parentItems[i] instanceof DataPropertyTreeElement ? (DataPropertyTreeElement)parentItems[i] : null);
			}
		}
		DataPropertyTreeElement[][] res = new DataPropertyTreeElement[2][clazzes.size()];
		clazzes.toArray(res[0]);
		parentClazzes.toArray(res[1]);
		return res;
	}
}
