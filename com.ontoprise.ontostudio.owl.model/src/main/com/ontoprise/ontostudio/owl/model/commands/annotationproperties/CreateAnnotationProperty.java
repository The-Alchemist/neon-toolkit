/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands.annotationproperties;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.util.IRIUtils;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;

import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;
/**
 * 
 * @author Nico Stieler
 */
public class CreateAnnotationProperty extends OWLModuleChangeCommand {

    public CreateAnnotationProperty(String project, String ontologyId, String propertyId, String superPropertyId) throws NeOnCoreException {
        super(project, ontologyId, propertyId, superPropertyId);
    }

    @Override
    public void doPerform() throws CommandException {
        String propertyId = getArgument(2).toString();
        String superPropertyId = (String) getArgument(3);

        try {
            OWLOntology ontology = getOwlModel().getOntology();
            OWLDataFactory factory = getOwlModel().getOWLDataFactory();
            OWLAnnotationProperty annotationProperty = OWLUtilities.annotationProperty(IRIUtils.ensureValidIRISyntax(propertyId), ontology);
            
            if (superPropertyId != null) { 
                OWLAnnotationProperty superAnnotationProperty = OWLUtilities.annotationProperty(IRIUtils.ensureValidIRISyntax(superPropertyId), ontology);
                getOwlModel().addAxiom(factory.getOWLSubAnnotationPropertyOfAxiom(annotationProperty, superAnnotationProperty));
            } else {
                getOwlModel().addEntity(annotationProperty);
            }
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }

    }

}
