/*****************************************************************************
 * written by the NeOn Technology Foundation Ltd.
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands.clazz;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;

import com.ontoprise.ontostudio.owl.model.ItemHits;
import com.ontoprise.ontostudio.owl.model.LocatedItem;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.OWLOntologyRequestCommand;

/**
 * @author Nico Stieler
 *
 */
public class GetDisjointUnionHits extends OWLOntologyRequestCommand {

    List<String[]> _results;
    
    /**
     * @param project
     * @param module
     * @param arguments
     */
    public GetDisjointUnionHits(String project, String module, String clazzUri) {
        super(project, module, clazzUri);
    }

    @Override
    protected void perform() throws CommandException {
        _results = new ArrayList<String[]>();
        String clazzUri = (String) getArgument(2);

        try {
            
            Set<ItemHits<OWLClassExpression,OWLDisjointUnionAxiom>> disjointUnionHits = getOwlModel().getDisjointUnionHits(clazzUri);
            for (ItemHits<OWLClassExpression,OWLDisjointUnionAxiom> hit: disjointUnionHits) {
                Set<LocatedItem<OWLDisjointUnionAxiom>> axioms = hit.getAxioms();
                for (LocatedItem<OWLDisjointUnionAxiom> axiom: axioms) {
                    if(OWLUtilities.toString(axiom.getItem().getOWLClass()).contains(OWLUtilities.toString(OWLUtilities.description(clazzUri)))){
                        String[] result = new String[]{OWLUtilities.toString(axiom.getItem()), axiom.getOntologyURI()};
                        _results.add(result);
                    }
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
