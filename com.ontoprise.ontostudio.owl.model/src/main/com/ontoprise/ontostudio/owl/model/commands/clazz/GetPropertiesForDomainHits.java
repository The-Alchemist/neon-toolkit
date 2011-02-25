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
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import com.ontoprise.ontostudio.owl.model.ItemHits;
import com.ontoprise.ontostudio.owl.model.LocatedItem;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.OWLOntologyRequestCommand;

/**
 * @author Michael
 * @author Nico Stieler
 * 
 * Returns the properties that have a given class as their domain.
 */
public class GetPropertiesForDomainHits extends OWLOntologyRequestCommand {

    List<String[]> _results;

    /**
     * @param project
     * @param module
     * @param arguments
     */
    public GetPropertiesForDomainHits(String project, String module, String classUri) {
        super(project, module, classUri);
    }

    @Override
    public void perform() throws CommandException {
        _results = new ArrayList<String[]>();
        String classUri = (String) getArgument(2);
        try {
            OWLOntology ontology = getOwlModel().getOntology();
            Set<ItemHits<OWLDataProperty,OWLDataPropertyDomainAxiom>> dataPropertyHits = getOwlModel().getDataPropertiesForDomainHits(classUri);
            for (ItemHits<OWLDataProperty,OWLDataPropertyDomainAxiom> hit: dataPropertyHits) {
                Set<LocatedItem<OWLDataPropertyDomainAxiom>> axioms = hit.getAxioms();
                for (LocatedItem<OWLDataPropertyDomainAxiom> axiom: axioms) {
                    String[] result = new String[]{OWLUtilities.toString(axiom.getItem(), ontology), axiom.getOntologyURI()};
                    _results.add(result);
                }
            }

            Set<ItemHits<OWLObjectProperty,OWLObjectPropertyDomainAxiom>> objectPropertyHits = getOwlModel().getObjectPropertiesForDomainHits(classUri);
            for (ItemHits<OWLObjectProperty,OWLObjectPropertyDomainAxiom> hit: objectPropertyHits) {
                Set<LocatedItem<OWLObjectPropertyDomainAxiom>> axioms = hit.getAxioms();
                for (LocatedItem<OWLObjectPropertyDomainAxiom> axiom: axioms) {
                    String[] result = new String[]{OWLUtilities.toString(axiom.getItem(), ontology), axiom.getOntologyURI()};
                    _results.add(result);
                }
            }

            Set<ItemHits<OWLAnnotationProperty,OWLAnnotationPropertyDomainAxiom>> annotationPropertyHits = getOwlModel().getAnnotationPropertiesForDomainHits(classUri);
            for (ItemHits<OWLAnnotationProperty,OWLAnnotationPropertyDomainAxiom> hit: annotationPropertyHits) {
                Set<LocatedItem<OWLAnnotationPropertyDomainAxiom>> axioms = hit.getAxioms();
                for (LocatedItem<OWLAnnotationPropertyDomainAxiom> axiom: axioms) {
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
