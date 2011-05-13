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

import java.lang.reflect.InvocationTargetException;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.util.IRIUtils;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;

import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.ApplyChanges;
import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;

public class RemoveAnnotationProperty extends OWLModuleChangeCommand {

    public RemoveAnnotationProperty(String project, String ontologyId, String propertyId, String superPropertyId, boolean deleteSubProperties) throws NeOnCoreException {
        super(project, ontologyId, propertyId, superPropertyId, new Boolean(deleteSubProperties));
    }
    public RemoveAnnotationProperty(String project, String ontologyId, String propertyId, String superPropertyId) throws NeOnCoreException {
        this(project, ontologyId, propertyId, superPropertyId, false);
    }

    @Override
    public void doPerform() throws CommandException {

            boolean deleteSubProperties = ((Boolean) getArgument(4)).booleanValue();
            String subPropertyId = getArgument(2).toString();
            String superPropertyId = getArgument(3) != null ? getArgument(3).toString() : null;

            try {
                OWLDataFactory factory = getOwlModel().getOWLDataFactory();

                // delete subproperties, or shift them one level higher
                String[] subPropUris = new GetSubAnnotationProperties(getProjectName(), getOntology(), superPropertyId).getResults();
                if (deleteSubProperties) {
                    for (String uri: subPropUris) {
                        new RemoveAnnotationProperty(getProjectName(), getOntology(), uri, subPropertyId, deleteSubProperties).perform();
                    }
                } else {
                    // move subproperties one level higher
                    for (String uri: subPropUris) {
                        new MoveAnnotationProperty(getProjectName(), getOntology(), uri, subPropertyId, superPropertyId).perform();
                    }
                }

                if (superPropertyId == null) {
                    OWLAnnotationProperty annotationProperty = OWLModelFactory.getOWLDataFactory(getProjectName()).getOWLAnnotationProperty(
                            OWLUtilities.toIRI(subPropertyId));
                    getOwlModel().delEntity(annotationProperty, null);
                } else {
                    new ApplyChanges(getProjectName(), getOntology(), new OWLAxiom[0], 
                            new OWLAxiom[]{factory.getOWLSubAnnotationPropertyOfAxiom(
                                    OWLUtilities.annotationProperty(IRIUtils.ensureValidIRISyntax(subPropertyId)), 
                                    OWLUtilities.annotationProperty(IRIUtils.ensureValidIRISyntax(superPropertyId)))}).perform();
                }
//            String propertyId = (String)getArgument(2);
//
//            OWLAnnotationProperty annotProperty = getOwlModel().getOWLDataFactory().getOWLAnnotationProperty(OWLUtilities.toIRI(propertyId));
//            getOwlModel().delEntity(annotProperty, null);
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        } catch (InterruptedException e) {
            throw new CommandException(e);
        } catch (InvocationTargetException e) {
            throw new CommandException(e);
        }

    }

}
