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
 * @author Nico Stieler
 * 
 */
public class RemoveOntologyAnnotation extends OWLModuleChangeCommand {

    /**
     * @param project
     * @param module
     * @param arguments
     * @throws NeOnCoreException
     */
    public RemoveOntologyAnnotation(String project, String module, String annotationPropertyText, String annotationValueText, String language, String datatype) throws NeOnCoreException {
        super(project, module, annotationPropertyText, annotationValueText, language, datatype);
    }

    @Override
    protected void doPerform() throws CommandException {
        String annotationPropertyText = (String) getArgument(2);
        String annotationValueText = (String) getArgument(3);
        String language = (String) getArgument(4);
        String datatype = (String) getArgument(5);
        try {
            OWLNamespaces namespaces = getOwlModel().getNamespaces();
            String expandedRange = namespaces.expandString(datatype);

            OWLDataFactory factory = OWLModelFactory.getOWLDataFactory(getProjectName());
            OWLAnnotationProperty annotProp = factory.getOWLAnnotationProperty(OWLUtilities.toIRI(annotationPropertyText));

            OWLLiteral c = null;
            if (language.equals(OWLCommandUtils.EMPTY_LANGUAGE)) {
                expandedRange = namespaces.expandString(datatype);
                c = factory.getOWLLiteral(annotationValueText, factory.getOWLDatatype(OWLUtilities.toIRI(expandedRange)));
            } else {
                c = factory.getOWLLiteral(annotationValueText, language);
            }
            OWLAnnotation annot = factory.getOWLAnnotation(annotProp, c);
            getOwlModel().removeAnnotation(annot);
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }
    }

}
