/*****************************************************************************
 * written by the NeOn Technologies Foundation Ltd.
 * Based on the class GetEquivalentObjectPropertyHits with Copyright by the ontoprise GmbH.
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands.datatypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;

import com.ontoprise.ontostudio.owl.model.ItemHits;
import com.ontoprise.ontostudio.owl.model.LocatedItem;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.OWLOntologyRequestCommand;

/**
 * @author Nico Stieler
 * 
 */
public class GetEquivalentDatatypeHits extends OWLOntologyRequestCommand {

    private List<String[]> _results;

    /**
     * @param project
     * @param module
     * @param arguments
     */
    public GetEquivalentDatatypeHits(String project, String module, String propertyUri) {
        super(project, module, propertyUri);
    }

    @Override
    protected void perform() throws CommandException {
        String datatypeUri = (String) getArgument(2);
        _results = new ArrayList<String[]>();

        try {
            Set<ItemHits<OWLDatatype,OWLDatatypeDefinitionAxiom>> list = getOwlModel().getEquivalentDatatypeHits(datatypeUri);
            for (ItemHits<OWLDatatype,OWLDatatypeDefinitionAxiom> hit: list) {
                Set<LocatedItem<OWLDatatypeDefinitionAxiom>> axioms = hit.getAxioms();
                for (LocatedItem<OWLDatatypeDefinitionAxiom> axiom: axioms) {
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
