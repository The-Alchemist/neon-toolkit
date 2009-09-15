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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;

import com.ontoprise.ontostudio.owl.model.ItemHits;
import com.ontoprise.ontostudio.owl.model.LocatedItem;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.OWLOntologyRequestCommand;

/**
 * @author werner
 * 
 */
public class GetEquivalentClazzHits extends OWLOntologyRequestCommand {

    List<String[]> _results;

    /**
     * @param project
     * @param module
     * @param arguments
     */
    public GetEquivalentClazzHits(String project, String module, String clazzUri) {
        super(project, module, clazzUri);
    }

    @Override
    protected void perform() throws CommandException {
        String clazzUri = (String) getArgument(2);
        _results = new ArrayList<String[]>();

        try {
            Set<ItemHits<?,OWLEquivalentClassesAxiom>> descriptions = new HashSet<ItemHits<?,OWLEquivalentClassesAxiom>>();
            Set<ItemHits<OWLClassExpression,OWLEquivalentClassesAxiom>> equivalentDescriptions = getOwlModel().getEquivalentDescriptionHits(clazzUri);
            Set<ItemHits<OWLClass,OWLEquivalentClassesAxiom>> equivalentClasses = getOwlModel().getEquivalentClassesHits(clazzUri);
            descriptions.addAll(equivalentDescriptions);
            descriptions.addAll(equivalentClasses);
            for (ItemHits<?,OWLEquivalentClassesAxiom> hit: descriptions) {
                Set<LocatedItem<OWLEquivalentClassesAxiom>> axioms = hit.getAxioms();
                for (LocatedItem<OWLEquivalentClassesAxiom> axiom: axioms) {
                    String[] result = new String[] {OWLUtilities.toString(axiom.getItem()), axiom.getOntologyURI()};
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
