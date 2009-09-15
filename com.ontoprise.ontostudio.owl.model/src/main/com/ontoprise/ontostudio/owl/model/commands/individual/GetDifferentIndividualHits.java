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
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;

import com.ontoprise.ontostudio.owl.model.ItemHits;
import com.ontoprise.ontostudio.owl.model.LocatedItem;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.OWLOntologyRequestCommand;

/**
 * @author werner
 *
 */
public class GetDifferentIndividualHits extends OWLOntologyRequestCommand {

    List<String[]> _results;
    
    /**
     * @param project
     * @param module
     * @param arguments
     */
    public GetDifferentIndividualHits(String project, String module, String individualUri) {
        super(project, module, individualUri);
    }

    @Override
    public void perform() throws CommandException {
        _results = new ArrayList<String[]>();
        String individualUri = (String) getArgument(2);
        try {
            Set<ItemHits<OWLIndividual,OWLDifferentIndividualsAxiom>> differentIndividualHits = getOwlModel().getDifferentIndividualHits(individualUri);
            for (ItemHits<OWLIndividual,OWLDifferentIndividualsAxiom> hit: differentIndividualHits) {
                Set<LocatedItem<OWLDifferentIndividualsAxiom>> axioms = hit.getAxioms();
                for (LocatedItem<OWLDifferentIndividualsAxiom> axiom: axioms) {
                    String[] result = new String[]{OWLUtilities.toString(axiom.getItem()), axiom.getOntologyURI()};
                    _results.add(result);
                }
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
