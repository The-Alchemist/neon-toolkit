/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.navigator.project;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.project.IOntologyProject;
import org.neontoolkit.core.project.OntologyProjectManager;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.gui.navigator.elements.IProjectElement;
import org.neontoolkit.swt.dialogs.DefaultListSelectionDialogModel;
import org.neontoolkit.swt.dialogs.EditableListSelectionDialog;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;

/* 
 * Created on: 24.10.2008
 * Created by: Mika Maier-Collin
 *
 * Keywords: 
 */

public class AddOWLOntologyAction implements IObjectActionDelegate {

    private IWorkbenchPart _part;
    private IProjectElement _element;

    public AddOWLOntologyAction() {
    }

    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        _part = targetPart;
    }

    public void run(IAction action) {
        EditableListSelectionDialog dialog = new EditableListSelectionDialog(_part.getSite().getShell());
        dialog.setImage(OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.ONTOLOGY));
        try {
            Set<String> ontologyUris;
            Set<String> projectOntologyUris;
            IOntologyProject ontologyProject = NeOnCorePlugin.getDefault().getOntologyProject(_element.getProjectName());
            ontologyUris = ontologyProject.getAvailableOntologyURIs();
            projectOntologyUris = new LinkedHashSet<String>(Arrays.asList(ontologyProject.getOntologies()));
            for (String projectOntologyUri: projectOntologyUris) {
                ontologyUris.remove(projectOntologyUri);
            }
            if (ontologyUris.size() > 0) {
                dialog.setModel(new DefaultListSelectionDialogModel(ontologyUris.toArray(), true));
                dialog.setTitle(Messages.AddOWLOntologyAction_0);
                dialog.open();
                Object[] selectedItems = dialog.getSelectedItems();
                if (selectedItems != null) {
                    String[] ontologies = new String[selectedItems.length];
                    for (int i = 0; i < selectedItems.length; i++) {
                        ontologies[i] = selectedItems[i].toString();
                    }
                    new AddOWLOntology(_element.getProjectName(), ontologies).run();
                }
            } else {
                MessageDialog.openInformation(_part.getSite().getShell(), Messages.AddOWLOntologyAction_1, Messages.AddOWLOntologyAction_2);
            }
        } catch (Exception de) {
            // TODO: migration
            Logger.getLogger(getClass()).error("TODO: migration"); //$NON-NLS-1$
//            if (de instanceof InternalOntoStudioException) {
//                InternalOntoStudioException exception = (InternalOntoStudioException) de;
//                if (exception.getCause() instanceof OntologyResolveException) {
//                    OntologyResolveException ore = (OntologyResolveException) exception.getCause();
//                    StringBuffer message = new StringBuffer(Messages.AddOWLOntologyAction_3);
//                    message.append(ore.getOntologyURI());
//                    message.append(Messages.AddOWLOntologyAction_4);
//                    MessageDialog.openError(_part.getSite().getShell(), Messages.AddOWLOntologyAction_5, message.toString());
//                    return;
//                }
//            }
            new NeonToolkitExceptionHandler().handleException(Messages.AddOWLOntologyAction_6, de, _part.getSite().getShell());
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {
        action.setEnabled(true);
        IStructuredSelection sel = (IStructuredSelection) selection;
        if (sel.getFirstElement() instanceof IProjectElement) {
            _element = (IProjectElement) sel.getFirstElement();
            try {
                IOntologyProject ontoProject = OntologyProjectManager.getDefault().getOntologyProject(_element.getProjectName());
                if (ontoProject != null && ontoProject.isPersistent()) {
                    action.setEnabled(false);
                }
            } catch (NeOnCoreException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
