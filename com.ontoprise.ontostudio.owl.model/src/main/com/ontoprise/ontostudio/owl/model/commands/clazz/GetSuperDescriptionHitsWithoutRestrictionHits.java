/*****************************************************************************
 * based on com.ontoprise.ontostudio.owl.model.commands.clazz.GetSuperDescriptionHits developed by ontoprise GmbH
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands.clazz;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import com.ontoprise.ontostudio.owl.model.ItemHits;
import com.ontoprise.ontostudio.owl.model.LocatedItem;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.OWLOntologyRequestCommand;

/**
 * @author Nico Stieler
 * 
 */
public class GetSuperDescriptionHitsWithoutRestrictionHits extends OWLOntologyRequestCommand {

    List<String[]> _results;

    /**
     * @param project
     * @param module
     * @param arguments
     */
    public GetSuperDescriptionHitsWithoutRestrictionHits(String project, String module, String subClazzUri) {
        super(project, module, subClazzUri);
    }

    @Override
    public void perform() throws CommandException {
        _results = new ArrayList<String[]>();
        String subClazzUri = (String) getArgument(2);
        try {
            Set<ItemHits<OWLClassExpression,OWLSubClassOfAxiom>> superDescriptionHitsWithoutRestrictionHits = getOwlModel().getSuperDescriptionHitsWithoutRestrictionHits(subClazzUri);
            for (ItemHits<OWLClassExpression,OWLSubClassOfAxiom> hit: superDescriptionHitsWithoutRestrictionHits) {
                Set<LocatedItem<OWLSubClassOfAxiom>> axioms = hit.getAxioms();
                for (LocatedItem<OWLSubClassOfAxiom> axiom: axioms) {
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
