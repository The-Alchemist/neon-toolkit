/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.gui.navigator.ontology;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.neontoolkit.gui.Messages;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.gui.navigator.MTreeView;
import org.neontoolkit.gui.navigator.elements.IOntologyElement;
import org.neontoolkit.gui.navigator.elements.IProjectElement;
import org.neontoolkit.gui.properties.ProgressMonitorWithExceptionDialog;

/* 
 * Created on: 01.07.2005
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Action, Refactor
 */
/**
 * Action for saving an ontology.
 */
public class SaveOntologyAction implements IObjectActionDelegate {
	private MTreeView _view;
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		if (targetPart instanceof MTreeView) {
			_view = (MTreeView) targetPart;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
        if (_view != null) {
            IStructuredSelection selectedItems = (IStructuredSelection) _view.getTreeViewer().getSelection();
            Object selectedElement = selectedItems.getFirstElement();
            if (selectedElement instanceof IOntologyElement && selectedElement instanceof IProjectElement) {
                try {
                    IOntologyElement ontologyElem = (IOntologyElement) selectedElement;
                	IProjectElement projectElem = (IProjectElement) selectedElement;
	                ProgressMonitorWithExceptionDialog progdialog = new ProgressMonitorWithExceptionDialog(_view.getSite().getShell());
	                SaveOntologyRunnableWithProgress runnable = new SaveOntologyRunnableWithProgress(projectElem.getProjectName(), ontologyElem.getOntologyUri());
	                progdialog.runWithException(true, false, runnable);
                } catch (Exception e) {
                	new NeonToolkitExceptionHandler().handleException(Messages.SaveOntologyAction_1, e, new Shell()); 
                }
            }
        }
	
	}

	/**
     * @param projectName
     * @param ontologyUri
     * @return
     */
    private boolean isRemote(String projectName, String ontologyUri) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
