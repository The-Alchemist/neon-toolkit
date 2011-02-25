/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands.clazz;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import com.ontoprise.ontostudio.owl.model.ItemHits;
import com.ontoprise.ontostudio.owl.model.LocatedItem;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.OWLOntologyRequestCommand;

/**
 * @author werner
 * @author Nico Stieler
 * 
 */
public class GetSuperDescriptionHits extends OWLOntologyRequestCommand {

    List<String[]> _results;

    /**
     * @param project
     * @param module
     * @param arguments
     */
    public GetSuperDescriptionHits(String project, String module, String subClazzUri) {
        super(project, module, subClazzUri);
    }

    @Override
    public void perform() throws CommandException {
        _results = new ArrayList<String[]>();
        String subClazzUri = (String) getArgument(2);
        try {
            OWLOntology ontology = getOwlModel().getOntology();
            Set<ItemHits<OWLClassExpression,OWLSubClassOfAxiom>> superDescriptionHits = getOwlModel().getSuperDescriptionHits(subClazzUri);
            for (ItemHits<OWLClassExpression,OWLSubClassOfAxiom> hit: superDescriptionHits) {
                Set<LocatedItem<OWLSubClassOfAxiom>> axioms = hit.getAxioms();
                for (LocatedItem<OWLSubClassOfAxiom> axiom: axioms) {
                    String[] result = new String[] {OWLUtilities.toString(axiom.getItem(), ontology), axiom.getOntologyURI()};
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
