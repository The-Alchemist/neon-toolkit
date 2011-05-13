/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.navigator.property.dataProperty;

import org.neontoolkit.gui.navigator.ITreeDataProvider;
import org.semanticweb.owlapi.model.OWLEntity;

import com.ontoprise.ontostudio.owl.gui.navigator.property.PropertyExtraDomainRangeinfoTreeElement;

/**
 * TreeElements used for object properties in the tree.
 * 
 * @author Nico Stieler
 */

public class DataPropertyTreeElement extends PropertyExtraDomainRangeinfoTreeElement {

    /**
     * @param elementId
     * @param providerID
     * @param isRoot
     * @param projectName
     * @param ontologyId
     */
    public DataPropertyTreeElement(OWLEntity entity, String ontologyUri, String projectId, ITreeDataProvider provider) {
        super(entity, ontologyUri, projectId, provider);
    }
    public DataPropertyTreeElement(String uri, String ontologyUri, String projectId, ITreeDataProvider provider) {
        super(uri, ontologyUri, projectId, provider);
    }
    /**
     * should only be used for Domain and Range View
     */
    public DataPropertyTreeElement(OWLEntity entity, String ontologyUri, String projectName, ITreeDataProvider provider, OWLEntity selectedEntity, String owlEntity) {
        super(entity, ontologyUri, projectName, provider, selectedEntity, owlEntity);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ProjectModuleEntity#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof DataPropertyTreeElement) {
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
