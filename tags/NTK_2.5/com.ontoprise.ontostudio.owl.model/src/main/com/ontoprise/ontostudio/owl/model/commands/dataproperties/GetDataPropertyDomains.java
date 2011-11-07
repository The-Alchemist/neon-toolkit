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
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;

import com.ontoprise.ontostudio.owl.model.ItemHits;
import com.ontoprise.ontostudio.owl.model.LocatedItem;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.OWLOntologyRequestCommand;

/**
 * @author werner
 * @author Nico Stieler
 *
 */
public class GetDataPropertyDomains extends OWLOntologyRequestCommand {

    private List<String[]> _results;

    /**
     * @param project
     * @param ontology
     * @param arguments
     */
    public GetDataPropertyDomains(String project, String ontology, String propertyUri) {
        super(project, ontology, propertyUri);
    }

    @Override
    protected void perform() throws CommandException {
        _results = new ArrayList<String[]>();
        String propertyUri = (String) getArgument(2);

        try {
            Set<ItemHits<OWLClassExpression,OWLDataPropertyDomainAxiom>> clazzDescriptions = getOwlModel().getDataPropertyDomainHits(propertyUri);
            for (ItemHits<OWLClassExpression,OWLDataPropertyDomainAxiom> hit: clazzDescriptions) {
                Set<LocatedItem<OWLDataPropertyDomainAxiom>> axioms = hit.getAxioms();
                for (LocatedItem<OWLDataPropertyDomainAxiom> item: axioms) {
                    _results.add(new String[]{OWLUtilities.toString(item.getItem()), item.getOntologyURI()});
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
