/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.core.project;

import java.util.EventListener;



/* 
 * Created on: 30.03.2005
 * Created by: Mika Maier-Collin
 *
 * Keywords: OntologyProject, Project Listener
 */

/**
 * An implementation of this listener class is able to receive events from the
 * datamodel if an ontology changes its modified state. The syntax of the identifier
 * depends on the ontology type;
 * Listeners that want to be notified on connection failures as well should additionally implement
 * IConnectionFailureListener.
 * 
 * @author Mika Maier-Collin
 */
public interface IOntologyProjectListener extends EventListener {

    public void projectAdded(String projectName);
    
    public void projectRemoved(String projectName);

    public void projectRenamed(String oldProjectName, String newProjectName);

    public void ontologyModified(String projectName, String ontologyUri, boolean modified);    
    
    public void ontologyStructureModified(String projectName, String ontologyUri, boolean modified);    

    public void ontologyAdded(String projectName, String ontologyUri);
    
    public void ontologyRemoved(String projectName, String ontologyUri);

    public void ontologyRenamed(String projectName, String oldOntologyUri, String newOntologyUri);

}
