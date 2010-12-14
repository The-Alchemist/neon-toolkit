/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands.individual;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;

import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;

public class MoveIndividual extends OWLModuleChangeCommand {

    public MoveIndividual(String project, String ontologyId, String instanceId, String oldParentId, String newParentId) throws NeOnCoreException {
        super(project, ontologyId, instanceId, oldParentId, newParentId);
    }

    @Override
    public void doPerform() throws CommandException {
        String instanceId = (String) getArgument(2);
        String oldParent = (String) getArgument(3);
        String newParent = (String) getArgument(4);

        try {
            new RemoveIndividual(getProjectName(), getOntology(), oldParent, new String[] {instanceId}, false).perform();
            new CreateIndividual(getProjectName(), getOntology(), newParent, instanceId).perform();
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }
    }
}
