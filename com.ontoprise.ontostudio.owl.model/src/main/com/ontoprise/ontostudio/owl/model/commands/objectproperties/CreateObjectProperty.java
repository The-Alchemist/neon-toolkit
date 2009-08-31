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
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import com.ontoprise.ontostudio.owl.model.OWLNamespaces;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;

public class CreateObjectProperty extends OWLModuleChangeCommand {

    public CreateObjectProperty(String project, String ontologyId, String propertyId, String superPropertyId) throws NeOnCoreException {
        super(project, ontologyId, propertyId, superPropertyId);
    }

    @Override
    public void doPerform() throws CommandException {
        String propertyId = getArgument(2).toString();
        String superPropertyId = (String) (getArgument(3) == null ? null : getArgument(3));

        try {
            OWLNamespaces ns = getOwlModel().getNamespaces();
            OWLDataFactory factory = getOwlModel().getOWLDataFactory();
            OWLObjectProperty objectProperty = OWLUtilities.objectProperty(propertyId, ns, factory);
            
            if (superPropertyId != null) { 
                OWLObjectProperty superObjectProperty = OWLUtilities.objectProperty(superPropertyId, ns, factory);
                getOwlModel().addAxiom(factory.getOWLSubObjectPropertyOfAxiom(objectProperty, superObjectProperty));
            } else {
                getOwlModel().addEntity(objectProperty);
            }
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }

    }

}
