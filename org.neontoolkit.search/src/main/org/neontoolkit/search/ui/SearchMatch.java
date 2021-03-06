/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.search.ui;

import org.eclipse.swt.graphics.Image;

/* 
 * Created on: 23.03.2006
 * Created by: Dirk Wenke
 *
 * Function:
 * Keywords:
 *
 */
/**
 * @author Dirk Wenke
 * @author Nico Stieler
 * 
 */

public abstract class SearchMatch{
	private Object _match;

	public SearchMatch(Object match) {
		_match = match;
	}
	
	public Object getMatch() {
		return _match;
	}

	public abstract int getOccurenceCount();
	
	public abstract void show(int index);
	
	public abstract Image getImage();
	
	public abstract void setFocus();
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return _match.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SearchMatch) {
			return _match.equals(((SearchMatch)obj)._match);
		}
		return false;
	}
}
