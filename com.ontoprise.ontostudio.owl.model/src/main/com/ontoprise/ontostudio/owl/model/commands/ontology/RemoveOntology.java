/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 16.06.2009
 * Created by: diwe
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.model.commands.ontology;

import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.project.IOntologyProject;

import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;

/**
 * @author diwe
 *
 */
public class RemoveOntology extends OWLModuleChangeCommand {
    public RemoveOntology(String project, String ontologyUri, boolean removeFromDM) throws NeOnCoreException {
        super(project, ontologyUri, removeFromDM);
    }

    @Override
    protected void doPerform() throws CommandException {
        try {
            IOntologyProject project = NeOnCorePlugin.getDefault().getOntologyProject(getProjectName());
            project.removeOntology(getOntology(), (Boolean)getArgument(2));
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }
    }
}
