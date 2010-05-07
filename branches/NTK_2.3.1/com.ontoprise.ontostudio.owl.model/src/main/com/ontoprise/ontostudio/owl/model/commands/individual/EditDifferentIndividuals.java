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
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;

import com.ontoprise.ontostudio.owl.model.OWLNamespaces;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.ApplyChanges;
import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;
import com.ontoprise.ontostudio.owl.model.util.OWLAxiomUtils;

/**
 * @author werner
 * 
 */
public class EditDifferentIndividuals extends OWLModuleChangeCommand {

    /**
     * @param project
     * @param module
     * @param arguments
     * @throws NeOnCoreException
     */
    public EditDifferentIndividuals(String project, String module, String oldAxiomText, String entityUri, String newUri) throws NeOnCoreException {
        super(project, module, oldAxiomText, entityUri, newUri);
    }

    @Override
    public void doPerform() throws CommandException {
        String oldAxiomText = (String) getArgument(2);
        String entityUri = (String) getArgument(3);
        String newUri = (String) getArgument(4);

        try {
            OWLNamespaces namespaces = getOwlModel().getNamespaces();
            OWLDataFactory factory = getOwlModel().getOWLDataFactory();
            OWLDifferentIndividualsAxiom oldAxiom = (OWLDifferentIndividualsAxiom) OWLUtilities.axiom(oldAxiomText, namespaces, factory);
            OWLDifferentIndividualsAxiom newAxiom = OWLAxiomUtils.createNewDifferentFromAxiom(oldAxiom, entityUri, newUri, getProjectName());
            new ApplyChanges(getProjectName(), getOntology(), new String[] {OWLUtilities.toString(newAxiom)}, new String[] {OWLUtilities.toString(oldAxiom)}).perform();
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }
    }

}
