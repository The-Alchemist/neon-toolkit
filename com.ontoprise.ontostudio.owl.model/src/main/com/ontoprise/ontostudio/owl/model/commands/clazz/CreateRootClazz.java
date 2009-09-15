/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands.clazz;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLEntity;

import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;

public class CreateRootClazz extends OWLModuleChangeCommand {

    public CreateRootClazz(String project, String ontologyId, String clazzId) throws NeOnCoreException {
        super(project, ontologyId, clazzId);
    }

    @Override
    public void doPerform() throws CommandException {
        String subClazzId = getArgument(2).toString();

        try {
            OWLEntity owlClazz = OWLModelFactory.getOWLDataFactory(getProjectName()).getOWLClass(OWLUtilities.toURI(subClazzId));

            getOwlModel().addEntity(owlClazz);
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }

    }
}
