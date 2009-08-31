/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.gui.navigator.ontology;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
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

import com.sun.org.apache.xml.internal.utils.URI;
import com.sun.org.apache.xml.internal.utils.URI.MalformedURIException;

/* 
 * Created on: 01.07.2005
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Action, Refactor
 */
/**
 * Action for the renaming of an ontology.
 */
public class RenameOntologyAction implements IObjectActionDelegate {
    private IInputValidator _inputValidator = new IInputValidator() {
        public String isValid(String newText) {
            try {
                new URI(newText);
                return null;
            } catch (MalformedURIException e) {
                return e.getLocalizedMessage();
            }
        }
    };

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
	                InputDialog dialog = new InputDialog(new Shell(), Messages.RenameOntologyAction_1, Messages.RenameOntologyAction_2, ontologyElem.getOntologyUri(), _inputValidator); 
	                dialog.open();
	                String newOntologyUri = dialog.getValue();
	                if (newOntologyUri != null && !newOntologyUri.equals(ontologyElem.getOntologyUri())) {
	                    //rename
		                ProgressMonitorWithExceptionDialog progdialog = new ProgressMonitorWithExceptionDialog(_view.getSite().getShell());
		                RenameOntologyRunnableWithProgress runnable = new RenameOntologyRunnableWithProgress(projectElem.getProjectName(), ontologyElem.getOntologyUri(), newOntologyUri);
		                progdialog.runWithException(true, false, runnable);
	                }
                } catch (Exception e) {
                	new NeonToolkitExceptionHandler().handleException(Messages.RenameOntologyAction_3, e, new Shell()); 
                }
            }
        }
	
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
