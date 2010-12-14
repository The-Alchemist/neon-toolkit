/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands.imports;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;

import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;

/**
 * @author werner
 *
 */
public class RemoveOntologyFromImports extends OWLModuleChangeCommand {

    /**
     * @param project
     * @param module
     * @param arguments
     * @throws NeOnCoreException
     */
    public RemoveOntologyFromImports(String project, String module, String ontologyUri) throws NeOnCoreException {
        super(project, module, ontologyUri);
    }

    @Override
    protected void doPerform() throws CommandException {
        String ontologyUri = (String) getArgument(2);
        try {
            getOwlModel().removeFromImports(OWLModelFactory.getOWLModel(ontologyUri, getProjectName()));
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }
    }

}
