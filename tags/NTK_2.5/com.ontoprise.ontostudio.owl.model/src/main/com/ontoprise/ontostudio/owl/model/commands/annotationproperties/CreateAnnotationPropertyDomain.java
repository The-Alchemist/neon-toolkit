/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands.annotationproperties;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.util.IRIUtils;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;

import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.ApplyChanges;
import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;

/**
 * @author Michael
 * @author Nico Stieler
 *
 */
public class CreateAnnotationPropertyDomain extends OWLModuleChangeCommand {

    /**
     * @param project
     * @param module
     * @param arguments
     * @throws NeOnCoreException
     */
    public CreateAnnotationPropertyDomain(String project, String module, String propertyUri, String domain) throws NeOnCoreException {
        super(project, module, propertyUri, domain);
    }

    @Override
    protected void doPerform() throws CommandException {
        String propertyUri = (String) getArgument(2);
        String domain = (String) getArgument(3);
        try {
            OWLDataFactory factory = getOwlModel().getOWLDataFactory();
            IRI iri = OWLUtilities.toIRI(domain);
            new ApplyChanges(getProjectName(), getOntology(), 
                    new OWLAxiom[]{factory.getOWLAnnotationPropertyDomainAxiom(
                            OWLUtilities.annotationProperty(IRIUtils.ensureValidIRISyntax(propertyUri)), 
                            iri)}, 
                    new OWLAxiom[0]).perform();
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }

    }

}
