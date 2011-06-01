/*****************************************************************************
 * written by the NeOn Technologies Foundation Ltd.
 * Based on the class EntityRowHandler with Copyright by the ontoprise GmbH.
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.util.forms;

import java.util.List;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLDataRange;

import com.ontoprise.ontostudio.owl.gui.properties.IOWLPropertyPage;
import com.ontoprise.ontostudio.owl.gui.properties.LocatedAxiom;
import com.ontoprise.ontostudio.owl.model.OWLModel;

/**
 * @author Nico Stieler
 */
public abstract class DataRangeRowHandler extends AbstractRowHandler {

    private OWLDataRange _dataRange;
    private List<LocatedAxiom> _axioms;

    public DataRangeRowHandler(IOWLPropertyPage page, OWLModel localOwlModel, OWLModel sourceOwlModel, OWLDataRange dataRange, List<LocatedAxiom> axioms) {
        super(page, localOwlModel, sourceOwlModel);
        _dataRange = dataRange;
        _axioms = axioms;
    }


    @Override
    public void addPressed() {
        // empty implementation
    }

    @Override
    public abstract void savePressed();

    @Override
    public abstract void removePressed() throws NeOnCoreException, CommandException;

    public List<LocatedAxiom> getAxioms() {
        return _axioms;
    }

    public OWLDataRange getEntity() {
        return _dataRange;
    }
    @Override
    public void editPressed() {
        // empty implementation
    }

}
