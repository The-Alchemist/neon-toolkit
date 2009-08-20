/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.navigator.ontology;

import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.navigator.ITreeDataProvider;
import org.neontoolkit.gui.navigator.elements.AbstractOntologyTreeElement;
import org.neontoolkit.gui.navigator.ontology.IOntologyTreeElement;

import com.ontoprise.ontostudio.owl.model.OWLNamespaces;

/**
 * TreeElement used for modules shown in the OntologyNavigator
 */
public class OntologyTreeElement extends AbstractOntologyTreeElement implements IOntologyTreeElement {

    public OntologyTreeElement(String projectName, String ontologyURI, ITreeDataProvider provider) {
        super(projectName, ontologyURI, provider);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object arg0) {
        boolean res = super.equals(arg0);
        if (res && arg0.getClass() == getClass()) {
            OntologyTreeElement that = (OntologyTreeElement) arg0;
            res = _dataProvider == that._dataProvider && equal(getOntologyUri(), that.getOntologyUri()) && equal(getProjectName(), that.getProjectName());
        } else {
            res = false;
        }
        return res;
    }

    /*
     * (non-Javadoc) NOTICE: not needed for OWL
     * @see org.neontoolkit.gui.navigator.elements.IEntityElement#getLocalName()
     */
    public String getNamespace() {
        return getId();
    }

    public String getId() {
        return getOntologyUri();
    }

    @Override
    public String toString() {
        int idDisplayStyle = NeOnUIPlugin.getDefault().getIdDisplayStyle();
        String result = getOntologyUri();
        if (idDisplayStyle == NeOnUIPlugin.DISPLAY_LOCAL) {
            result = OWLNamespaces.guessLocalName(result);
        }
        return result;
    }

    /*
     * (non-Javadoc) NOTICE: not needed for OWL
     * @see org.neontoolkit.gui.navigator.elements.IEntityElement#getLocalName()
     */
    public String getLocalName() {
        return getId();
    }
}
