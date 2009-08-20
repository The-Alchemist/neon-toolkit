/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
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
import org.neontoolkit.core.exception.NeOnCoreException;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;

/* 
 * Created on: 08.03.2007
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Refactor, Runnable
 */
/**
 * Runnable processing the renaming of a module
 */
public class RenameOntologyRunnableWithProgress implements IRunnableWithProgress {
    private String _oldOntologyUri;
    private String _newOntologyUri;
    private String _project;

    public RenameOntologyRunnableWithProgress(String project, String oldModuleId, String newModuleId) {
        _oldOntologyUri = oldModuleId;
        _newOntologyUri = newModuleId;
        _project = project;
    }

    public void run(IProgressMonitor monitor) throws InvocationTargetException,InterruptedException {
        monitor.beginTask(Messages.RenameOntologyRunnableWithProgress_0, IProgressMonitor.UNKNOWN); 
        try {
            OWLModelFactory.getOWLModel(_oldOntologyUri, _project).renameOntology(_newOntologyUri);
        } catch (NeOnCoreException e) {
            throw new InvocationTargetException(e);
        }
        monitor.done();
    }

}
