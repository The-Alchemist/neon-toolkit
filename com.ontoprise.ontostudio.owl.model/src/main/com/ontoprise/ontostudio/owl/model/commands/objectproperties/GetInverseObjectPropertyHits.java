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
import java.util.List;
import java.util.Set;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
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
public class GetInverseObjectPropertyHits extends OWLOntologyRequestCommand {

    private List<String[]> _results;

    /**
     * @param project
     * @param module
     * @param arguments
     */
    public GetInverseObjectPropertyHits(String project, String module, String propertyUri) {
        super(project, module, propertyUri);
    }

    @Override
    protected void perform() throws CommandException {
        _results = new ArrayList<String[]>();
        String propertyUri = (String) getArgument(2);
        try {
            OWLOntology ontology = getOwlModel().getOntology();
            Set<ItemHits<OWLClassExpression,OWLInverseObjectPropertiesAxiom>> results = getOwlModel().getInverseObjectPropertyHits(propertyUri);
            for (ItemHits<OWLClassExpression,OWLInverseObjectPropertiesAxiom> hit: results) {
                Set<LocatedItem<OWLInverseObjectPropertiesAxiom>> axioms = hit.getAxioms();
                for (LocatedItem<OWLInverseObjectPropertiesAxiom> axiom: axioms) {
                    String[] array = new String[] {OWLUtilities.toString(axiom.getItem(), ontology), axiom.getOntologyURI()};
                    if (!_results.contains(array)) {
                        _results.add(array);
                    }
                }
            }
        } catch (NeOnCoreException e1) {
            new NeonToolkitExceptionHandler().handleException(e1);
        }
    }

    public String[][] getResults() throws CommandException {
        if (_results == null) {
            perform();
        }
        return _results.toArray(new String[_results.size()][2]);
    }
}
