/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.core;

import org.eclipse.core.resources.IProject;
import org.neontoolkit.core.project.IOntologyProject;

/**
 * This is an interface that has to be implemented by factories that can create
 * OntologyProjects. Extensions of the org.neontoolkit.core.ontologyProject
 * extension point have to implement this interface.
 * @author Dirk Wenke
 */
public interface IOntologyProjectFactory {

    /**
     * This method is called after the project resource has been created. The project
     * already has the OntologyProjectNature and only the IOntologyProject implementation
     * has to be instantiated and returned.
     * @param project the project resource
     */
    IOntologyProject createOntologyProject(IProject project);
    
    /**
     * Returns the unique identifier of the factory which is defined in the extension.
     * @return
     */
    String getIdentifier();
}
