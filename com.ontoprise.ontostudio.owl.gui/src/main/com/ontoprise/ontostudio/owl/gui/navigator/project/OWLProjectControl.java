/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.navigator.project;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.natures.OntologyProjectNature;
import org.neontoolkit.core.project.OntologyProjectManager;

import com.ontoprise.ontostudio.owl.model.OWLManchesterProjectFactory;

public class OWLProjectControl {

    private static OWLProjectControl _singleton;

    public OWLProjectControl() {

    }

    public static OWLProjectControl getDefault() {
        if (_singleton == null) {
            _singleton = new OWLProjectControl();
        }
        return _singleton;
    }

    /**
     * Returns an array of all names of current open owl ontology projects.
     * 
     * @return
     * @throws CommandException
     */
    public String[] getOwlOntologyProjects() throws CommandException {
        try {
            return OntologyProjectManager.getDefault().getOntologyProjects(OWLManchesterProjectFactory.ONTOLOGY_LANGUAGE);
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }
    }

    /**
     * Removes the project with the given name from the workspace. If the project is an ontology project and the delete flag is true, all ontologies contained
     * in the project are removed from the datamodel.
     * 
     * @param projectName
     * @param deleteFromDB
     * @throws CommandException
     */
    public void deleteProject(String projectName, boolean deleteFromDB) throws CommandException {
        try {
            IProject project = NeOnCorePlugin.getDefault().getProject(projectName);
            if (project.exists()) {
                OntologyProjectNature nature = (OntologyProjectNature) project.getNature(OntologyProjectNature.ID);
                if (nature != null) {
                    String[] ontos = nature.getOntologies();
                    for (int j = 0; j < ontos.length; j++) {
                        NeOnCorePlugin.getDefault().getOntologyProject(projectName).removeOntology(ontos[j], deleteFromDB);
                    }
                }
                project.delete(true, true, null);
            }
        } catch (Throwable t) {
            throw new CommandException(t);
        }
    }

    /**
     * Adds a resource change listener to the workspace.
     * 
     * @param listener
     */
    public void addResourceChangeListener(IResourceChangeListener listener) {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        workspace.addResourceChangeListener(listener);
    }

    /**
     * Removes the given resource change listener from the workspace.
     * 
     * @param listener
     */
    public void removeResourceChangeListener(IResourceChangeListener listener) {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        workspace.removeResourceChangeListener(listener);
    }

}
