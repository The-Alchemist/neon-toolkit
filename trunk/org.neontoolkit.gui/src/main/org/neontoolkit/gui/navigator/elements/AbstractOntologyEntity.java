/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.gui.navigator.elements;

import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.navigator.ITreeDataProvider;

/* 
 * Created on: 09.06.2005
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Navigator
 */
/**
 * abstract superclass for elements belonging to an ontology and a project.
 */
public abstract class AbstractOntologyEntity extends AbstractOntologyTreeElement implements 
	IEntityElement, IOntologyElement {
	
    protected String _entityUri;

	/**
	 * Simple constructor with only the provider as parameter. Ontology uri and project id have to be set
	 * later by the setter methods.
	 * @param provider
	 */
	public AbstractOntologyEntity(String projectName, String ontologyUri, String entityUri, ITreeDataProvider provider) {
		super(projectName, ontologyUri, provider);
		_entityUri = entityUri;
	}
    /*
     * (non-Javadoc)
     * @see org.neontoolkit.gui.navigator.elements.IEntityElement#getId()
     */
	@Override
    public String getId() {
	    return _entityUri;
	}
	/*
	 * (non-Javadoc)
	 * @see org.neontoolkit.gui.navigator.elements.TreeElement#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof AbstractOntologyEntity)) {
			return false;
		} else {
			AbstractOntologyEntity that = (AbstractOntologyEntity) o;
			return equal(_entityUri, that._entityUri)
				&& equal(getOntologyUri(), that.getOntologyUri())
				&& equal(getProjectName(), that.getProjectName());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.neontoolkit.gui.navigator.elements.TreeElement#hashCode()
	 */
	@Override
	public int hashCode() {
		int hashcode = 0;
		hashcode += (_entityUri != null) ? _entityUri.hashCode() : 0;
		hashcode += (getOntologyUri() != null) ? getOntologyUri().hashCode() : 0;
		hashcode += (getProjectName() != null) ? getProjectName().hashCode() : 0;
		return hashcode;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.neontoolkit.gui.navigator.elements.AbstractProjectTreeElement#toString()
	 */
	@Override
	public String toString() {
		return toString(NeOnUIPlugin.getDefault().getIdDisplayStyle());
	}
	
	protected String toString(int idStyle) {
		switch (idStyle) {
		case NeOnUIPlugin.DISPLAY_LOCAL:
			return getLocalName();
		default:
            return getId();
		}
	}
	
	public abstract String getQName(); 
}
