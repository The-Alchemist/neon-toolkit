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
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;

import com.ontoprise.ontostudio.owl.model.OWLNamespaces;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;

public class CreateSubClazz extends OWLModuleChangeCommand {

    public CreateSubClazz(String project, String ontologyId, String subClazzId, String superClazzId) throws NeOnCoreException {
        super(project, ontologyId, subClazzId, superClazzId);
    }

    @Override
    public void doPerform() throws CommandException {
        try {
            OWLNamespaces ns = getOwlModel().getNamespaces();
            OWLDataFactory factory = getOwlModel().getOWLDataFactory();
            OWLClassExpression subClazzId = OWLUtilities.description(getArgument(2).toString(), ns, factory);
            OWLClassExpression superClazzId = OWLUtilities.description(getArgument(3).toString(), ns, factory);
            getOwlModel().addAxiom(factory.getOWLSubClassOfAxiom(subClazzId, superClazzId));
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }

    }

}
