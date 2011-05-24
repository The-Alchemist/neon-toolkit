/*****************************************************************************
 * Copyright (c) 2010 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands.datatypes;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.util.IRIUtils;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;

import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;
/**
 * 
 * @author Nico Stieler
 */
public class CreateSubDatatype extends OWLModuleChangeCommand {

    public CreateSubDatatype(String project, String ontologyId, String subId, String superId) throws NeOnCoreException {
        super(project, ontologyId, subId, superId);
    }

    @Override
    public void doPerform() throws CommandException {
        try {
            OWLDataFactory factory = getOwlModel().getOWLDataFactory();
            OWLDatatype subDatatype = (OWLDatatype)OWLUtilities.dataRange(getArgument(2).toString());
            OWLDataRange superDatatype = OWLUtilities.dataRange(getArgument(3).toString());
            OWLDatatypeDefinitionAxiom axiom = factory.getOWLDatatypeDefinitionAxiom(subDatatype, superDatatype);
            getOwlModel().addAxiom(axiom);
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }
    }
}
