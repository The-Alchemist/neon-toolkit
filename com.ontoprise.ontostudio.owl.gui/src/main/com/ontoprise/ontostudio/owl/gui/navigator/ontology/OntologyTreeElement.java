/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.navigator.ontology;

import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.history.IOWLHistoryEntry;
import org.neontoolkit.gui.navigator.ITreeDataProvider;
import org.neontoolkit.gui.navigator.elements.AbstractOntologyTreeElement;
import org.neontoolkit.gui.navigator.ontology.IOntologyTreeElement;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.history.OWLOntologyHistoryEntry;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLNamespaces;

/**
 * TreeElement used for modules shown in the OntologyNavigator
 * @author Nico Stieler
 */
public class OntologyTreeElement extends AbstractOntologyTreeElement implements IOntologyTreeElement {

    private IOWLHistoryEntry _historyEntry;
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
        if(arg0 != null){
            if (res && arg0.getClass() == getClass()) {
                OntologyTreeElement that = (OntologyTreeElement) arg0;
                res = _dataProvider == that._dataProvider && equal(getOntologyUri(), that.getOntologyUri()) && equal(getProjectName(), that.getProjectName());
            } else {
                if(arg0.getClass() == getClass()){ //needed for jump from search result to ontology 
                    OntologyTreeElement that = (OntologyTreeElement) arg0;
                    res = equal(getOntologyUri(), that.getOntologyUri()) && equal(getProjectName(), that.getProjectName());
                }else{
                    res = false;
                }
            }
        }
        return res;
    }

    /*
     * (non-Javadoc) NOTICE: not needed for OWL
     * @see org.neontoolkit.gui.navigator.elements.IEntityElement#getLocalName()
     */
    @Override
    public String getNamespace() {
        return getId();
    }

    @Override
    public String getId() {
        return getOntologyUri();
    }
    @Override
    public String toString() {
        int idDisplayStyle = NeOnUIPlugin.getDefault().getIdDisplayStyle();
        String result = getOntologyUri();
        if (idDisplayStyle == NeOnUIPlugin.DISPLAY_LOCAL) {
            result = OWLNamespaces.guessLocalName(result);
            if (result.length() == 0) {
                result = getOntologyUri();
            }
        }else if (idDisplayStyle == OWLPlugin.DISPLAY_LANGUAGE){
            try {
                String[] idArray = OWLGUIUtilities.getIdArray(OWLModelFactory.getOWLModel(getOntologyUri(), getProjectName()).getOntology(), getOntologyUri(), getProjectName());
                if(idArray.length > 3)
                    result = idArray[3];
            } catch (NeOnCoreException e) {
                // nothing to do
            }
        }
//        else if(idDisplayStyle == NeOnUIPlugin.)
        return result;
    }
    /*
     * (non-Javadoc) NOTICE: not needed for OWL
     * @see org.neontoolkit.gui.navigator.elements.IEntityElement#getLocalName()
     */
    @Override
    public String getLocalName() {
        return getId();
    }
    @Override
    public IOWLHistoryEntry getOWLHistoryEntry() {
        if(_historyEntry == null){
            _historyEntry = new OWLOntologyHistoryEntry(this);
        }
        return _historyEntry;
    }
}
