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
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import com.ontoprise.ontostudio.owl.model.ItemHits;
import com.ontoprise.ontostudio.owl.model.LocatedItem;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.OWLOntologyRequestCommand;

/**
 * @author werner
 * @author Nico Stieler
 *
 */
public class GetEquivalentRestrictionHits extends OWLOntologyRequestCommand {

    List<String[]> _results;
    
    /**
     * @param project
     * @param module
     * @param arguments
     */
    public GetEquivalentRestrictionHits(String project, String module, String superClazzUri) {
        super(project, module, superClazzUri);
    }

    @Override
    public void perform() throws CommandException {
        _results = new ArrayList<String[]>();
        String superClazzUri = (String) getArgument(2);
        try {
            OWLOntology ontology = getOwlModel().getOntology();
            Set<ItemHits<OWLClassExpression,OWLEquivalentClassesAxiom>> superRestrictionHits = getOwlModel().getEquivalentRestrictionHits(superClazzUri);
            for (ItemHits<OWLClassExpression,OWLEquivalentClassesAxiom> hit: superRestrictionHits) {
                Set<LocatedItem<OWLEquivalentClassesAxiom>> axioms = hit.getAxioms();
                for (LocatedItem<OWLEquivalentClassesAxiom> axiom: axioms) {
                    String[] result = new String[]{OWLUtilities.toString(axiom.getItem(), ontology), axiom.getOntologyURI()};
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
