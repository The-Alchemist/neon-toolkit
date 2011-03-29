/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.gui.views.domainview;

import org.semanticweb.owlapi.model.OWLObject;

/**
 * Represents an item in the domain view, which can hold an ObjectProperty, a DataProperty 
 * or an AnnotationProperty.
 * 
 * @author Michael
 */
public class PropertyItem {

    private OWLObject _property;
    private boolean _isImported;

    protected PropertyItem(OWLObject property, boolean isImported) {
        _property = property;
        _isImported = isImported;
    }
    
    public OWLObject getProperty(){
        return _property;
    }

    public boolean isImported() {
        return _isImported;
    }
}
