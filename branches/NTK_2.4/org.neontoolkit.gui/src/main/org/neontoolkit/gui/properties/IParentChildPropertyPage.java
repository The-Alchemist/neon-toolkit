/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.gui.properties;

/*
 * Created on 09.06.2008
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
/**
 * Just a temporarily existing interface for legacy purposes. 
 */
public interface IParentChildPropertyPage extends IEntityPropertyPage {

	void addChild(IEntityPropertyPage childPage);
	void setParent(IEntityPropertyPage parentPage);
}
