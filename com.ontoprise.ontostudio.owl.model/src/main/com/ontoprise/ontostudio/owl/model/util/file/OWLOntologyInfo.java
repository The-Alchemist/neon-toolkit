/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 19.06.2009
 * Created by: krekeler
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.model.util.file;

import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyID;

/**
 * @author krekeler
 *
 */
public class OWLOntologyInfo {
    private OWLOntologyID _id;
    private OWLOntologyFormat _format;
    public OWLOntologyInfo(OWLOntologyID id, OWLOntologyFormat format) {
        _id = id;
        _format = format;
    }
    public OWLOntologyID getOntologyID() {
        return _id;
    }
    public OWLOntologyFormat getOntologyFormat() {
        return _format;
    }
}
