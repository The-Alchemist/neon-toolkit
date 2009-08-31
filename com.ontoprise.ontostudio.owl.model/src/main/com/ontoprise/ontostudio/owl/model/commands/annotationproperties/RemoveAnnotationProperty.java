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

import java.lang.reflect.InvocationTargetException;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;

import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;

public class RemoveAnnotationProperty extends OWLModuleChangeCommand {

    public RemoveAnnotationProperty(String project, String ontologyId, String propertyId, String superPropertyId) throws NeOnCoreException {
        super(project, ontologyId, propertyId, superPropertyId);
    }

    @Override
    public void doPerform() throws CommandException {
        try {
            String propertyId = (String)getArgument(2);

            OWLAnnotationProperty annotProperty = getOwlModel().getOWLDataFactory().getOWLAnnotationProperty(OWLUtilities.toURI(propertyId));
            getOwlModel().delEntity(annotProperty, null);
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        } catch (InterruptedException e) {
            throw new CommandException(e);
        } catch (InvocationTargetException e) {
            throw new CommandException(e);
        }

    }

}
