/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 28.01.2009
 * Created by: werner
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.model.commands.objectproperties;

import java.util.Arrays;
import java.util.LinkedHashSet;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

import com.ontoprise.ontostudio.owl.model.OWLNamespaces;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.ApplyChanges;
import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;

/**
 * @author werner
 * 
 */
public class CreateEquivalentObjectProperty extends OWLModuleChangeCommand {

    /**
     * @param project
     * @param module
     * @param arguments
     * @throws NeOnCoreException
     */
    public CreateEquivalentObjectProperty(String project, String module, String propertyUri, String equivalentPropertyUri) throws NeOnCoreException {
        super(project, module, propertyUri, equivalentPropertyUri);
    }

    @Override
    protected void doPerform() throws CommandException {
        try {
            OWLNamespaces ns = getOwlModel().getNamespaces();
            OWLDataFactory factory = getOwlModel().getOWLDataFactory();
            
            OWLObjectPropertyExpression propertyUri = OWLUtilities.objectProperty((String) getArgument(2), ns, factory);
            OWLObjectPropertyExpression equivalentPropertyUri = OWLUtilities.objectProperty((String) getArgument(3), ns, factory);

            new ApplyChanges(getProjectName(), getOntology(), new OWLAxiom[] {factory.getOWLEquivalentObjectPropertiesAxiom(new LinkedHashSet<OWLObjectPropertyExpression>(Arrays.asList(propertyUri, equivalentPropertyUri)))}, new OWLAxiom[0]).perform();
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }
    }

}
