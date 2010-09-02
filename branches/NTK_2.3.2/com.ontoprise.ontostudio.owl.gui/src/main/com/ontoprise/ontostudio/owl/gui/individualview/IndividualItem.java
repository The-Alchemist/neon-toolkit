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

import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import com.ontoprise.ontostudio.owl.model.OWLUtilities;

/**
 * @author janiko
 * Created on: 07.10.2009
 */
public class IndividualItem<T extends OWLIndividual>{

    T _individual;
    private String _id;
    private String _clazz;

    protected IndividualItem(T individual, String clazzUri, String ontologyUri, String projectName) {

        _id = OWLUtilities.toString(individual);
        _individual = individual;
        _clazz = clazzUri;
    }
    
    public static IIndividualTreeElement<?> createNewInstance(OWLIndividual individual, String clazzUri, String ontologyUri, String projectName){
        if(individual instanceof OWLNamedIndividual) return new NamedIndividualViewItem((OWLNamedIndividual)individual, clazzUri, ontologyUri, projectName);
        else return new AnonymousIndividualViewItem((OWLAnonymousIndividual)individual, clazzUri, ontologyUri, projectName);
    }

    public void setIndividualId(String s) {
        _id = s;
    }

    public T getIndividual(){
        return _individual;
    }

    public String getId() {
        return _id;
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

//    //Not needed for OWL
//    @Override
//    public String getLocalName() {
//       return getId();
//    }
//    
//    //Not needed for OWL
//    @Override
//    public String getNamespace() {
//        return getId();
//    }
    

    

}
