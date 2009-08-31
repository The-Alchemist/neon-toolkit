/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.gui.navigator;

/* 
 * Created on: 27.12.2004
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Navigator
 */
/**
 * Basic interface for elements provided by the ITreeDataProviders
 */

public interface ITreeElement {
	/**
	 * Returns the id of the provider this tree element was generated with.
	 * @return
	 */
	public ITreeDataProvider getProvider();
}
