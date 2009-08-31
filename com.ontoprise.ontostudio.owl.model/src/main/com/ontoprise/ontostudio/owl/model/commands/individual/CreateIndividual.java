/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
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
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;

public class CreateIndividual extends OWLModuleChangeCommand {

    public CreateIndividual(String project, String ontologyId, String clazzId, String individualId) throws NeOnCoreException {
        super(project, ontologyId, clazzId, individualId);
    }

    @Override
    public void doPerform() throws CommandException {
        try {
            OWLDataFactory factory = OWLModelFactory.getOWLDataFactory(getProjectName());
            OWLClass clazz = factory.getOWLClass(OWLUtilities.toURI(getArgument(2).toString()));
            OWLNamedIndividual individual = factory.getOWLNamedIndividual(OWLUtilities.toURI(getArgument(3).toString()));
            OWLClassAssertionAxiom clazzMember = factory.getOWLClassAssertionAxiom(clazz, individual);

            getOwlModel().addEntity(individual);
            getOwlModel().addAxiom(clazzMember);
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }

    }
}
