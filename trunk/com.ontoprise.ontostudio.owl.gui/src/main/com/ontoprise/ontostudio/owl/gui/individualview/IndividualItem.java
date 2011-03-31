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

import org.neontoolkit.core.util.IRIUtils;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import com.ontoprise.ontostudio.owl.model.OWLUtilities;

/**
 * @author janiko
 * @author Nico Stieler
 * Created on: 07.10.2009
 */
public class IndividualItem<T extends OWLIndividual>{

    T _individual;
    private String _id;
    private String[] _clazzUris;
    private String _currentClazz;
    private boolean _direct;

    protected IndividualItem(T individual, String clazzUri, String ontologyUri, String projectName){
        this(individual, clazzUri, new String[]{clazzUri}, ontologyUri, projectName, true);
    }
    protected IndividualItem(T individual, String currentClazzUri, String[] clazzUris, String ontologyUri, String projectName, boolean direct){
        _id = IRIUtils.ensureValidIdentifierSyntax(OWLUtilities.toString(individual));
        _individual = individual;
        _clazzUris = clazzUris;
        _currentClazz = currentClazzUri;
        _direct = direct;
    }

    public static IIndividualTreeElement<?> createNewInstance(OWLIndividual individual, String clazzUri, String ontologyUri, String projectName){
        return createNewInstance(individual, clazzUri, new String[]{clazzUri}, ontologyUri, projectName, true);
    }
    public static IIndividualTreeElement<?> createNewInstance(OWLIndividual individual, String currentClazzUri, String[] clazzUris, String ontologyUri, String projectName, boolean direct){
        if(individual instanceof OWLNamedIndividual) { 
            return new NamedIndividualViewItem((OWLNamedIndividual)individual, currentClazzUri, clazzUris, ontologyUri, projectName, direct);
        } else { 
            return new AnonymousIndividualViewItem((OWLAnonymousIndividual)individual, currentClazzUri, clazzUris, ontologyUri, projectName, direct);
        }
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

    public boolean isDirect() {
        return _direct;
    }

    public String[] getClazzUris() {
        return _clazzUris;
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
    /**
     * @return the _currentClazz
     */
    public String getCurrentClazz() {
        return _currentClazz;
    }
    

}
