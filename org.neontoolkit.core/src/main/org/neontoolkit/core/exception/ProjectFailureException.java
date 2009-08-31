/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 02.07.2009
 * Created by: krekeler
 ******************************************************************************/
package org.neontoolkit.core.exception;

import org.neontoolkit.core.project.IOntologyProject;

/**
 * Thrown when an ontology project has becomes erroneous.
 *
 */
public class ProjectFailureException extends NeOnCoreException {
    private static final long serialVersionUID = -2747269481367627189L;
    private final IOntologyProject _ontologyProject;

    public ProjectFailureException(IOntologyProject ontologyProject, Throwable cause) {
        super(cause);
        _ontologyProject = ontologyProject;
    }
    
    public IOntologyProject getOntologyProject() {
        return _ontologyProject;
    }
    
    @Override
    public String getErrorCode() {
        return null;
    }

}
