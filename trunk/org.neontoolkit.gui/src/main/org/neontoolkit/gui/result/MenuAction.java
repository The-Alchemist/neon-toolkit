/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.gui.result;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.custom.CTabItem;

/*
 * Created on 14.10.2008
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
public abstract class MenuAction extends Action {
	private CTabItem _tabItem;
	
	public MenuAction(String text, ImageDescriptor image, CTabItem tabItem) {
		super(text, image);
		_tabItem = tabItem;
	}

	public CTabItem getTabItem() {
		return _tabItem;
	}
}
