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

/* 
 * Created on: 09.06.2005
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Navigator
 */
/**
 * This interface defines the methods for entities with qualified identifiers.
 */

public interface IEntityElement extends IQualifiedIDElement {

	
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
