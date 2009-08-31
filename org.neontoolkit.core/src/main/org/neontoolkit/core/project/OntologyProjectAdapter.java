/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 11.06.2009
 * Created by: Dirk Wenke
 ******************************************************************************/
package org.neontoolkit.core.project;

/**
 * @author Dirk Wenke
 *
 */
public class OntologyProjectAdapter implements IOntologyProjectListener {

    @Override
    public void ontologyAdded(String projectName, String ontologyUri) {
        // Implementation does nothing
    }

    @Override
    public void ontologyModified(String projectName, String ontologyUri, boolean modified) {
        // Implementation does nothing
    }

    @Override
    public void ontologyRemoved(String projectName, String ontologyUri) {
        // Implementation does nothing
    }

    @Override
    public void ontologyRenamed(String projectName, String oldOntologyUri, String newOntologyUri) {
        // Implementation does nothing
    }

    @Override
    public void projectAdded(String projectName) {
        // Implementation does nothing
    }

    @Override
    public void projectRemoved(String projectName) {
        // Implementation does nothing
    }

    @Override
    public void projectRenamed(String oldProjectName, String newProjectName) {
        // Implementation does nothing
    }

    @Override
    public void ontologyStructureModified(String projectName, String ontologyUri, boolean modified) {
        // Implementation does nothing
    }
}
