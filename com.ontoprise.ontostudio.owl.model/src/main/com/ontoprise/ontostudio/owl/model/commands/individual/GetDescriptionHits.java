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
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;

import com.ontoprise.ontostudio.owl.model.ItemHits;
import com.ontoprise.ontostudio.owl.model.LocatedItem;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.OWLOntologyRequestCommand;

/**
 * @author werner
 *
 */
public class GetDescriptionHits extends OWLOntologyRequestCommand {

    private List<String[]> _results;
    /**
     * @param project
     * @param module
     * @param arguments
     */
    public GetDescriptionHits(String project, String module, String individualUri) {
        super(project, module, individualUri);
    }

    @Override
    protected void perform() throws CommandException {
        _results = new ArrayList<String[]>();
        String individualUri = (String) getArgument(2);
        try {
            Set<ItemHits<OWLClassExpression,OWLClassAssertionAxiom>> descriptionHits = getOwlModel().getDescriptionHits(individualUri);
            Set<ItemHits<OWLClassExpression,OWLClassAssertionAxiom>> clazzHits = getOwlModel().getClassHits(individualUri);
            descriptionHits.addAll(clazzHits);
            for (ItemHits<OWLClassExpression, OWLClassAssertionAxiom> hit: descriptionHits) {
                Set<LocatedItem<OWLClassAssertionAxiom>> axioms = hit.getAxioms();
                for (LocatedItem<OWLClassAssertionAxiom> axiom: axioms) {
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
