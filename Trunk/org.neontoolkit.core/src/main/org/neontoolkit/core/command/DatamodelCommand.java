/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.core.command;

import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.project.IOntologyProject;


/* 
 * Created on 07.09.2007
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
public abstract class DatamodelCommand extends LoggedCommand{
    public static final String INVALID_PROJECT_MESSAGE = "The given project is not valid!"; //$NON-NLS-1$
    
    protected static Object[] argumentsAsList(String firstArgument, Object... remainingArguments) {
		Object[] res = new Object[remainingArguments.length+1];
		res[0] = firstArgument;
		System.arraycopy(remainingArguments, 0, res, 1, remainingArguments.length);
		return res;
	}

	
	private String _project;

	protected DatamodelCommand(String project, Object... arguments) {
		super(argumentsAsList(project, arguments));
		_project = project;
	}
	
	/**
	 * Returns a handle for an ontology project.
	 * @return
	 * @throws NeOnCoreException 
	 */
	public IOntologyProject getOntologyProject() throws NeOnCoreException {
		return NeOnCorePlugin.getDefault().getOntologyProject(_project);
	}

	public String getProjectName() {
		return _project;
	}

}
