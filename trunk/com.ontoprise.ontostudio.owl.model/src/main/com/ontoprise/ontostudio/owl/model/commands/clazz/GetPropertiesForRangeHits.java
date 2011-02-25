/*****************************************************************************
 * based on com.ontoprise.ontostudio.owl.gui.domainview.DomainView developed by ontoprise GmbH
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands.clazz;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import com.ontoprise.ontostudio.owl.model.ItemHits;
import com.ontoprise.ontostudio.owl.model.LocatedItem;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.OWLOntologyRequestCommand;

/**
 * @author Nico Stieler
 * 
 * Returns the properties that have a given class as their range.
 */
public class GetPropertiesForRangeHits extends OWLOntologyRequestCommand {

    List<String[]> _results;

    /**
     * @param project
     * @param module
     * @param arguments
     */
    public GetPropertiesForRangeHits(String project, String module, String classUri) {
        super(project, module, classUri);
    }

    @Override
    public void perform() throws CommandException {
        _results = new ArrayList<String[]>();
        String classUri = (String) getArgument(2);
        try {
            OWLOntology ontology = getOwlModel().getOntology();
            Set<ItemHits<OWLDataProperty,OWLDataPropertyRangeAxiom>> dataPropertyHits = getOwlModel().getDataPropertiesForRangeHits(classUri);
            for (ItemHits<OWLDataProperty,OWLDataPropertyRangeAxiom> hit: dataPropertyHits) {
                Set<LocatedItem<OWLDataPropertyRangeAxiom>> axioms = hit.getAxioms();
                for (LocatedItem<OWLDataPropertyRangeAxiom> axiom: axioms) {
                    String[] result = new String[]{OWLUtilities.toString(axiom.getItem(), ontology), axiom.getOntologyURI()};
                    _results.add(result);
                }
            }

            Set<ItemHits<OWLObjectProperty,OWLObjectPropertyRangeAxiom>> objectPropertyHits = getOwlModel().getObjectPropertiesForRangeHits(classUri);
            for (ItemHits<OWLObjectProperty,OWLObjectPropertyRangeAxiom> hit: objectPropertyHits) {
                Set<LocatedItem<OWLObjectPropertyRangeAxiom>> axioms = hit.getAxioms();
                for (LocatedItem<OWLObjectPropertyRangeAxiom> axiom: axioms) {
                    String[] result = new String[]{OWLUtilities.toString(axiom.getItem(), ontology), axiom.getOntologyURI()};
                    _results.add(result);
                }
            }

            Set<ItemHits<OWLAnnotationProperty,OWLAnnotationPropertyRangeAxiom>> annotationPropertyHits = getOwlModel().getAnnotationPropertiesForRangeHits(classUri);
            for (ItemHits<OWLAnnotationProperty,OWLAnnotationPropertyRangeAxiom> hit: annotationPropertyHits) {
                Set<LocatedItem<OWLAnnotationPropertyRangeAxiom>> axioms = hit.getAxioms();
                for (LocatedItem<OWLAnnotationPropertyRangeAxiom> axiom: axioms) {
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
