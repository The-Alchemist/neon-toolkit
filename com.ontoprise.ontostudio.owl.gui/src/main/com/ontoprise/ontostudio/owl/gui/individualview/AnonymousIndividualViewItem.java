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

import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.navigator.elements.AbstractOntologyTreeElement;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;

/**
 * @author janiko
 * Created on: 05.10.2009
 */
public class AnonymousIndividualViewItem extends AbstractOntologyTreeElement implements IIndividualTreeElement<OWLAnonymousIndividual>{

    private IndividualItem<OWLAnonymousIndividual> _individualItem;
    /**
     * @param individual
     * @param clazzUri
     * @param ontologyUri
     * @param projectName
     */
    public AnonymousIndividualViewItem(OWLAnonymousIndividual individual, String clazzUri, String ontologyUri, String projectName) {
        super(projectName, ontologyUri, null);
        _individualItem = new IndividualItem<OWLAnonymousIndividual>(individual, clazzUri, ontologyUri, projectName);
    }

    
    
    /**
     * @return
     * @see com.ontoprise.ontostudio.owl.gui.individualview.IndividualItem#getClazz()
     */
    @Override
    public String getClazz() {
        return _individualItem.getClazz();
    }



    /**
     * @return
     * @see com.ontoprise.ontostudio.owl.gui.individualview.IndividualItem#getEditableValue()
     */
    public Object getEditableValue() {
        return _individualItem.getEditableValue();
    }



    /**
     * @return
     * @see com.ontoprise.ontostudio.owl.gui.individualview.IndividualItem#getId()
     */
    @Override
    public String getId() {
        return _individualItem.getId();
    }



    /**
     * @return
     * @see com.ontoprise.ontostudio.owl.gui.individualview.IndividualItem#getIndividual()
     */
    @Override
    public OWLAnonymousIndividual getIndividual() {
        return _individualItem.getIndividual();
    }



    /**
     * @return
     * @see com.ontoprise.ontostudio.owl.gui.individualview.IndividualItem#isDirect()
     */
    @Override
    public boolean isDirect() {
        return _individualItem.isDirect();
    }



    /**
     * @param s
     * @see com.ontoprise.ontostudio.owl.gui.individualview.IndividualItem#setIndividualId(java.lang.String)
     */
    @Override
    public void setIndividualId(String s) {
        _individualItem.setIndividualId(s);
    }



    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AnonymousIndividualViewItem)) {
            return false;
        } else {
            AnonymousIndividualViewItem that = (AnonymousIndividualViewItem) o;
            boolean equal = false;
            if (_individualItem == null) {
                equal = that._individualItem == null; 
            } else {
                try {
                    OWLOntology ontology = OWLModelFactory.getOWLModel(getOntologyUri(), getProjectName()).getOntology();
                    equal = equal(OWLUtilities.toString(_individualItem.getIndividual(), ontology), 
                            OWLUtilities.toString(that._individualItem.getIndividual(), ontology));
                } catch (NeOnCoreException e) {
                    // NICO how to handle this :true or false???
                    equal = false;
                }
            }
            return equal && equal(getOntologyUri(), that.getOntologyUri()) && equal(getProjectName(), that.getProjectName());
        }
    }
    
    @Override
    public String toString() {
        String[] idArray;
        try {
            idArray = OWLGUIUtilities.getIdArray(_individualItem.getIndividual(), getOntologyUri(), getProjectName());
        } catch (NeOnCoreException e) {
            idArray = new String[] {};
//            idArray = new String[] {OWLUtilities.toString(_individualItem.getIndividual())}; //NICO model is needed
        }
        return OWLGUIUtilities.getEntityLabel(idArray);
    }

}
