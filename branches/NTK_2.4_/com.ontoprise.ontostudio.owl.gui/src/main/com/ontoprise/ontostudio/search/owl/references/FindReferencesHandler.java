/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.search.owl.references;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.neontoolkit.gui.navigator.actions.AbstractSelectionBasedHandler;
import org.semanticweb.owlapi.model.OWLEntity;

import com.ontoprise.ontostudio.owl.gui.navigator.AbstractOwlEntityTreeElement;

/**
 * Action for the finding the references to an entity.
 * 
 * @author Nico Stieler
 */
public class FindReferencesHandler extends AbstractSelectionBasedHandler {

    /*
     * (non-Javadoc)
     * 
     * @see org.neontoolkit.gui.navigator.actions.AbstractSelectionBasedHandler#executeWithSelection(org.eclipse.ui.IWorkbenchPart,
     * org.eclipse.jface.viewers.IStructuredSelection)
     */
    @Override
    public Object executeWithSelection(IWorkbenchPart part, IStructuredSelection selection) throws ExecutionException {

        if (selection.getFirstElement() instanceof AbstractOwlEntityTreeElement) {
            final OWLEntity selectedEntity = ((AbstractOwlEntityTreeElement) selection.getFirstElement()).getEntity();
            final String selectedProject = ((AbstractOwlEntityTreeElement) selection.getFirstElement()).getProjectName();
            
            FindReferencesQuery frq = new FindReferencesQuery(selectedEntity, selectedProject);

            Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
            ProgressMonitorDialog pmd = new ProgressMonitorDialog(shell);
            pmd.setCancelable(false);
            pmd.create();
            
            IStatus status = NewSearchUI.runQueryInForeground(null, frq);
            return status;

        } else {
            return null;
        }
    }
}
