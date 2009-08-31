/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.gui.navigator.elements;

/* 
 * Created on: 09.06.2005
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Navigator
 */
/**
 * This interface defines the methods for entities with qualified identifiers.
 */

public interface IEntityElement {
	/**
	 * Returns the fully qualified identifier
	 * @return qualified identifier
	 */
	String getId();
	
	/**
	 * Returns the local part of the identifier
	 * @return local part
	 */
	String getLocalName();
	
	/**
	 * Returns the namespace of the identifier.
	 * @return the namespace
	 */
	String getNamespace();
}
