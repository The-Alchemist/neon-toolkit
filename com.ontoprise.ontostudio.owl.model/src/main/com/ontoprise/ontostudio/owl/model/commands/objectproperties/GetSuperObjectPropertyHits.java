/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands.objectproperties;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;

import com.ontoprise.ontostudio.owl.model.ItemHits;
import com.ontoprise.ontostudio.owl.model.LocatedItem;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.OWLOntologyRequestCommand;

/**
 * @author werner
 * @author Nico Stieler
 * 
 */
public class GetSuperObjectPropertyHits extends OWLOntologyRequestCommand {

    private List<String[]> _results;

    /**
     * @param project
     * @param module
     * @param arguments
     */
    public GetSuperObjectPropertyHits(String project, String module, String propertyUri) {
        super(project, module, propertyUri);
    }

    @Override
    protected void perform() throws CommandException {
        _results = new ArrayList<String[]>();
        String propertyUri = (String) getArgument(2);

        Set<ItemHits<OWLObjectPropertyExpression,OWLSubObjectPropertyOfAxiom>> results = new HashSet<ItemHits<OWLObjectPropertyExpression,OWLSubObjectPropertyOfAxiom>>();
        try {
            OWLOntology ontology = getOwlModel().getOntology();
            results = getOwlModel().getSuperObjectPropertyHits(propertyUri);
            for (ItemHits<OWLObjectPropertyExpression,OWLSubObjectPropertyOfAxiom> hit: results) {
                Set<LocatedItem<OWLSubObjectPropertyOfAxiom>> axioms = hit.getAxioms();
                for (LocatedItem<OWLSubObjectPropertyOfAxiom> axiom: axioms) {
                    _results.add(new String[] {OWLUtilities.toString(axiom.getItem(), ontology), axiom.getOntologyURI()});
                }
            }
        } catch (NeOnCoreException e1) {
            throw new CommandException(e1);
        }
    }

    public String[][] getResults() throws CommandException {
        if (_results == null) {
            perform();
        }
        return _results.toArray(new String[_results.size()][2]);
    }
}
