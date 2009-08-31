/**
 * Copyright (c) 2008 ontoprise GmbH.
 */

package org.neontoolkit.gui.navigator.elements;

import org.neontoolkit.gui.navigator.ITreeDataProvider;

/*
 * Created on 02.10.2008
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
/**
 * This is an abstract superclass for all tree elements that either
 * represent an ontology in the tree or represent an element that is
 * contained in an ontology.
 */
public class AbstractOntologyTreeElement extends
		AbstractProjectTreeElement implements IOntologyElement {

	private String _ontologyUri;
	private boolean _isImported = false;
	
	/**
	 * 
	 */
	public AbstractOntologyTreeElement(String project, String ontologyURI, ITreeDataProvider provider) {
		super(project, provider);
		_ontologyUri = ontologyURI;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.neontoolkit.gui.navigator.elements.IOntologyElement#getOntologyUri()
	 */
    public String getOntologyUri() {
    	return _ontologyUri;
    }

    /* (non-Javadoc)
     * @see org.neontoolkit.gui.navigator.elements.IOntologyElement#isImported()
     */
    public boolean isImported() {
		return _isImported;
	}
	
	public void setIsImported(boolean isImported) {
		_isImported = isImported;
	}

}
