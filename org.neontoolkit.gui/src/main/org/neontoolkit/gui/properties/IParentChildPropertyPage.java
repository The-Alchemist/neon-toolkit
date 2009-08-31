/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

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
