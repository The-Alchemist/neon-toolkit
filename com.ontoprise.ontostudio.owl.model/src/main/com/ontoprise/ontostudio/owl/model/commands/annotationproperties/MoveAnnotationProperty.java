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
import org.semanticweb.owlapi.model.OWLEntity;

import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.MoveProperty;

public class MoveAnnotationProperty extends MoveProperty {

    public MoveAnnotationProperty(String project, String ontologyId, String propertyId, String oldParentId, String newParentId) throws NeOnCoreException {
        super(project, ontologyId, propertyId, oldParentId, newParentId);
    }

    @Override
    protected void createProperty(String propertyId, String parentId) throws CommandException, NeOnCoreException {
        new CreateAnnotationProperty(getProjectName(), getOntology(), propertyId, parentId).perform();
    }

    @Override
    protected void removeSubProperty(String propertyId, String parentId) throws CommandException, NeOnCoreException {
        new RemoveAnnotationProperty(getProjectName(), getOntology(), propertyId, parentId).perform();
    }

    @Override
    protected OWLEntity getEntity(String propertyId) throws CommandException {
        try {
            return getOwlModel().getOWLDataFactory().getOWLAnnotationProperty(OWLUtilities.toURI(propertyId));
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }
    }

    @Override
    protected boolean isRootProperty(String projectId, String id) throws NeOnCoreException {
        return true;
    }
}
