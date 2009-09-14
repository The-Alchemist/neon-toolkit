/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.navigator.clazz;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchPart;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.gui.navigator.MTreeView;
import org.neontoolkit.gui.navigator.actions.AbstractSelectionBasedHandler;
import org.neontoolkit.refactor.GenericRefactoringExecutionStarter;

/* 
 * Created on: 18.05.2006
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Move concepts
 */
/**
 * Action for moving concepts
 */

public class MoveClazzHandler extends AbstractSelectionBasedHandler {

    /* (non-Javadoc)
     * @see org.neontoolkit.gui.navigator.actions.AbstractSelectionBasedHandler#executeWithSelection(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.IStructuredSelection)
     */
    @Override
    public Object executeWithSelection(IWorkbenchPart part, IStructuredSelection selection) throws ExecutionException {
        if (!(part instanceof MTreeView)) {
            return null;
        }
        MTreeView view = (MTreeView)part;
		try {
			if (selection != null) {
                TreeItem[] items = view.getTreeViewer().getTree().getSelection();
				ClazzTreeElement[] objects = new ClazzTreeElement[items.length];
				ClazzTreeElement[] parents = new ClazzTreeElement[items.length];
				for (int i=0; i<items.length; i++) {
					objects[i] = (ClazzTreeElement)items[i].getData();
					Object parentData = items[i].getParentItem().getData();
					parents[i] = parentData instanceof ClazzTreeElement ? (ClazzTreeElement)parentData : null;
				}
				GenericRefactoringExecutionStarter.startRefactoring(
				        view.getTreeViewer().getTree().getShell(), 
						"com.ontoprise.ontostudio.owl.gui.refactor.moveClazz",  //$NON-NLS-1$
						objects,
						parents);
			}
		} catch (Exception e) {
            new NeonToolkitExceptionHandler().handleException(e);
		}
		return null;
	}
}
