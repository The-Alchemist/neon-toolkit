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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;

import com.ontoprise.ontostudio.owl.model.LocatedItem;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.OWLOntologyRequestCommand;

/**
 * @author Michael Erdmann
 * 
 */
public class GetDataPropertyMemberHitsForProperty extends OWLOntologyRequestCommand {

    List<String[]> _results;

    /**
     * @param project
     * @param ontology
     * @param arguments
     */
    public GetDataPropertyMemberHitsForProperty(String project, String ontology, OWLDataProperty property) {
        super(project, ontology, property);
    }

    @Override
    protected void perform() throws CommandException {
        OWLDataProperty property = (OWLDataProperty) getArgument(2);
        _results = new ArrayList<String[]>();

        try {
            Set<LocatedItem<OWLDataPropertyAssertionAxiom>> objMem = getOwlModel().getDataPropertyMemberHitsForProperty(property);
            for (LocatedItem<OWLDataPropertyAssertionAxiom> item: objMem) {
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
