/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands.annotations;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;

import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLNamespaces;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.OWLCommandUtils;
import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;

/**
 * @author werner
 * 
 */
public class AddOntologyAnnotation extends OWLModuleChangeCommand {

    /**
     * @param project
     * @param module
     * @param arguments
     * @throws NeOnCoreException
     */
    public AddOntologyAnnotation(String project, String module, String[] newValues) throws NeOnCoreException {
        super(project, module, (Object) newValues);
    }

    @Override
    protected void doPerform() throws CommandException {
        String[] newValues = (String[]) getArgument(2);

        String annotationProperty = newValues[0];
        String value = newValues[1];
        String range = newValues[2];
        String language = newValues[3];

        if (language.equals(OWLCommandUtils.EMPTY_LANGUAGE)) {
            // bugfix of bug 9674 - we get an exception if no type AND no language is selected,
            // so always use string if no datatype is selected
            if (range.equals("")) { //$NON-NLS-1$
                range = OWLNamespaces.XSD_NS + "string"; //$NON-NLS-1$
            }
        }
        try {
            OWLNamespaces namespaces = getOwlModel().getNamespaces();
            String expandedRange = namespaces.expandString(range);

            OWLDataFactory factory = OWLModelFactory.getOWLDataFactory(getProjectName());
            OWLAnnotationProperty annotProp = factory.getOWLAnnotationProperty(OWLUtilities.toIRI(annotationProperty));
            OWLLiteral c = null;
            if (language.equals(OWLCommandUtils.EMPTY_LANGUAGE)) {
                expandedRange = namespaces.expandString(range);
                c = factory.getOWLTypedLiteral(value, factory.getOWLDatatype(OWLUtilities.toIRI(expandedRange)));
            } else {
                c = factory.getOWLStringLiteral(value, language);
            }
            OWLAnnotation annot = factory.getOWLAnnotation(annotProp, c);
            getOwlModel().addAnnotation(annot);
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }
    }

}
