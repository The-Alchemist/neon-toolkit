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

import org.eclipse.core.resources.IProject;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.navigator.ITreeDataProvider;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import com.ontoprise.ontostudio.owl.gui.navigator.AbstractOwlEntityTreeElement;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;

/**
 * @author janiko
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
        super((OWLEntity)individual, ontologyUri, projectName, null);
        _individual = new IndividualItem<OWLNamedIndividual>(individual, clazzUri, ontologyUri, projectName);
    }
    /**
     * @return
     * @see com.ontoprise.ontostudio.owl.gui.individualview.IndividualItem#getClazz()
     */
    public String getClazz() {
        return _individual.getClazz();
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
    public boolean isDirect() {
        return _individual.isDirect();
    }
    /**
     * @param s
     * @see com.ontoprise.ontostudio.owl.gui.individualview.IndividualItem#setIndividualId(java.lang.String)
     */
    public void setIndividualId(String s) {
        _individual.setIndividualId(s);
    }
    @Override
    public String getQName() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public OWLNamedIndividual getIndividual() {
        return _individual.getIndividual();
    }
  
 
    
}
