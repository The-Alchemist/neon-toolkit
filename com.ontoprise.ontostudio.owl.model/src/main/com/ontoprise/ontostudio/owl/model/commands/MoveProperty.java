/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.InformationAlreadyExistsException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;

import com.ontoprise.ontostudio.owl.model.Messages;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;

public abstract class MoveProperty extends OWLModuleChangeCommand {

    public MoveProperty(String project, String ontologyId, String propertyId, String oldParentId, String newParentId) throws NeOnCoreException {
        super(project, ontologyId, propertyId, oldParentId, newParentId);
    }

    @Override
    public void doPerform() throws CommandException {
        String propertyId = (String)getArgument(2);
        String oldParentId = getArgument(3) != null ? (String)getArgument(3) : null;
        String newParentId = getArgument(4) != null ? (String)getArgument(4) : null;

        try {
            if (newParentId != null) {
                // add a subproperty
                if (propertyId.equals(newParentId)) {
                    // property cannot be moved on itself
                    throw new CommandException(Messages.getString("MoveProperty.0")); //$NON-NLS-1$
                }
                if (oldParentId != null && oldParentId.equals(newParentId)) {
                    // no move operation has to be performed
                    return;
                }
                createProperty(propertyId, newParentId);
                if (oldParentId != null) {
                    removeSubProperty(propertyId, oldParentId);
                } else {
                    // this means a property from root level has been moved, so we have to remove the declaration
                    // so that the respective event removes the tree item.
                    try {
                        OWLDataFactory factory = OWLModelFactory.getOWLDataFactory(getProjectName());
                        OWLDeclarationAxiom decl = factory.getOWLDeclarationAxiom(getEntity(propertyId));
                        getOwlModel().removeAxiom(decl);
                    } catch (NeOnCoreException e) {
                        // nothing to do
                    }
                }
            } else {
                // newParentId == null =>> add a rootProperty
                try {
                    createProperty(propertyId, null); 
                } catch (InformationAlreadyExistsException iae) {
                    if (isRootProperty(getProjectName(), propertyId)) {
                        // nothing to do
                    } else {
                        createProperty(propertyId, oldParentId);
                        throw new CommandException(""); //$NON-NLS-1$
                    }
                }
                if (oldParentId != null) {
                    removeSubProperty(propertyId, oldParentId);
                }
            }
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }
    }

    protected abstract void removeSubProperty(String propertyId, String parentId) throws CommandException,NeOnCoreException;

    protected abstract void createProperty(String propertyId, String parentId) throws CommandException,NeOnCoreException;

    protected abstract OWLEntity getEntity(String propertyid) throws CommandException;

    /**
     * @param id
     * @param ontologyId
     * @return
     * @throws NeOnCoreException
     */
    protected abstract boolean isRootProperty(String projectId, String id) throws NeOnCoreException;
}
