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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.command.DatamodelCommand;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.natures.OntologyProjectNature;
import org.neontoolkit.core.project.IOntologyProject;

/* 
 * Created on: 24.10.2008
 * Created by: Mika Maier-Collin
 *
 * Keywords: 
 */
/**
 * Adds one or multiple existing ontologies to a project.
 */

public class AddOWLOntology extends DatamodelCommand {

    public AddOWLOntology(String project, String[] ontologies) {
        super(project, (Object) ontologies);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.datamodel.api.DatamodelCommand#run()
     */
    @Override
    public void perform() throws CommandException {
        String project = (String) getArgument(0);
        String[] ontologies = (String[]) getArgument(1);
        if (ontologies != null) {
            try {
                List<String> ontosToAdd = new ArrayList<String>();
                Set<String> openOntologyURIs;
                openOntologyURIs = new LinkedHashSet<String>(Arrays.asList(NeOnCorePlugin.getDefault().getOntologyProject(project).getOntologies()));
                // only add if not already open
                for (int j = 0; j < ontologies.length; j++) {
                    // String ontoUri = URIUtilities.moduleToOntologyURI(container.createTerm(ontologies[j]));
                    String ontoUri = ontologies[j];
                    if (!openOntologyURIs.contains(ontoUri)) {
                        ontosToAdd.add(ontologies[j]);
                    }
                }
                addOntologies(project, ontologies);
            } catch (Exception ke) {
                throw new CommandException(ke);
            }
        }

    }

    private void addOntologies(String project, String[] ontologies) throws NeOnCoreException, CoreException {
        IOntologyProject ontologyProject = NeOnCorePlugin.getDefault().getOntologyProject(project);
        Set<String> openOntologyURIs = new LinkedHashSet<String>(Arrays.asList(ontologyProject.getOntologies()));
        for (int i = 0; i < ontologies.length; i++) {
            String ontoUri = ontologies[i];
            if (!openOntologyURIs.contains(ontoUri)) {
                // only open, if not already open
                ontologyProject.openOntology(ontoUri);
            }
        }

        OntologyProjectNature nature = ontologyProject.getOntologyProjectNature();
        for (String onto: ontologies) {
            nature.addOntology(onto);
        }
    }
}
