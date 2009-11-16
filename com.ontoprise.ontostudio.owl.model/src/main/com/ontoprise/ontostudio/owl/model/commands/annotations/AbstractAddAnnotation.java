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

import java.util.ArrayList;
import java.util.List;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;

import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLNamespaces;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.ApplyChanges;
import com.ontoprise.ontostudio.owl.model.commands.OWLCommandUtils;
import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;
import com.ontoprise.ontostudio.owl.model.util.OWLAxiomUtils;

/**
 * @author werner
 * 
 */
public abstract class AbstractAddAnnotation extends OWLModuleChangeCommand {

    /**
     * @param project
     * @param module
     * @param arguments
     * @throws NeOnCoreException
     */
    public AbstractAddAnnotation(String project, String module, Object subject, String[] newValues) throws NeOnCoreException {
        super(project, module, subject, newValues);
    }

    @Override
    protected void doPerform() throws CommandException {
        
        String[] newValues = (String[]) getArgument(3);

        String property = newValues[0];
        String value = newValues[1];
        String range = newValues[2];
        String language = newValues[3];

        try {
            OWLNamespaces namespaces = getOwlModel().getNamespaces();
            OWLDataFactory factory = OWLModelFactory.getOWLDataFactory(getProjectName());

            
            String expandedRange = namespaces.expandString(range);
            String expandedProperty = namespaces.expandString(property);
            OWLAnnotationProperty annotProp = factory.getOWLAnnotationProperty(OWLUtilities.toURI(expandedProperty));
            OWLAnnotationValue c = null;
            if (language.equals(OWLCommandUtils.EMPTY_LANGUAGE) || language.equals("")) { //$NON-NLS-1$
                // bugfix of bug 9674 - we get an exception if no type AND no language is selected,
                // so always use string if no datatype is selected
                if (range.equals("")) { //$NON-NLS-1$
                    range = OWLNamespaces.XSD_NS + "string"; //$NON-NLS-1$
                }
                expandedRange = namespaces.expandString(range);
                if (expandedRange.equals(OWLAxiomUtils.OWL_INDIVIDUAL)) {
                    c = IRI.create(OWLUtilities.toURI(namespaces.expandString(value)));
                } else {
                    c = factory.getOWLTypedLiteral(value, factory.getOWLDatatype(OWLUtilities.toURI(expandedRange)));
                }
            } else {
                c = factory.getOWLStringLiteral(value, language);
            }
            OWLAnnotation annot = factory.getOWLAnnotation(annotProp, c);

            List<OWLAxiom> axiomsToAdd = new ArrayList<OWLAxiom>();
            axiomsToAdd.add(factory.getOWLAnnotationAssertionAxiom(getAnnotationSubject(), annot));
            new ApplyChanges(getProjectName(), getOntology(), axiomsToAdd.toArray(new OWLAxiom[axiomsToAdd.size()]), new OWLAxiom[0]).perform();
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }
    }

    protected abstract OWLAnnotationSubject getAnnotationSubject() throws NeOnCoreException;
}
