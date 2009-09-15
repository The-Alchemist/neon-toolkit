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

import java.util.List;

import org.neontoolkit.core.exception.NeOnCoreException;

import com.ontoprise.ontostudio.owl.gui.properties.IOWLPropertyPage;
import com.ontoprise.ontostudio.owl.gui.properties.LocatedAxiom;
import com.ontoprise.ontostudio.owl.model.OWLModel;

public abstract class DescriptionRowHandler extends AbstractRowHandler {

    private String[] _descriptionArray;
    private List<LocatedAxiom> _axioms;

    public DescriptionRowHandler(IOWLPropertyPage page, OWLModel owlModel, String[] descriptionArray, List<LocatedAxiom> axioms) {
        super(page, owlModel);
        _descriptionArray = descriptionArray;
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

    public String[] getDescriptionArray() {
        return _descriptionArray;
    }
}
