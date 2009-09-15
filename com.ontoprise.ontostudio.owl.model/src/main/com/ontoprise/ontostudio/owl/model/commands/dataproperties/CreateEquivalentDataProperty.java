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

import java.util.Arrays;
import java.util.LinkedHashSet;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;

import com.ontoprise.ontostudio.owl.model.OWLNamespaces;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.ApplyChanges;
import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;
import com.ontoprise.ontostudio.owl.model.util.Cast;

/**
 * @author werner
 * 
 */
public class CreateEquivalentDataProperty extends OWLModuleChangeCommand {

    /**
     * @param project
     * @param module
     * @param arguments
     * @throws NeOnCoreException
     */
    public CreateEquivalentDataProperty(String project, String module, String propertyUri1, String propertyUri2) throws NeOnCoreException {
        super(project, module, propertyUri1, propertyUri2);
    }

    @Override
    protected void doPerform() throws CommandException {
        String propertyUri1 = (String) getArgument(2);
        String propertyUri2 = (String) getArgument(3);

        try {
            OWLNamespaces ns = getOwlModel().getNamespaces();
            OWLDataFactory factory = getOwlModel().getOWLDataFactory();
            new ApplyChanges(getProjectName(), getOntology(), new OWLAxiom[] {Cast.cast(factory.getOWLEquivalentDataPropertiesAxiom(new LinkedHashSet<OWLDataPropertyExpression>(Arrays.asList(OWLUtilities.dataProperty(propertyUri1, ns, factory), OWLUtilities.dataProperty(propertyUri2, ns, factory)))))}, new OWLAxiom[0]).perform();
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }

    }

}
