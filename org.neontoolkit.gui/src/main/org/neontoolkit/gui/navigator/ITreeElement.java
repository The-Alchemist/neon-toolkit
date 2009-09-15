/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

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
