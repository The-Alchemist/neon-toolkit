/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 21.01.2009
 * Created by: werner
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.gui.properties;

import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * @author werner
 * 
 */
public class LocatedAxiom {

    private OWLAxiom _axiom;
    private boolean _isLocal;

    public LocatedAxiom(OWLAxiom axiom, boolean isLocal) {
        _axiom = axiom;
        _isLocal = isLocal;
    }

    public boolean isLocal() {
        return _isLocal;
    }

    public OWLAxiom getAxiom() {
        return _axiom;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        LocatedAxiom that = (LocatedAxiom)obj;
        return _isLocal == that._isLocal && _axiom.equals(that._axiom);
    }

    @Override
    public int hashCode() {
        int hash = isLocal() ? 1 : 0;
        hash += _axiom.hashCode();
        return hash;
    }
}
