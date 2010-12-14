/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands.project;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.command.DatamodelCommand;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.project.IOntologyProject;

import com.ontoprise.ontostudio.owl.model.OWLManchesterProject;

/**
 * @author diwe
 *
 */
public class RestoreProject extends DatamodelCommand {
    private IProgressMonitor _monitor;
    
    public RestoreProject(String project) {
        super(project);
    }
    
    public RestoreProject(String project, IProgressMonitor monitor) {
        this(project);
        _monitor = monitor;
    }
    @Override
    public void perform() throws CommandException {
        doPerform();
        try {
            getOntologyProject().readAndDispatchWhileWaitingForEvents();
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }
    }
    protected void doPerform() throws CommandException {
        try {
            IOntologyProject ontologyProject = NeOnCorePlugin.getDefault().getOntologyProject(getProjectName());
            String[] ontosFromNature = ontologyProject.getOntologyProjectNature().getOntologies();

            URI[] ontologyFileUris = ontologyProject.getOntologyFiles();
            ((OWLManchesterProject)ontologyProject).importOntologies(ontologyFileUris, true, _monitor);

            Set<String> loadedOntos = ontologyProject.getAvailableOntologyURIs();
            Set<URI> remoteOntosToLoad = new HashSet<URI>();
            
            for (String ontoFromNature: ontosFromNature) {
                boolean loadOnto = true;
                for (String loadedOnto: loadedOntos) {
                    if(loadedOnto.equals(ontoFromNature)) {
                        loadOnto = false;
                        break;
                    }
                }
                if(loadOnto) {
                    try {
                        URI uri = new URI(ontoFromNature);
                        remoteOntosToLoad.add(uri);
                    } catch (URISyntaxException e) {
                        // ignore
                    }
                }
            }
            ((OWLManchesterProject)ontologyProject).importOntologies(remoteOntosToLoad.toArray(new URI[remoteOntosToLoad.size()]), true, _monitor);
        } catch (Exception e) {
            throw new CommandException(e);
        }
    }

}
