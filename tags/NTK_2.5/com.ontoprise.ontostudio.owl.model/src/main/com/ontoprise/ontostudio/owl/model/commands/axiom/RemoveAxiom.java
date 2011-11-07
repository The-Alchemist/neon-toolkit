/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands.axiom;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLAxiom;

import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;
/**
 * 
 * @author Nico Stieler
 */
public class RemoveAxiom extends OWLModuleChangeCommand {

    public RemoveAxiom(String project, String ontologyId, String axiomText) throws NeOnCoreException {
        super(project, ontologyId, axiomText);
    }

    @Override
    public void doPerform() throws CommandException {
        String axiomText = (String) getArgument(2);

        try {
            OWLModel owlModel = OWLModelFactory.getOWLModel(getOntology(), getProjectName());
            OWLAxiom axiom = (OWLAxiom) OWLUtilities.axiom(axiomText);
            owlModel.removeAxiom(axiom);
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }
    }

}
