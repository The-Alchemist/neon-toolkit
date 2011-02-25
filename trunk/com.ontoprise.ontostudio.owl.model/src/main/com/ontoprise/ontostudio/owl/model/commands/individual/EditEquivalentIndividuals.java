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

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.util.IRIUtils;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;

import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.ApplyChanges;
import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;
import com.ontoprise.ontostudio.owl.model.util.OWLAxiomUtils;

/**
 * @author werner
 * @author Nico Stieler
 *
 */
public class EditEquivalentIndividuals extends OWLModuleChangeCommand {

    /**
     * @param project
     * @param module
     * @param arguments
     * @throws NeOnCoreException
     */
    public EditEquivalentIndividuals(String project, String module, String oldAxiomText, String individualToRemove, String individualToAdd) throws NeOnCoreException {
        super(project, module, oldAxiomText, individualToRemove, individualToAdd);
    }

    @Override
    public void doPerform() throws CommandException {
        String oldAxiomText = (String) getArgument(2);
        String entityUri = (String) getArgument(3);
        String newUri = (String) getArgument(4);

        try {
            OWLOntology ontology = getOwlModel().getOntology();
            OWLSameIndividualAxiom oldAxiom = (OWLSameIndividualAxiom) OWLUtilities.axiom(oldAxiomText, ontology);
            OWLSameIndividualAxiom newAxiom = OWLAxiomUtils.createNewSame_AsAxiom(oldAxiom, entityUri, newUri, getOntology(), getProjectName());
            new ApplyChanges(getProjectName(), getOntology(), new String[]{OWLUtilities.toString(newAxiom, ontology)}, new String[]{OWLUtilities.toString(oldAxiom, ontology)}).perform();
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }
    }

}
