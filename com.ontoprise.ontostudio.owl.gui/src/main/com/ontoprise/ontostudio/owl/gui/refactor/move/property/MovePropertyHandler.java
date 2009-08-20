/*****************************************************************************
 * Copyright (c) 2007 ontoprise GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU General Public License (GPL)
 * which accompanies this distribution, and is available at
 * http://www.ontoprise.de/legal/gpl.html
 *****************************************************************************/

package com.ontoprise.ontostudio.owl.gui.refactor.move.property;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchPart;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.gui.navigator.MTreeView;
import org.neontoolkit.gui.navigator.actions.AbstractSelectionBasedHandler;
import org.neontoolkit.refactor.GenericRefactoringExecutionStarter;

import com.ontoprise.ontostudio.owl.gui.navigator.property.PropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.dataProperty.DataPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.objectProperty.ObjectPropertyTreeElement;

/* 
 * Created on: 18.05.2006
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Move concepts
 */
/**
 * Action for moving concepts
 */
public class MovePropertyHandler extends AbstractSelectionBasedHandler {

    /*
     * (non-Javadoc)
     * 
     * @see org.neontoolkit.gui.navigator.actions.AbstractSelectionBasedHandler#executeWithSelection(org.eclipse.ui.IWorkbenchPart,
     * org.eclipse.jface.viewers.IStructuredSelection)
     */
    @Override
    public Object executeWithSelection(IWorkbenchPart part, IStructuredSelection selection) throws ExecutionException {
        if (!(part instanceof MTreeView)) {
            return null;
        }
        MTreeView view = (MTreeView) part;
        try {
            if (selection != null) {
                TreeItem[] items = view.getTreeViewer().getTree().getSelection();
                PropertyTreeElement[] objects = new PropertyTreeElement[items.length];
                PropertyTreeElement[] parents = new PropertyTreeElement[items.length];
                for (int i = 0; i < items.length; i++) {
                    objects[i] = (PropertyTreeElement) items[i].getData();
                    Object parentData = items[i].getParentItem().getData();
                    parents[i] = parentData instanceof PropertyTreeElement ? (PropertyTreeElement) parentData : null;
                }
                if (objects.length > 0 && objects[0] instanceof ObjectPropertyTreeElement) {
                    GenericRefactoringExecutionStarter.startRefactoring(view.getTreeViewer().getTree().getShell(), "com.ontoprise.ontostudio.owl.gui.refactor.moveObjectProperty", //$NON-NLS-1$
                            objects,
                            parents);
                } else if (objects.length > 0 && objects[0] instanceof DataPropertyTreeElement) {
                    GenericRefactoringExecutionStarter.startRefactoring(view.getTreeViewer().getTree().getShell(), "com.ontoprise.ontostudio.owl.gui.refactor.moveDataProperty", //$NON-NLS-1$
                            objects,
                            parents);
                }
            }
        } catch (Exception e) {
            new NeonToolkitExceptionHandler().handleException(e);
        }
        return null;
    }
}
