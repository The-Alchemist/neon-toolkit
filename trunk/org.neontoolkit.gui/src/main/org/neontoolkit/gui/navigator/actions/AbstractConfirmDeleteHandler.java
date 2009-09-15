/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.gui.navigator.actions;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchPart;
import org.neontoolkit.gui.navigator.CheckableConfirmDialog;
import org.neontoolkit.gui.navigator.MTreeView;

/**
 * @author diwe
 *
 */
public abstract class AbstractConfirmDeleteHandler extends AbstractDeleteHandler {

    private boolean _checkState = false;

    /* (non-Javadoc)
     * @see org.neontoolkit.gui.navigator.actions.AbstractDeleteHandler#executeWithSelection(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.IStructuredSelection)
     */
    @Override
    public Object executeWithSelection(IWorkbenchPart part, IStructuredSelection selection) throws ExecutionException {
        if (!(part instanceof MTreeView)) {
            return null;
        }
        TreeViewer treeViewer = ((MTreeView)part).getTreeViewer();
        _shell = treeViewer.getTree().getShell();
        _selection = (IStructuredSelection)treeViewer.getSelection();
        if (treeViewer != null) {
            CheckableConfirmDialog dialog = new CheckableConfirmDialog(
                    getShell(),
                    "Confirm deletion", //$NON-NLS-1$
                    getCheckBoxText() != null);
            dialog.setDialogMessage(getDialogMessage());
            dialog.setCheckboxText(getCheckBoxText());
            int res = dialog.open();
            _checkState = res != Window.OK;
            if (res != Window.CANCEL) {
                super.executeWithSelection(part, selection);
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see com.ontoprise.ontostudio.gui.navigator.actions.AbstractDeleteAction#doDelete(java.lang.Object[], java.lang.Object[])
     */
    @Override
    public boolean doDelete(Object[] items, Object[] parentItems) {
        return doDelete(items, parentItems, _checkState);
    }
    
    /**
     * This method is invoked, if the user confirms the delete operation. The seleted elements and 
     * their parents are passed and the state of the checkbox. 
     * @param items
     * @param parentItems
     * @return
     */
    public abstract boolean doDelete(Object[] items, Object[] parentItems, boolean checked);
    
    /**
     * Should return the message of the delete dialog.
     * @return
     */
    public abstract String getDialogMessage();
    
    /**
     * Should return the text of the checkbox (e.g. subelements should be deleted, too). If null is
     * returned, no checkbox is shown at all.
     * @return
     */
    public abstract String getCheckBoxText();
}
