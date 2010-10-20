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

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLEntity;

import com.ontoprise.ontostudio.owl.gui.properties.IOWLPropertyPage;
import com.ontoprise.ontostudio.owl.gui.properties.LocatedAxiom;
import com.ontoprise.ontostudio.owl.model.OWLModel;

public abstract class EntityRowHandler extends AbstractRowHandler {

    private OWLEntity _entity;
    private List<LocatedAxiom> _axioms;

    public EntityRowHandler(IOWLPropertyPage page, OWLModel localOwlModel, OWLModel sourceOwlModel, OWLEntity entity, List<LocatedAxiom> axioms) {
        super(page, localOwlModel, sourceOwlModel);
        _entity = entity;
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

    public OWLEntity getEntity() {
        return _entity;
    }

}
