/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 20.02.2009
 * Created by: diwe
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.model.commands.project;

import java.net.URI;

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
    @SuppressWarnings("unused")
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
            URI[] ontologyFileUris = ontologyProject.getOntologyFiles();
            ((OWLManchesterProject)ontologyProject).importOntologies(ontologyFileUris, true);
        } catch (Exception e) {
            throw new CommandException(e);
        }
    }
}
