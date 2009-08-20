/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.navigator.clazz;

import org.neontoolkit.gui.navigator.ITreeDataProvider;
import org.semanticweb.owlapi.model.OWLEntity;

import com.ontoprise.ontostudio.owl.gui.navigator.AbstractOwlEntityTreeElement;

/**
 * TreeElements used for classes in the tree.
 */

public class ClazzTreeElement extends AbstractOwlEntityTreeElement {

    public ClazzTreeElement(OWLEntity entity, String ontologyUri, String projectId, ITreeDataProvider provider) {
        super(entity, ontologyUri, projectId, provider);
    }

    public ClazzTreeElement(String uri, String ontologyUri, String projectId, ITreeDataProvider provider) {
        super(uri, ontologyUri, projectId, provider);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.ProjectModuleEntity#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof ClazzTreeElement) {
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
