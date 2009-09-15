/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.gui.internal.navigator.action;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbenchPart;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.gui.navigator.MTreeView;
import org.neontoolkit.gui.navigator.actions.AbstractSelectionBasedHandler;
import org.neontoolkit.gui.navigator.project.ProjectTreeElement;

/* 
 * Created on: 18.12.2004
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Action
 */
/**
 * Action for refreshing the tree.
 */
public class RefreshHandler extends AbstractSelectionBasedHandler {
	

    /* (non-Javadoc)
     * @see org.neontoolkit.gui.navigator.actions.AbstractSelectionBasedHandler#executeWithSelection(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.IStructuredSelection)
     */
    @Override
    public Object executeWithSelection(IWorkbenchPart part, IStructuredSelection selection) throws ExecutionException {
        if (!(part instanceof MTreeView)) {
            return null;
        }
        TreeViewer treeViewer = ((MTreeView)part).getTreeViewer();
		if (treeViewer != null) {
			IStructuredSelection selectedItems =
				(IStructuredSelection) treeViewer.getSelection();
			Object selectedElement = selectedItems.getFirstElement();
			
			if(selectedElement instanceof ProjectTreeElement) {
				IProject project = NeOnCorePlugin.getDefault().getProject(selectedElement.toString());
				try {
					project.refreshLocal(IResource.DEPTH_INFINITE, null);
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
			
			Object[] elems = treeViewer.getVisibleExpandedElements();
			treeViewer.refresh(selectedElement);
			treeViewer.setExpandedElements(elems);
		}
		return null;
	}
}
