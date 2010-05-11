/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands.objectproperties;

import java.lang.reflect.InvocationTargetException;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLNamespaces;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.ApplyChanges;
import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;

public class RemoveObjectProperty extends OWLModuleChangeCommand {

    /**
     * Only used from {@link MoveObjectProperty}. Removing ObjectProperties is realized by 
     * {@link ApplyChanges}. 
     * @param project
     * @param ontologyId
     * @param propertyId
     * @param superPropertyId
     * @throws NeOnCoreException 
     */
    public RemoveObjectProperty(String project, String ontologyId, String propertyId, String superPropertyId) throws NeOnCoreException {
        super(project, ontologyId, propertyId, superPropertyId);
    }

    @Override
    public void doPerform() throws CommandException {
        String subPropertyId = getArgument(2).toString();
        String superPropertyId = getArgument(3) != null ? getArgument(3).toString() : null;

        try {
            OWLNamespaces ns = getOwlModel().getNamespaces();
            OWLDataFactory factory = getOwlModel().getOWLDataFactory();

            if (superPropertyId == null) {
                // FIXME this should never be used, would lead to a loss of data as delEntity removes all axioms containing that entity
                OWLObjectProperty objectProperty = OWLModelFactory.getOWLDataFactory(getProjectName()).getOWLObjectProperty(OWLUtilities.toIRI(subPropertyId));
                getOwlModel().delEntity(objectProperty, null);
            } else {
                getOwlModel().removeAxiom(factory.getOWLSubObjectPropertyOfAxiom(OWLUtilities.objectProperty(subPropertyId, ns, factory), OWLUtilities.objectProperty(superPropertyId, ns, factory)));
            }

        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        } catch (InterruptedException e) {
            throw new CommandException(e);
        } catch (InvocationTargetException e) {
            throw new CommandException(e);
        }
    }

}
