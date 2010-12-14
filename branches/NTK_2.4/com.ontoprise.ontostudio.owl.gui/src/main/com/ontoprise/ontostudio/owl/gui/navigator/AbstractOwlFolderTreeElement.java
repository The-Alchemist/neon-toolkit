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

import org.neontoolkit.gui.navigator.ITreeDataProvider;
import org.neontoolkit.gui.navigator.elements.IFolderElement;
import org.neontoolkit.gui.navigator.elements.IOntologyElement;
import org.neontoolkit.gui.navigator.elements.IProjectElement;
import org.neontoolkit.gui.navigator.elements.TreeElement;

/**
 * @author mer
 * 
 */
public abstract class AbstractOwlFolderTreeElement extends TreeElement implements IProjectElement,IOntologyElement,IFolderElement {

    private String _folderName;
    private String _ontologyUri;
    private String _projectName;

    public AbstractOwlFolderTreeElement(String folderName, String projectName, String ontologyURI, ITreeDataProvider provider) {
        super(provider);
        _folderName = folderName;
        _ontologyUri = ontologyURI;
        _projectName = projectName;
    }

    public String getProjectName() {
        return _projectName;
    }

    public String getOntologyUri() {
        return _ontologyUri;
    }

    public void setOntologyUri(String ontologyUri) {
        _ontologyUri = ontologyUri;
    }

    public String getId() {
        return _folderName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.gui.navigator.TreeElement#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o.getClass() == getClass()) {
            AbstractOwlFolderTreeElement that = (AbstractOwlFolderTreeElement) o;
            return equal(_folderName, that._folderName) && equal(_projectName, that._projectName) && equal(_ontologyUri, that._ontologyUri);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hashcode = super.hashCode();
        hashcode += (_folderName != null) ? _folderName.hashCode() : 0;
        hashcode += (_projectName != null) ? _projectName.hashCode() : 0;
        return hashcode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return _folderName;
    }

    /**
     * folders are always local, not imported from another module
     */
    public boolean isImported() {
        return false;
    }

    public String getModuleId() {
        return _ontologyUri;
    }

    public void setModuleId(String moduleId) {
        _ontologyUri = moduleId;
    }

    public String getNamespace() {
        return _folderName;
    }

    public String getLocalName() {
        return _folderName;
    }
}
