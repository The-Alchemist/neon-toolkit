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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.project.IOntologyProject;
import org.neontoolkit.gui.Messages;

/* 
 * Created on: 08.03.2007
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Refactor, Runnable
 */
/**
 * Runnable processing the renaming of an ontology 
 */
public class RenameOntologyRunnableWithProgress implements IRunnableWithProgress {
	private String _oldOntologyUri;
	private String _newOntologyUri;
	private String _project;
	
	public RenameOntologyRunnableWithProgress(String project, String oldOntologyUri, String newOntologyUri) {
		_oldOntologyUri = oldOntologyUri;
		_newOntologyUri = newOntologyUri;
		_project = project;
	}
	

	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
        try {
        	monitor.beginTask(Messages.RenameOntologyRunnableWithProgress_0, IProgressMonitor.UNKNOWN);
            IOntologyProject ontoProject = NeOnCorePlugin.getDefault().getOntologyProject(_project);
            ontoProject.renameOntology(_oldOntologyUri, _newOntologyUri);
			monitor.done();
		} catch (NeOnCoreException e) {
			throw new InvocationTargetException(e);
		}
	}

}
