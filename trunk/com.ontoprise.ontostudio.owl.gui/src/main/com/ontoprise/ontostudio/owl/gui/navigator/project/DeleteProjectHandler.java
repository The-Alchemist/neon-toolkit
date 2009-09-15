/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.navigator.project;

import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.gui.navigator.actions.AbstractConfirmDeleteHandler;
import org.neontoolkit.gui.navigator.elements.IProjectElement;

import com.ontoprise.ontostudio.owl.gui.Messages;

/* 
 * Created on: 01.07.2005
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Action
 */
/**
 * Action for the removal of a project.
 */
public class DeleteProjectHandler extends AbstractConfirmDeleteHandler {

    private OWLProjectControl _control = OWLProjectControl.getDefault();

    public DeleteProjectHandler() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.actions.AbstractDeleteAction#doDelete(java.lang.Object[], java.lang.Object[], boolean)
     */
    @Override
    public boolean doDelete(Object[] items, Object[] parentItems, boolean checked) {
        for (int i = 0; i < items.length; i++) {
            if (items[i] instanceof OWLProjectTreeElement) {
                try {
                    String projectName = ((OWLProjectTreeElement) items[i]).getProjectName();
                    _control.deleteProject(projectName, checked);
                } catch (Exception e) {
                    new NeonToolkitExceptionHandler().handleException(e);
                    return false;
                }
            }
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
        return Messages.DeleteProjectAction_0;}

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.actions.AbstractDeleteAction#getCheckBoxText()
     */
    @Override
    public String getCheckBoxText() {
        if (isAtLeastOneElementPersistent()) {
            return Messages.DeleteProjectAction_1; 
        } else {
            return null;
        }
    }

    private boolean isAtLeastOneElementPersistent() {
        try {
            boolean persistent = false;
            for (Object o: getSelection().toArray()) {
                if (o instanceof OWLProjectTreeElement) {
                    String projectName = ((IProjectElement) o).getProjectName();
                    persistent |= NeOnCorePlugin.getDefault().getOntologyProject(projectName).isPersistent();
                }
            }
            return persistent;
        } catch (NeOnCoreException e) {
            throw new RuntimeException(e);
        }
    }

}
