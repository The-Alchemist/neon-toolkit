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
package com.ontoprise.ontostudio.owl.gui.individualview;

import org.neontoolkit.gui.navigator.elements.IIndividualTreeElement;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import com.ontoprise.ontostudio.owl.gui.navigator.AbstractOwlEntityTreeElement;

/**
 * @author janiko
 * @author Nico Stieler
 * Created on: 05.10.2009
 */
public class NamedIndividualViewItem extends AbstractOwlEntityTreeElement implements IIndividualTreeElement<OWLNamedIndividual>{

    private IndividualItem<OWLNamedIndividual> _individual;
    
    /**
     * @param individual
     * @param clazzUri
     * @param ontologyUri
     * @param projectName
     */
    public NamedIndividualViewItem(OWLNamedIndividual individual, String clazzUri, String ontologyUri, String projectName) {
        this(individual, clazzUri, new String[]{clazzUri}, ontologyUri, projectName, false, true);
    }
    /**
     * @param individual
     * @param currentClazzUri 
     * @param linkedList
     * @param ontologyUri
     * @param projectName
     * @param direct
     */
    public NamedIndividualViewItem(OWLNamedIndividual individual, String currentClazzUri, String[] clazzUris, String ontologyUri, String projectName, boolean isImported, boolean isDirect) {
        super((OWLEntity)individual, ontologyUri, projectName, null);
        setIsImported(isImported);
        _individual = new IndividualItem<OWLNamedIndividual>(individual, currentClazzUri, clazzUris, ontologyUri, projectName, isDirect);
    }
    /**
     * @return
     * @see com.ontoprise.ontostudio.owl.gui.individualview.IndividualItem#getClazz()
     */
    @Override
    public String[] getClazzUris() {
        return _individual.getClazzUris();
    }
    /**
     * @return
     * @see com.ontoprise.ontostudio.owl.gui.individualview.IndividualItem#getEditableValue()
     */
    public Object getEditableValue() {
        return _individual.getEditableValue();
    }
    /**
     * @return
     * @see com.ontoprise.ontostudio.owl.gui.individualview.IndividualItem#getId()
     */
    @Override
    public String getId() {
        return _individual.getId();
    }
    /**
     * @return
     * @see com.ontoprise.ontostudio.owl.gui.individualview.IndividualItem#isDirect()
     */
    @Override
    public boolean isDirect() {
        return _individual.isDirect();
    }
    /**
     * @param s
     * @see com.ontoprise.ontostudio.owl.gui.individualview.IndividualItem#setIndividualId(java.lang.String)
     */
    @Override
    public void setIndividualId(String s) {
        _individual.setIndividualId(s);
    }
    @Override
    public String getQName() {
        return null;
    }
    @Override
    public OWLNamedIndividual getIndividual() {
        return _individual.getIndividual();
    }
    @Override
    public String toString() {
        String string = super.toString();
        return string;
    } 
    @Override
    public String getCurrentClazz() {
        String[] uris = getClazzUris();
        return uris[uris.length - 1];
    }
}
