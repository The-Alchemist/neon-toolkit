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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;

import com.ontoprise.ontostudio.owl.model.LocatedItem;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.OWLOntologyRequestCommand;

/**
 * @author werner
 * @author Nico Stieler
 * 
 */
public class GetObjectPropertyMemberHits extends OWLOntologyRequestCommand {

    List<String[]> _results;

    /**
     * @param project
     * @param module
     * @param arguments
     */
    public GetObjectPropertyMemberHits(String project, String module, String individualUri) {
        super(project, module, individualUri);
    }

    @Override
    protected void perform() throws CommandException {
        String individualUri = (String) getArgument(2);
        _results = new ArrayList<String[]>();

        try {
            Set<LocatedItem<OWLObjectPropertyAssertionAxiom>> objMem = getOwlModel().getObjectPropertyMemberHits(individualUri);
            for (LocatedItem<OWLObjectPropertyAssertionAxiom> item: objMem) {
                _results.add(new String[] {OWLUtilities.toString(item.getItem()), item.getOntologyURI()});
            }
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }

    }

    public String[][] getResults() throws CommandException {
        if (_results == null) {
            perform();
        }
        return _results.toArray(new String[_results.size()][2]);
    }
}
