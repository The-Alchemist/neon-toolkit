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
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.ApplyChanges;
import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;

/**
 * @author werner
 * 
 */
public class CreateObjectPropertyMember extends OWLModuleChangeCommand {

    /**
     * @param project
     * @param module
     * @param arguments
     * @throws NeOnCoreException
     */
    public CreateObjectPropertyMember(String project, String module, String individualUri, String propertyUri, String targetIndividualUri) throws NeOnCoreException {
        super(project, module, individualUri, propertyUri, targetIndividualUri);
    }

    @Override
    protected void doPerform() throws CommandException {
        String individualUri = (String) getArgument(2);
        String propertyUri = (String) getArgument(3);
        String targetIndividualUri = (String) getArgument(4);

        try {
            OWLDataFactory factory = OWLModelFactory.getOWLDataFactory(getProjectName());
            OWLIndividual individual = factory.getOWLNamedIndividual(OWLUtilities.toIRI(individualUri));
            OWLIndividual targetIndividual = factory.getOWLNamedIndividual(OWLUtilities.toIRI(targetIndividualUri));
            OWLObjectPropertyExpression objPropExpr = factory.getOWLObjectProperty(OWLUtilities.toIRI(propertyUri));
            OWLObjectPropertyAssertionAxiom newAxiom = factory.getOWLObjectPropertyAssertionAxiom(objPropExpr, individual, targetIndividual);

            new ApplyChanges(getProjectName(), getOntology(), new String[] {OWLUtilities.toString(newAxiom)}, new String[0]).perform();
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }
    }

}
