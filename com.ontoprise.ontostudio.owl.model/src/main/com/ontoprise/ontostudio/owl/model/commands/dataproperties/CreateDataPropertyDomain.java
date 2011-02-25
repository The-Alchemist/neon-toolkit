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
import org.neontoolkit.core.util.IRIUtils;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;

import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.ApplyChanges;
import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;

/**
 * @author werner
 * @author Nico Stieler
 *
 */
public class CreateDataPropertyDomain extends OWLModuleChangeCommand {

    /**
     * @param project
     * @param module
     * @param arguments
     * @throws NeOnCoreException
     */
    public CreateDataPropertyDomain(String project, String module, String propertyUri, String domain) throws NeOnCoreException {
        super(project, module, propertyUri, domain);
    }

    @Override
    protected void doPerform() throws CommandException {
        String propertyUri = (String) getArgument(2);
        String domain = (String) getArgument(3);
        try {
            OWLOntology ontology = getOwlModel().getOntology();
            OWLDataFactory factory = getOwlModel().getOWLDataFactory();
            OWLClassExpression desc = OWLUtilities.description(domain, ontology);
            new ApplyChanges(getProjectName(), getOntology(), 
                    new OWLAxiom[]{factory.getOWLDataPropertyDomainAxiom(
                            OWLUtilities.dataProperty(IRIUtils.ensureValidIRISyntax(propertyUri), ontology), 
                            desc)}, 
                    new OWLAxiom[0]).perform();
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }

    }

}
