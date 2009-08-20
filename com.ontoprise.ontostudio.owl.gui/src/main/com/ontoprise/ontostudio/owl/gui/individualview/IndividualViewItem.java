/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.individualview;

import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import com.ontoprise.ontostudio.owl.gui.navigator.AbstractOwlEntityTreeElement;


public class IndividualViewItem extends AbstractOwlEntityTreeElement {

    private String _uri;
    private String _clazz;

    public IndividualViewItem(OWLIndividual individual, String clazzUri, String ontologyUri, String projectName) {
        super((OWLEntity)individual, ontologyUri, projectName, null);
        _uri = ((OWLNamedIndividual)individual).getURI().toString();
        _clazz = clazzUri;
    }

    public void setIndividualId(String s) {
        _uri = s;
    }

    @Override
    public String getId() {
        return _uri;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
     */
    public Object getEditableValue() {
        return this;
    }

    // TODO implement
    public boolean isDirect() {
        return true;
    }

    public String getClazz() {
        return _clazz;
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
