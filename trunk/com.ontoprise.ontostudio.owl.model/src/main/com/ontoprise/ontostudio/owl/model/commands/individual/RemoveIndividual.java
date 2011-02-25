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

import java.lang.reflect.InvocationTargetException;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.util.IRIUtils;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.ApplyChanges;
import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;
/**
 * 
 * @author Nico Stieler
 */
public class RemoveIndividual extends OWLModuleChangeCommand {

    public RemoveIndividual(String project, String ontologyId, String clazzId, String[] individualIds) throws NeOnCoreException {
        this(project, ontologyId, clazzId, individualIds, true);
    }

    public RemoveIndividual(String project, String ontologyId, String clazzId, String[] individualIds, boolean removeReferringAxioms) throws NeOnCoreException {
        super(project, ontologyId, clazzId, individualIds, removeReferringAxioms);
    }

    @Override
    public void doPerform() throws CommandException {
        String clazzUri = (String) getArgument(2);
        String[] individuals = (String[]) getArgument(3);
        boolean removeReferringAxioms = ((Boolean) getArgument(4)).booleanValue();
        for (int i = 0; i < individuals.length; i++) {
            try {
                OWLDataFactory factory = OWLModelFactory.getOWLDataFactory(getProjectName());
                OWLOntology ontology = OWLModelFactory.getOWLModel(getOntology(), getProjectName()).getOntology();

                OWLIndividual individual = OWLUtilities.individual(individuals[i], ontology);
                if(individual instanceof OWLNamedIndividual){
                    OWLNamedIndividual namedIndividual = (OWLNamedIndividual) individual;
                    if (removeReferringAxioms) {
                        getOwlModel().delEntity(namedIndividual, null);
                    } else {
                        // called from MoveIndividual, so don' t remove all referring axioms
                        OWLClassExpression clazzDescription = OWLUtilities.description(IRIUtils.ensureValidIRISyntax(OWLUtilities.owlFuntionalStyleSyntaxIRIToIRI(clazzUri, ontology).toString()), ontology);
                        OWLClassAssertionAxiom clazzMember = factory.getOWLClassAssertionAxiom(clazzDescription, namedIndividual);
                        new ApplyChanges(getProjectName(), getOntology(), new String[0], new String[] {OWLUtilities.toString(clazzMember, ontology)}).perform();
                    }
                }
            } catch (NeOnCoreException e) {
                throw new CommandException(e);
            } catch (InterruptedException e) {
                throw new CommandException(e);
            } catch (InvocationTargetException e) {
                throw new CommandException(e);
            }
        }

    }
}
