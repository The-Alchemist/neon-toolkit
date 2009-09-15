/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands.individual;

import java.util.Arrays;
import java.util.LinkedHashSet;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;

import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;

/**
 * @author werner
 * 
 */
public class CreateSameIndividuals extends OWLModuleChangeCommand {

    /**
     * @param project
     * @param module
     * @param arguments
     * @throws NeOnCoreException
     */
    public CreateSameIndividuals(String project, String module, String[] individualUris) throws NeOnCoreException {
        super(project, module, (Object) individualUris);
    }

    @Override
    public void doPerform() throws CommandException {
        String[] individualUris = (String[]) getArgument(2);

        try {
            OWLDataFactory factory = OWLModelFactory.getOWLDataFactory(getProjectName());
            OWLIndividual[] individuals = new OWLIndividual[individualUris.length];
            int i = 0;
            for (String uri: individualUris) {
                individuals[i] = factory.getOWLNamedIndividual(OWLUtilities.toURI(uri));
                i++;
            }
            OWLAxiom a = factory.getOWLSameIndividualAxiom(new LinkedHashSet<OWLIndividual>(Arrays.asList(individuals)));

            getOwlModel().addAxiom(a);
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }
    }

}
