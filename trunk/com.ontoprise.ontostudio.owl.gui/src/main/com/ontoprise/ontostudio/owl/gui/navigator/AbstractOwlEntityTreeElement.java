/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.navigator;

import org.eclipse.jface.preference.IPreferenceStore;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.navigator.ITreeDataProvider;
import org.neontoolkit.gui.navigator.elements.AbstractOntologyEntity;
import org.semanticweb.owlapi.model.OWLEntity;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;

/**
 * @author mer
 * 
 */
public abstract class AbstractOwlEntityTreeElement extends AbstractOntologyEntity {

    private String _uri;
    private OWLEntity _entity;

    public AbstractOwlEntityTreeElement(OWLEntity entity, String ontologyURI, String projectName, ITreeDataProvider provider) {
        super(projectName, ontologyURI, entity.getURI().toString(), provider);
        _entity = entity;
    }

    public AbstractOwlEntityTreeElement(String uri, String ontologyURI, String projectName, ITreeDataProvider provider) {
        super(projectName, ontologyURI, uri, provider);
        _uri = uri;
    }

    @Override
    public String getId() {
        if (_entity != null) {
            return _entity.getURI().toString();
        } else {
            return _uri;
        }
    }

    public OWLEntity getEntity() {
        return _entity;
    }
    
    @Override
    public boolean isImported() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String[] idArray;
        try {
            idArray = OWLGUIUtilities.getIdArray(_entity, getOntologyUri(), getProjectName());
        } catch (NeOnCoreException e) {
            idArray = new String[] {_entity.getURI().toString()};
        }
        String result = OWLGUIUtilities.getEntityLabel(idArray);
        IPreferenceStore store = NeOnUIPlugin.getDefault().getPreferenceStore();
        boolean DISPLAY_NB_INSTANCES = store.getBoolean(OWLPlugin.SHOW_NB_INSTANCES_PREFERENCE);
        if (DISPLAY_NB_INSTANCES) {
            // TODO: should find a nicer way to do that, so that the font changes... would require more time though
            int nbDInstances = OWLGUIUtilities.getNumberOfDirectInstances(_entity, getOntologyUri(), getProjectName());
            int nbIInstances = OWLGUIUtilities.getNumberOfInDirectInstances(_entity, getOntologyUri(), getProjectName());
            if (nbIInstances != 0)
                result+=" "+nbDInstances+"|"+nbIInstances;
        }
        return result;
    }
    
    /*
     * (non-Javadoc) NOTE: the values of the project variables are not required to be equivalent.
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.TreeElement#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AbstractOwlEntityTreeElement)) {
            return false;
        } else {
            AbstractOwlEntityTreeElement that = (AbstractOwlEntityTreeElement) o;
            boolean equal = false;
            if (_entity == null) {
                equal = that._entity == null; 
            } else {
                equal = equal(_entity.getURI().toString(), that._entity.getURI().toString());
            }
            return equal && equal(getOntologyUri(), that.getOntologyUri()) && equal(getProjectName(), that.getProjectName());
        }
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += _entity == null ? 0 : _entity.hashCode();
        hash += _uri == null ? 0 : _uri.hashCode();
        hash += getOntologyUri().hashCode();
        hash += getProjectName().hashCode();
        return hash;
    }

    /* (non-Javadoc) NOTE: not needed for OWL
     * @see org.neontoolkit.gui.navigator.elements.IEntityElement#getLocalName()
     */
    public String getLocalName() {
        return _uri;
    }
    
    /* (non-Javadoc) NOTE: not needed for OWL
     * @see org.neontoolkit.gui.navigator.elements.IEntityElement#getNamespace()
     */
    public String getNamespace() {
        return _uri;
    }
}
