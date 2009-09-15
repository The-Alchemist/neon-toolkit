/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.navigator.ontology;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.gui.navigator.MTreeView;
import org.neontoolkit.gui.navigator.actions.AbstractSelectionBasedHandler;
import org.neontoolkit.gui.properties.ProgressMonitorWithExceptionDialog;
import org.neontoolkit.gui.util.URIUtils;

import com.ontoprise.ontostudio.owl.gui.Messages;

/* 
 * Created on: 01.07.2005
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Action, Refactor
 */
/**
 * Action for the renaming of a module.
 */
public class RenameOntologyHandler extends AbstractSelectionBasedHandler {

    /* (non-Javadoc)
     * @see org.neontoolkit.gui.navigator.actions.AbstractSelectionBasedHandler#executeWithSelection(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.IStructuredSelection)
     */
    @Override
    public Object executeWithSelection(IWorkbenchPart part, IStructuredSelection selection) throws ExecutionException {
        if (!(part instanceof MTreeView)) {
            return null;
        }
        MTreeView view = (MTreeView)part;
        if (view != null) {
            Object selectedElement = selection.getFirstElement();
//            Object parent = view.getTreeViewer().getTree().getSelection()[0].getParentItem().getData();
            if (selectedElement instanceof OntologyTreeElement) {
                try {
                    OntologyTreeElement elem = (OntologyTreeElement) selectedElement;
                    InputDialog dialog = new InputDialog(new Shell(), Messages.RenameModuleAction_1, 
                            Messages.RenameModuleAction_2, elem.getOntologyUri(), 
                            URIUtils.getInputValidator()); 
                    dialog.open();
                    String res = dialog.getValue();

                    if (res != null && !res.equals(elem.getOntologyUri())) {
                        // rename
                        ProgressMonitorWithExceptionDialog progdialog = new ProgressMonitorWithExceptionDialog(view.getSite().getShell());
                        RenameOntologyRunnableWithProgress runnable = new RenameOntologyRunnableWithProgress(elem.getProjectName(), elem.getOntologyUri(), res);
                        progdialog.runWithException(false, false, runnable);
                    }
                } catch (Exception e) {
                    new NeonToolkitExceptionHandler().handleException(Messages.RenameModuleAction_4, e, new Shell()); 
                }
            }
        }
        return null;
    }
}
