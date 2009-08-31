/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.gui.navigator.elements;

/*
 * Created on 30.06.2008
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
/**
 * This interface defines the methods for intities belonging to an ontology.
 */
public interface IOntologyElement {
    /**
     * Returns the ontology URI of the ontology this element belongs to
     * @return module identifier 
     */
    String getOntologyUri() ;

	/**
	 * 
	 * @return true if the element is an element imported from another ontology
	 */
	public boolean isImported();

}
