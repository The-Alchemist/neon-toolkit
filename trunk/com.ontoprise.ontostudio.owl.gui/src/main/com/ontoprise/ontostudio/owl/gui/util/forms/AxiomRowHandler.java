/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.util.forms;

import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;

import com.ontoprise.ontostudio.owl.gui.properties.IOWLPropertyPage;
import com.ontoprise.ontostudio.owl.gui.properties.LocatedAxiom;
import com.ontoprise.ontostudio.owl.model.OWLModel;

/**
 * Provides methods that are called when buttons in a FormRow are pressed.
 * 
 * @author werner
 * @author Nico Stieler
 * 
 */
public abstract class AxiomRowHandler extends AbstractRowHandler {

    private LocatedAxiom _axiom;

    public AxiomRowHandler(IOWLPropertyPage page, OWLModel localOwlModel, OWLModel sourceOwlModel, LocatedAxiom axiom) {
        super(page, localOwlModel, sourceOwlModel);
        _axiom = axiom;
    }

    /**
     * Removes the stored axiom.
     * 
     * @throws NeOnCoreException
     * @throws OWLOntologyChangeException 
     */
    @Override
    public void removePressed() throws NeOnCoreException {
        remove();
        _propertyPage.refresh();
    }
    
    /**
     * Removes the old axiom stored for this row.
     * @throws NeOnCoreException
     * @throws OWLOntologyChangeException 
     */
    public void remove() throws NeOnCoreException {
        if (_axiom.isLocal()) {
            _sourceOwlModel.removeAxiom(_axiom.getAxiom());
        }else{
            removeImported();
        }
    }

    /**
     * Removes the axiom stored for this row from the source ontology in case its imported.
     * @throws NeOnCoreException 
     */
    private void removeImported() throws NeOnCoreException {
        _sourceOwlModel.removeAxiom(_axiom.getAxiom());
    }

    public OWLAxiom getAxiom() {
        return _axiom.getAxiom();
    }

    @Override
    public void addPressed() {
        // empty implementation
    }
}
