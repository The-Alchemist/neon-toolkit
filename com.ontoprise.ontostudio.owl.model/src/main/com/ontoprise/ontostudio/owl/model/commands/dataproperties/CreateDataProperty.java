/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands.dataproperties;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;

import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLNamespaces;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;

public class CreateDataProperty extends OWLModuleChangeCommand {

    public CreateDataProperty(String project, String ontologyId, String propertyId, String superPropertyId) throws NeOnCoreException {
        super(project, ontologyId, propertyId, superPropertyId);
    }

    @Override
    public void doPerform() throws CommandException {
        String propertyId = getArgument(2).toString();
        String superPropertyId = (String) (getArgument(3) == null ? null : getArgument(3));

        try {
            OWLDataFactory factory = OWLModelFactory.getOWLDataFactory(getProjectName());
            OWLDataProperty dataProperty = factory.getOWLDataProperty(OWLUtilities.toURI(propertyId));

            if (superPropertyId != null) {
                OWLNamespaces ns = getOwlModel().getNamespaces();
                getOwlModel().addAxiom(factory.getOWLSubDataPropertyOfAxiom(dataProperty, OWLUtilities.dataProperty(superPropertyId, ns, factory)));
            } else {
                getOwlModel().addEntity(dataProperty);
            }
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }

    }

}
