/*****************************************************************************
 * written by the NeOn Technologies Foundation Ltd.
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.util.forms;

import java.util.List;
import java.util.Set;

import org.neontoolkit.core.exception.NeOnCoreException;

import com.ontoprise.ontostudio.owl.gui.properties.IOWLPropertyPage;
import com.ontoprise.ontostudio.owl.gui.properties.LocatedAxiom;
import com.ontoprise.ontostudio.owl.model.OWLModel;

/**
 * @author Nico Stieler
 */
public abstract class DescriptionSetRowHandler extends AbstractRowHandler {

    private Set<String[]> _descriptionArrays;
    private List<LocatedAxiom> _axioms;

    public DescriptionSetRowHandler(IOWLPropertyPage page, OWLModel localOwlModel, OWLModel sourceOwlModel, Set<String[]> descriptionArrays, List<LocatedAxiom> axioms) {
        super(page, localOwlModel, sourceOwlModel);
        _descriptionArrays = descriptionArrays;
        _axioms = axioms;
    }
    
    @Override
    public void addPressed() {
        // empty implementation
    }

    @Override
    public abstract void savePressed();

    @Override
    public abstract void removePressed() throws NeOnCoreException;

    public List<LocatedAxiom> getAxioms() {
        return _axioms;
    }

    public Set<String[]> getDescriptionArrays() {
        return _descriptionArrays;
    }
}
