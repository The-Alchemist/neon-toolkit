/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.navigator.property.annotationProperty;

import org.neontoolkit.gui.navigator.ITreeDataProvider;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;

import com.ontoprise.ontostudio.owl.gui.navigator.property.PropertyExtraDomainRangeinfoTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.PropertyTreeElement;

/**
 * TreeElements used for object properties in the tree.
 */

public class AnnotationPropertyTreeElement extends PropertyExtraDomainRangeinfoTreeElement {

    public AnnotationPropertyTreeElement(OWLEntity entity, String ontologyUri, String projectName, ITreeDataProvider provider) {
        super(entity, ontologyUri, projectName, provider);
    }
    /**
     * should only be used for Domain and Range View
     */
    public AnnotationPropertyTreeElement(OWLEntity entity, String ontologyUri, String projectName, ITreeDataProvider provider, OWLEntity selectedClass, String owlClass) {
        super(entity, ontologyUri, projectName, provider, selectedClass, owlClass);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ProjectModuleEntity#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof AnnotationPropertyTreeElement) {
            return super.equals(o);
        } else {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.neontoolkit.gui.navigator.elements.AbstractOntologyEntity#getQName()
     */
    @Override
    public String getQName() {
        // TODO Auto-generated method stub
        return null;
    }
}
