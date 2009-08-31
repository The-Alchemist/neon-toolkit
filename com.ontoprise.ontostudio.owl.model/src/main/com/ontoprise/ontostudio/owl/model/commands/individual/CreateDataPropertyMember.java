/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 29.01.2009
 * Created by: werner
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.model.commands.individual;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;

import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLNamespaces;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.ApplyChanges;
import com.ontoprise.ontostudio.owl.model.commands.OWLCommandUtils;
import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;

/**
 * @author werner
 * 
 */
public class CreateDataPropertyMember extends OWLModuleChangeCommand {

    /**
     * @param project
     * @param module
     * @param arguments
     * @throws NeOnCoreException
     */
    public CreateDataPropertyMember(String project, String module, String individualUri, String propertyUri, String type, String[] value) throws NeOnCoreException {
        super(project, module, individualUri, propertyUri, type, (Object) value);
    }

    @Override
    protected void doPerform() throws CommandException {
        String individualUri = (String) getArgument(2);
        String propertyUri = (String) getArgument(3);
        String type = (String) getArgument(4);
        String value = ((String[]) getArgument(5))[0];
        String language = ((String[]) getArgument(5))[1];

        try {
            OWLDataFactory factory = OWLModelFactory.getOWLDataFactory(getProjectName());
            OWLIndividual individual = factory.getOWLNamedIndividual(OWLUtilities.toURI(individualUri));
            OWLLiteral c;
            if (type.equals(OWLNamespaces.RDF_NS + "XMLLiteral")) { //$NON-NLS-1$
                if (!language.equals(OWLCommandUtils.EMPTY_LANGUAGE) && !language.equals("")) { //$NON-NLS-1$
                    c = factory.getOWLStringLiteral(value, language);
                } else {
                    c = factory.getOWLTypedLiteral(value, factory.getOWLDatatype(OWLUtilities.toURI(OWLNamespaces.XSD_NS+"string")));
                }
            } else {
                c = factory.getOWLTypedLiteral(value, factory.getOWLDatatype(OWLUtilities.toURI(type)));
            }

            OWLDataPropertyExpression prop = OWLModelFactory.getOWLDataFactory(getProjectName()).getOWLDataProperty(OWLUtilities.toURI(propertyUri));
            OWLAxiom newAxiom = factory.getOWLDataPropertyAssertionAxiom(prop, individual, c);

            new ApplyChanges(getProjectName(), getOntology(), new String[] {OWLUtilities.toString(newAxiom)}, new String[0]).perform();
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }
    }

}
