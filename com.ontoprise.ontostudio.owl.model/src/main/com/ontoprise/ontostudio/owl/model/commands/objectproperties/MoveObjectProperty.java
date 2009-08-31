/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands.objectproperties;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.MoveProperty;

public class MoveObjectProperty extends MoveProperty {

    public MoveObjectProperty(String project, String ontologyId, String propertyId, String oldParentId, String newParentId) throws NeOnCoreException {
        super(project, ontologyId, propertyId, oldParentId, newParentId);
    }

    @Override
    protected void createProperty(String propertyId, String parentId) throws CommandException, NeOnCoreException {
        new CreateObjectProperty(getProjectName(), getOntology(), propertyId, parentId).perform();
    }

    @Override
    protected void removeSubProperty(String propertyId, String parentId) throws CommandException, NeOnCoreException {
        new RemoveObjectProperty(getProjectName(), getOntology(), propertyId, parentId).perform();
    }

    @Override
    protected OWLEntity getEntity(String propertyId) throws CommandException {
        try {
            return OWLModelFactory.getOWLDataFactory(getProjectName()).getOWLObjectProperty(OWLUtilities.toURI(propertyId));
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }
    }

    @Override
    protected boolean isRootProperty(String projectId, String id) throws NeOnCoreException  {
        OWLObjectProperty prop = OWLModelFactory.getOWLDataFactory(projectId).getOWLObjectProperty(OWLUtilities.toURI(id));
        return getOwlModel().isRootObjectProperty(prop);
    }
}
