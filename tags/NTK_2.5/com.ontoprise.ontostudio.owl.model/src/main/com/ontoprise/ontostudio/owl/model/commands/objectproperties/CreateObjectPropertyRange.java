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

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.util.IRIUtils;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.ApplyChanges;
import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;

/**
 * @author werner
 * @author Nico Stieler
 *
 */
public class CreateObjectPropertyRange extends OWLModuleChangeCommand {

    /**
     * @param project
     * @param module
     * @param arguments
     * @throws NeOnCoreException
     */
    public CreateObjectPropertyRange(String project, String module, String propertyUri, String range) throws NeOnCoreException {
        super(project, module, propertyUri, range);
    }

    @Override
    protected void doPerform() throws CommandException {
        try {
            OWLDataFactory factory = getOwlModel().getOWLDataFactory();
            OWLObjectProperty propertyUri = OWLUtilities.objectProperty(IRIUtils.ensureValidIRISyntax((String) getArgument(2)));
            OWLClassExpression range = OWLUtilities.description((String) getArgument(3));
            new ApplyChanges(getProjectName(), getOntology(), 
                    new OWLAxiom[]{factory.getOWLObjectPropertyRangeAxiom(propertyUri, range)}, 
                    new OWLAxiom[0]).perform();
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }

    }

}
