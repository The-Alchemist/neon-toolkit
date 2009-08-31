/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/
package org.neontoolkit.gui.navigator.elements;

import org.neontoolkit.gui.navigator.ITreeDataProvider;

/* 
 * Created on: 13.06.2005
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Navigator
 */
/**
 * Superclass for folders belonging to a module and a project.
 */
public class AbstractProjectOntologyFolder extends AbstractOntologyTreeElement implements
		IOntologyElement, IEntityElement, IFolderElement {

	private String _folderName;

	public AbstractProjectOntologyFolder(String project, String ontologyURI, String folderName, ITreeDataProvider provider) {
		super(project, ontologyURI, provider);
		_folderName = folderName;
	}
	
	public String getId() {
		return _folderName;
	}

	public String getLocalName() {
		return _folderName;
	}

	public String getNamespace() {
		return _folderName;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o.getClass() == getClass()) {
			AbstractProjectOntologyFolder that = (AbstractProjectOntologyFolder) o;
			return equal(_folderName, that._folderName)
				&& equal(getOntologyUri(), that.getOntologyUri())
				&& equal(getProjectName(), that.getProjectName());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int hashcode = super.hashCode();
		hashcode += (_folderName != null) ? _folderName.hashCode() : 0;
		hashcode += (getOntologyUri() != null) ? getOntologyUri().hashCode() : 0;
		hashcode += (getProjectName() != null) ? getProjectName().hashCode() : 0;
		return hashcode;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return _folderName;
	}
    
    /**
     * folders are always local, not imported from another ontology
     */
    @Override
	public boolean isImported() {
    	return false;
    }

}
