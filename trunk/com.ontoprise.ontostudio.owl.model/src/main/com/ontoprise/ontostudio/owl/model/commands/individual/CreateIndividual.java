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
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLRuntimeException;

import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;
/**
 * 
 * @author Nico Stieler
 */
public class CreateIndividual extends OWLModuleChangeCommand {

    public CreateIndividual(String project, String ontologyId, String clazzId, String individualId) throws NeOnCoreException {
        super(project, ontologyId, clazzId, individualId);
    }

    @Override
    public void doPerform() throws CommandException {
        try {
            OWLDataFactory factory = OWLModelFactory.getOWLDataFactory(getProjectName());
            IRI clazzIRI,individualIRI;
            try{
                clazzIRI = OWLUtilities.owlFuntionalStyleSyntaxIRIToIRI(getArgument(2).toString());
            }catch(OWLRuntimeException e){
                clazzIRI = OWLUtilities.toIRI(getArgument(2).toString());
            }
            try{
                individualIRI = OWLUtilities.owlFuntionalStyleSyntaxIRIToIRI(getArgument(3).toString());   
            }catch(OWLRuntimeException e){
                individualIRI = OWLUtilities.toIRI(getArgument(3).toString());
            }
            OWLClass clazz = factory.getOWLClass(clazzIRI);
            OWLNamedIndividual individual = factory.getOWLNamedIndividual(individualIRI);
            OWLClassAssertionAxiom clazzMember = factory.getOWLClassAssertionAxiom(clazz, individual);

            getOwlModel().addEntity(individual);
            getOwlModel().addAxiom(clazzMember);
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }

    }
}
