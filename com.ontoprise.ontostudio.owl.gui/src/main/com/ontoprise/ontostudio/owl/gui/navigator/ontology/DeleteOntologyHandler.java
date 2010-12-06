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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.project.IOntologyProject;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.gui.navigator.actions.AbstractConfirmDeleteHandler;
import org.neontoolkit.gui.navigator.elements.IOntologyElement;
import org.neontoolkit.gui.navigator.elements.IProjectElement;

import com.ontoprise.ontostudio.owl.gui.Messages;

/**
 * Action to delete classes in the tree.
 */
public class DeleteOntologyHandler extends AbstractConfirmDeleteHandler {

    public DeleteOntologyHandler() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.actions.AbstractDeleteAction#doDelete(java.lang.Object[], java.lang.Object[])
     */
    @Override
    public boolean doDelete(Object[] items, Object[] parentItems, boolean checked) {
        ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());
        DeleteOntologyRunnable runnable = new DeleteOntologyRunnable(items, parentItems, checked);
        try {
            if (!checkImportingModules(items, parentItems)) {
                return true;
            }
            // dialog.run(true, false, runnable);
            dialog.run(false, false, runnable);
        } catch (Exception e) {
            new NeonToolkitExceptionHandler().handleException(e);
            return false;
        }
        if (runnable.getException() != null) {
            new NeonToolkitExceptionHandler().handleException(runnable.getException());
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.actions.AbstractDeleteAction#getDialogMessage()
     */
    @Override
    public String getDialogMessage() {
        return Messages.DeleteOntologyAction_0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.actions.AbstractDeleteAction#getCheckBoxText()
     */
    @Override
    public String getCheckBoxText() {
        if (isAtLeastOneElementPersistent()) {
            return Messages.DeleteOntologyAction_1;
        } else {
            return null;
        }
    }

    private boolean isAtLeastOneElementPersistent() {
        try {
            boolean persistent = false;
            for (Object o: getSelection().toArray()) {
                if (o instanceof IProjectElement) {
                    String projectName = ((IProjectElement) o).getProjectName();
                    persistent |= NeOnCorePlugin.getDefault().getOntologyProject(projectName).isPersistent();
                }
            }
            return persistent;
        } catch (NeOnCoreException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean checkImportingModules(Object[] items, Object[] parentItems) {
        Map<String,List<String>> ontologyPerProject = new HashMap<String,List<String>>();
        for (int i = 0; i < parentItems.length; i++) {
            String project = ((IProjectElement) parentItems[i]).getProjectName();
            String ontologyUri = ((IOntologyElement) items[i]).getOntologyUri();
            List<String> ontologyUris = ontologyPerProject.get(project);
            if (ontologyUris == null) {
                ontologyUris = new ArrayList<String>();
                ontologyPerProject.put(project, ontologyUris);
            }
            ontologyUris.add(ontologyUri);
        }

        Set<String> additionalOntologies = new HashSet<String>();
        for (String project: ontologyPerProject.keySet()) {
            try {
                IOntologyProject ontologyProject = NeOnCorePlugin.getDefault().getOntologyProject(project);
                String[] openOntologies = ontologyProject.getOntologies();
                List<String> ontosToDelete = ontologyPerProject.get(project);
                for (String openOntology: openOntologies) {
                    if (ontosToDelete.contains(openOntology)) {
                        // ontology that is not going to be deleted
                        Set<String> importingOntologies = ontologyProject.getAllImportingOntologyURIs(openOntology);
                        for (String importingOntology: importingOntologies) {
                            if (!ontosToDelete.contains(importingOntology)) {
                                additionalOntologies.add(project + ": " + importingOntology); //$NON-NLS-1$
                            }
                        }
                    }
                }
            } catch (NeOnCoreException e) {
                e.printStackTrace();
            }
        }
        if (additionalOntologies.isEmpty()) {
            return true;
        } else {
            StringBuffer msg = new StringBuffer(Messages.DeleteOntologyHandler_0);
            for (String onto: additionalOntologies) {
                msg.append(onto);
                msg.append("\n"); //$NON-NLS-1$
            }
            msg.append(Messages.DeleteOntologyHandler_1);
            return MessageDialog.openConfirm(getShell(), Messages.DeleteOntologyHandler_2, msg.toString());
        }
    }
}
