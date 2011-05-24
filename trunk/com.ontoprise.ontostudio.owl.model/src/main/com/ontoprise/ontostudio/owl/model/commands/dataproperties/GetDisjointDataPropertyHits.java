/*****************************************************************************
 * written by the NeOn Technologies Foundation Ltd.
 * Based on the class GetDisjointDataPropertyHits with Copyright by the ontoprise GmbH.
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands.dataproperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;

import com.ontoprise.ontostudio.owl.model.ItemHits;
import com.ontoprise.ontostudio.owl.model.LocatedItem;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.OWLOntologyRequestCommand;

/**
 * @author Nico Stieler
 * 
 */
public class GetDisjointDataPropertyHits extends OWLOntologyRequestCommand {

    private List<String[]> _results;

    /**
     * @param project
     * @param module
     * @param arguments
     */
    public GetDisjointDataPropertyHits(String project, String module, String propertyUri) {
        super(project, module, propertyUri);
    }

    @Override
    protected void perform() throws CommandException {
        String propertyUri = (String) getArgument(2);
        _results = new ArrayList<String[]>();

        try {
            Set<ItemHits<OWLClassExpression,OWLDisjointDataPropertiesAxiom>> list = getOwlModel().getDisjointDataPropertyHits(propertyUri);
            for (ItemHits<OWLClassExpression,OWLDisjointDataPropertiesAxiom> hit: list) {
                Set<LocatedItem<OWLDisjointDataPropertiesAxiom>> axioms = hit.getAxioms();
                for (LocatedItem<OWLDisjointDataPropertiesAxiom> axiom: axioms) {
                    _results.add(new String[] {OWLUtilities.toString(axiom.getItem()), axiom.getOntologyURI()});
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
