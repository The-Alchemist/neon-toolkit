/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 23.01.2009
 * Created by: werner
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.model.commands.clazz;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;

import com.ontoprise.ontostudio.owl.model.ItemHits;
import com.ontoprise.ontostudio.owl.model.LocatedItem;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.OWLOntologyRequestCommand;

/**
 * @author werner
 *
 */
public class GetDisjointClazzHits extends OWLOntologyRequestCommand {

    List<String[]> _results;
    
    /**
     * @param project
     * @param module
     * @param arguments
     */
    public GetDisjointClazzHits(String project, String module, String clazzUri) {
        super(project, module, clazzUri);
    }

    @Override
    protected void perform() throws CommandException {
        _results = new ArrayList<String[]>();
        String clazzUri = (String) getArgument(2);

        try {
            Set<ItemHits<OWLClassExpression,OWLDisjointClassesAxiom>> equivalentClassHits = getOwlModel().getDisjointDescriptionHits(clazzUri);
            for (ItemHits<OWLClassExpression,OWLDisjointClassesAxiom> hit: equivalentClassHits) {
                Set<LocatedItem<OWLDisjointClassesAxiom>> axioms = hit.getAxioms();
                for (LocatedItem<OWLDisjointClassesAxiom> axiom: axioms) {
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
