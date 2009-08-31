/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands.annotationproperties;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.InternalNeOnException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;

import com.ontoprise.ontostudio.owl.model.Messages;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;

public class CreateAnnotationProperty extends OWLModuleChangeCommand {

    public CreateAnnotationProperty(String project, String ontologyId, String propertyId, String superPropertyId) throws NeOnCoreException {
        super(project, ontologyId, propertyId, superPropertyId);
    }

    @Override
    public void doPerform() throws CommandException {
        String propertyId = (String)getArgument(2);
        String superPropertyId = (String) (getArgument(3) == null ? null : getArgument(3));

        try {
            OWLAnnotationProperty annotationProperty = getOwlModel().getOWLDataFactory().getOWLAnnotationProperty(OWLUtilities.toURI(propertyId));
            // OWLAnnotationProperty superAnnotationProperty = OWLUtilities.getAnnotationProperty(OWLUtilities.toURI(superPropertyId));

            if (superPropertyId != null && !superPropertyId.equals("")) { //$NON-NLS-1$
                throw new InternalNeOnException(Messages.getString("CreateAnnotationProperty.1")); //$NON-NLS-1$
                // SubAnnotationPropertyOf subProp = KAON2Manager.factory().subAnnotationPropertyOf(annotationProperty, superAnnotationProperty);
                // OWLModelFactory.getOWLModel(getOntologyId(), getProjectName()).addAxiom(subProp);
            } else {
                getOwlModel().addEntity(annotationProperty);
            }
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }

    }

}
