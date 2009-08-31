/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 11.12.2008
 * Created by: diwe
 ******************************************************************************/
package org.neontoolkit.gui.navigator.actions;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchPart;
import org.neontoolkit.gui.navigator.MTreeView;

/**
 * @author diwe
 * 
 */
public abstract class AbstractDeleteHandler extends AbstractSelectionBasedHandler {

    protected Shell _shell;
    protected IStructuredSelection _selection;

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
        final TreeViewer treeViewer = ((MTreeView) part).getTreeViewer();
        _shell = treeViewer.getTree().getShell();
        _selection = (IStructuredSelection) treeViewer.getSelection();
        if (treeViewer != null) {
            BusyIndicator.showWhile(treeViewer.getTree().getDisplay(), new Runnable() {
                /*
                 * (non-Javadoc)
                 * 
                 * @see java.lang.Runnable#run()
                 */
                public void run() {
                    TreeItem[] selectedItems = treeViewer.getTree().getSelection();
                    final Object[] items = new Object[selectedItems.length];
                    final Object[] parentItems = new Object[selectedItems.length];
                    for (int i = 0; i < selectedItems.length; i++) {
                        items[i] = selectedItems[i].getData();
                        parentItems[i] = selectedItems[i].getParentItem() == null ? null : selectedItems[i].getParentItem().getData();
                    }
                    BusyIndicator.showWhile(_shell.getDisplay(), new Runnable() {
                        public void run() {
                            boolean deleted = doDelete(items, parentItems);
                            if (deleted && parentItems != null && parentItems.length > 0) {
                                final StructuredSelection newSelection = parentItems[0] != null ? new StructuredSelection(parentItems[0]) : new StructuredSelection();
                                treeViewer.getTree().getDisplay().syncExec(new Runnable() {
                                    public void run() {
                                        treeViewer.setSelection(newSelection);
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }
        return null;
    }

    protected Shell getShell() {
        return _shell;
    }

    protected IStructuredSelection getSelection() {
        return _selection;
    }

    public abstract boolean doDelete(Object[] items, Object[] parentItems);
}
