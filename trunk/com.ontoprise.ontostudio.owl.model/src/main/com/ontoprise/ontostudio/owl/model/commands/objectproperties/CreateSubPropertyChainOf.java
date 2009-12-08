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

import java.util.ArrayList;
import java.util.List;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import com.ontoprise.ontostudio.owl.model.OWLNamespaces;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;

public class CreateSubPropertyChainOf extends OWLModuleChangeCommand {

    public CreateSubPropertyChainOf(String project, String ontologyId, List<String> subPropertyChain, String superPropertyId) throws NeOnCoreException {
        super(project, ontologyId, subPropertyChain, superPropertyId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void doPerform() throws CommandException {
        List<String> subPropertyChain = (List<String>)getArgument(2);
        String superPropertyId = (String)getArgument(3);

        try {
            OWLNamespaces ns = getOwlModel().getNamespaces();
            OWLDataFactory factory = getOwlModel().getOWLDataFactory();
            List<OWLObjectProperty> chain = new ArrayList<OWLObjectProperty>();
            for (String p: subPropertyChain) {
                chain.add(OWLUtilities.objectProperty(p, ns, factory));
            }
            
            OWLObjectProperty superObjectProperty = OWLUtilities.objectProperty(superPropertyId, ns, factory);
            getOwlModel().addAxiom(factory.getOWLSubPropertyChainOfAxiom(chain, superObjectProperty));
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }

    }

}
