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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.navigator.elements.IOntologyElement;
import org.neontoolkit.gui.navigator.elements.IProjectElement;

import com.ontoprise.ontostudio.owl.gui.Messages;

/* 
 * Created on: 24.10.2008
 * Created by: Mika Maier-Collin
 *
 * Keywords: UI, Action, Runnable
 */
/**
 * Runnable processing the removal of an owl ontology.
 */

public class DeleteOntologyRunnable implements IRunnableWithProgress {
    private Object[] _selectedItems;
    private Object[] _parentItems;
    private boolean _deleteFromModel;
    private Exception _exception;

    public DeleteOntologyRunnable(Object[] items, Object[] parentItems, boolean checked) {
        _selectedItems = items;
        _parentItems = parentItems;
        _deleteFromModel = checked;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void run(IProgressMonitor monitor) throws InvocationTargetException,InterruptedException {
        monitor.beginTask(Messages.DeleteOntologyRunnable_0, IProgressMonitor.UNKNOWN); 
        for (int i = 0; i < _selectedItems.length; i++) {
            if (_selectedItems[i] instanceof IOntologyElement) {
                String project = (_parentItems[i] instanceof IProjectElement) ? ((IProjectElement) _parentItems[i]).getProjectName() : null;
                try {
                    String ontologyUri = ((IOntologyElement) _selectedItems[i]).getOntologyUri();
                    NeOnCorePlugin.getDefault().getOntologyProject(project).removeOntology(ontologyUri, _deleteFromModel);
                } catch (NeOnCoreException e) {
                    _exception = e;
                }
            }
        }
        monitor.done();
    }

    public Exception getException() {
        return _exception;
    }
}
