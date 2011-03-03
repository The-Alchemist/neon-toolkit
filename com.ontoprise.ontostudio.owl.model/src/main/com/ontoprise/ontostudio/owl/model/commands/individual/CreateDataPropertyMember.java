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
import org.neontoolkit.core.util.IRIUtils;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;

import com.ontoprise.ontostudio.owl.model.OWLConstants;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.ApplyChanges;
import com.ontoprise.ontostudio.owl.model.commands.OWLCommandUtils;
import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;

/**
 * @author werner
 * @author Nico Stieler
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
            OWLIndividual individual = OWLUtilities.individual(IRIUtils.ensureValidIRISyntax(OWLUtilities.owlFuntionalStyleSyntaxIRIToIRI(individualUri).toString()));
            OWLLiteral c;
            if (type.equals(OWLConstants.RDF_PLAIN_LITERAL)) {
                if (!language.equals(OWLCommandUtils.EMPTY_LANGUAGE) && !language.equals("")) { //$NON-NLS-1$
                    c = factory.getOWLLiteral(value, language);
                } else {
                    c = factory.getOWLLiteral(value, factory.getOWLDatatype(OWLUtilities.toIRI(OWLConstants.XSD_STRING)));
                }
            } else {
                c = factory.getOWLLiteral(value, factory.getOWLDatatype(OWLUtilities.owlFuntionalStyleSyntaxIRIToIRI(type)));
            }
            OWLDataPropertyExpression prop = OWLUtilities.dataProperty(IRIUtils.ensureValidIRISyntax(OWLUtilities.owlFuntionalStyleSyntaxIRIToIRI(propertyUri).toString()));
            OWLAxiom newAxiom = factory.getOWLDataPropertyAssertionAxiom(prop, individual, c);

            new ApplyChanges(getProjectName(), getOntology(), new String[] {OWLUtilities.toString(newAxiom)}, new String[0]).perform();
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }
    }

}
